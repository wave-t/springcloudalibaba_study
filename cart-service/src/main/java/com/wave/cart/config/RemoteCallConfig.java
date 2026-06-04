package com.wave.cart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;


// 通过配置类注入外部Bean，好在项目中使用
@Configuration
public class RemoteCallConfig {
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
