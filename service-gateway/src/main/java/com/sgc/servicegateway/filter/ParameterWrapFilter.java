package com.sgc.servicegateway.filter;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sgc.servicegateway.dao.UserDao;

import io.netty.buffer.ByteBufAllocator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public class ParameterWrapFilter implements GlobalFilter, Ordered {

	@Autowired
	UserDao userDao;
	
	@Override
	public int getOrder() {

		return Ordered.HIGHEST_PRECEDENCE;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

		ServerHttpRequest serverHttpRequest = exchange.getRequest();
		HttpMethod method = serverHttpRequest.getMethod();
		URI uri = serverHttpRequest.getURI();
		HttpHeaders httpHeaders = serverHttpRequest.getHeaders();
		
		String token = httpHeaders.getFirst("Authorization");
		
		// 鉴权
		if (isAuth()) {
			ServerHttpResponse response = exchange.getResponse();
	        Map<String,Object> data = new HashMap<String,Object>();
	        data.put("code", "401");
	        data.put("message","非法请求");
	        byte[] datas = JSON.toJSONString(data).getBytes(StandardCharsets.UTF_8);
	        DataBuffer buffer = response.bufferFactory().wrap(datas);
	        response.setStatusCode(HttpStatus.UNAUTHORIZED);
	        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
	        return response.writeWith(Mono.just(buffer));
		}
		
		String contentType = serverHttpRequest.getHeaders().getFirst("Content-Type");
		
		// 不是文件上传的post请求
		if (HttpMethod.POST.equals(method) && !contentType.startsWith("multipart/form-data")) {
			// 获取body信息
	//		String bodyStr = exchange.getAttribute("cachedRequestBodyObject");
			String bodyStr = resolveBodyFromRequest(serverHttpRequest);
			
			JSONObject jo = new JSONObject();
			if (StringUtils.isNotEmpty(bodyStr)) {
				jo = JSONObject.parseObject(bodyStr);
			}
	
			// TODO 往body中添加信息 需要确认往body添加的信息
			jo.put("username", "superman");
			
			// 重新 封装request，传给下一级，由于post的body只能订阅一次，所以要再次封装请求到request 才行，不然会报错请求已经订阅过
			ServerHttpRequest request = serverHttpRequest.mutate().uri(uri).build();
			DataBuffer bodyDataBuffer = stringBuffer(jo.toJSONString());
			Flux<DataBuffer> bodyFlux = Flux.just(bodyDataBuffer);
			
			HttpHeaders headers = new HttpHeaders();
	        headers.putAll(exchange.getRequest().getHeaders());
	        
	        // 由于修改了传递参数，需要重新设置CONTENT_LENGTH，长度是字节长度，不是字符串长度
	        int length = jo.toJSONString().getBytes().length;
	        headers.remove(HttpHeaders.CONTENT_LENGTH);
	        headers.setContentLength(length);
	
			request = new ServerHttpRequestDecorator(request) {
				
				 @Override
	             public HttpHeaders getHeaders() {
	                 long contentLength = headers.getContentLength();
	                 HttpHeaders httpHeaders = new HttpHeaders();
	                 httpHeaders.putAll(super.getHeaders());
	                 if (contentLength > 0) {
	                     httpHeaders.setContentLength(contentLength);
	                 } else {
	                     // TODO: this causes a 'HTTP/1.1 411 Length Required' on httpbin.org
	                     httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
	                 }
	                 return httpHeaders;
	             }
				
				@Override
				public Flux<DataBuffer> getBody() {
					return bodyFlux;
				}
			};
			// 特别注意，如果上面body长度经过修改，这边需要重新计算长度，以免下游服务获取body不完整
			request.mutate().header(HttpHeaders.CONTENT_LENGTH, Integer.toString(jo.toJSONString().length()));
			return chain.filter(exchange.mutate().request(request).build());
		} else {
			return chain.filter(exchange);
		}
	}
	

	
	private boolean isAuth() {
		return true;
	}
	
	/**
     * 获取请求体中的字符串内容
     * @param serverHttpRequest
     * @return
     */
    private String resolveBodyFromRequest(ServerHttpRequest serverHttpRequest){
        //获取请求体
        Flux<DataBuffer> body = serverHttpRequest.getBody();
        StringBuilder sb = new StringBuilder();

        body.subscribe(buffer -> {
            byte[] bytes = new byte[buffer.readableByteCount()];
            buffer.read(bytes);
            DataBufferUtils.release(buffer);
            String bodyString = new String(bytes, StandardCharsets.UTF_8);
            sb.append(bodyString);
        });
        return sb.toString();

    }

	private DataBuffer stringBuffer(String value){
	    byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
	    NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(ByteBufAllocator.DEFAULT);
	    DataBuffer buffer = nettyDataBufferFactory.allocateBuffer(bytes.length);
	    buffer.write(bytes);
	    return buffer;
	}

}
