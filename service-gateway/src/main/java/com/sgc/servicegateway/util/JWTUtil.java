package com.sgc.servicegateway.util;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.sgc.servicegateway.entity.User;

public class JWTUtil {
    // 过期时间 24 小时
    private static final long EXPIRE_TIME = 60 * 24 * 60 * 1000;
//	private static final long EXPIRE_TIME = 5 * 60 * 1000;
    // 密钥
    private static final String SECRET = "SHIRO+JWT";

    /**
     * 生成 token
     *
     * @return 加密的token
     */
    public static String createToken(User user) {
        try {
            Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            // 附带username信息
            return JWT.create()
                    .withClaim("username", user.getUsername())
                    .withClaim("gmtModified", user.getGmtModified())
                    //到期时间
                    .withExpiresAt(date)
                    //创建一个新的JWT，并使用给定的算法进行标记
                    .sign(algorithm);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    /**
     * 校验 token 是否正确
     *
     * @param token    密钥
     * @param username 用户名
     * @return 是否正确
     */
    public static boolean verify(String token, User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            //在token中附带了username信息
            JWTVerifier verifier = JWT.require(algorithm)
                    .withClaim("username", user.getUsername())
                    .withClaim("gmtModified", user.getGmtModified())
                    .build();
            //验证 token
            verifier.verify(token);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    /**
     * 获得token中的信息，无需secret解密也能获得
     *
     * @return token中包含的用户名
     */
    public static String getUsername(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("username").asString();
        } catch (JWTDecodeException e) {
            return null;
        }
    }
    
    /**
     * 获得token中的信息，无需secret解密也能获得
     *
     * @return token中包含的用户最后更新时间
     */
    public static Date getUpdateDate(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("gmtModified").asDate();
        } catch (JWTDecodeException e) {
            return null;
        }
    }
    
    /**
     * 取得token的过期时间
     * @author zpc
     * @date 2019年1月31日上午11:39:57
     * @param token
     * @return token过期时间
     */
    public static Date getTokenExpiredDate(String token) {
    	DecodedJWT jwt = JWT.decode(token);
    	
    	return jwt.getExpiresAt();
    }
}
