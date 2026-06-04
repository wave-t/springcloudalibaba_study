package com.wave.cart;

import com.wave.client.config.DefaultFeignConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

//启用OpenFeig功能,并添加日志配置类
@EnableFeignClients(basePackages = "com.wave.client.api", defaultConfiguration = DefaultFeignConfig.class)
@MapperScan("com.wave.cart.mapper")
@SpringBootApplication
public class CartApplication {
    public static void main(String[] args) {
        SpringApplication.run(CartApplication.class, args);
    }

}
