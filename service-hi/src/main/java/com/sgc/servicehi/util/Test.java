package com.sgc.servicehi.util;

import java.security.MessageDigest;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.alibaba.fastjson.JSONObject;

public class Test {

	private static final String VALUE_STRING = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

	private static final String USER_ID = "1e5e78cf21cd4928a6b148b9412e142b";
	private static final String APP_KEY = "401fe60e95c34040b78307987cbef366";

	public static void main(String[] args) {
		String nonceStr = generateNonceStr(32);

		System.out.println("nonceStr:" + nonceStr);

		JSONObject jo = new JSONObject();
		
		jo.put("nonceStr", nonceStr);
		jo.put("imei", "123456789126665");
		jo.put("userId", USER_ID);
		
		String sign = generateSign(jo);
		
		System.out.println(sign);
	}

	public static String generateNonceStr(int length) {
		String code = "";
		for (int i = 0; i < length; i++) {
			code += VALUE_STRING.charAt((int) (Math.random() * 100) % VALUE_STRING.length());
		}
		return code;
	}

	public static String generateSign(JSONObject jo) {
		StringBuilder sb = new StringBuilder();
		
		TreeSet<String> treeSet = new TreeSet<String>();
		
		for (Map.Entry<String, Object> vo : jo.entrySet()) {
			
			treeSet.add(vo.getKey());
		}
		
		for(String key:treeSet) {
			sb.append(key);
			sb.append("=");
			sb.append(jo.getString(key));
			sb.append("&");
		}
		
		sb.append("key=");
		sb.append(APP_KEY);
		System.out.println(sb.toString());
		return MD5(sb.toString()).toUpperCase();
	}
	
	public final static String MD5(String s) {
        char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};       
        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
