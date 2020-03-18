package com.sgc.servicegateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.reactive.HiddenHttpMethodFilter;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;

import com.sgc.servicegateway.filter.ParameterWrapFilter;
import com.sgc.servicegateway.filterFactory.RequestTimeGatewayFilterFactory;

import reactor.core.publisher.Mono;

@Configuration
public class GatewayConfig {

	@Bean
    public RequestTimeGatewayFilterFactory requestTimeGatewayFilterFactory() {
        return new RequestTimeGatewayFilterFactory();
    }
    
    @Bean
    public ParameterWrapFilter parameterWrapFilter() {
        return new ParameterWrapFilter();
    }
    @Bean
    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter() {
            @Override
            public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
                return chain.filter(exchange);
            }
        };
    }
}
