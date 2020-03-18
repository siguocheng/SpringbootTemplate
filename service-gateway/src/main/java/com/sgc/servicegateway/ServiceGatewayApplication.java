package com.sgc.servicegateway;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableEurekaClient
@ComponentScan(basePackages={"com.sgc"})
@MapperScan(basePackages={"com.sgc.servicegateway.dao"})
public class ServiceGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run( ServiceGatewayApplication.class, args );
    }
}
