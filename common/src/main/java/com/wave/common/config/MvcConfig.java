package com.wave.common.config;

import com.wave.common.interceptor.UserInfoInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//微服务拦截器的配置类，添加拦截器到SpringMvc流程中
//因日该配置类的包和微服务的包不一致，需要将配置类添加到SpringBoot的自动装配文件中扫描
@Configuration
@ConditionalOnClass(DispatcherServlet.class) //SpringBoot添加注解，判断类是否存在指定类，存在的话就加载该配置类
public class MvcConfig implements WebMvcConfigurer {
    //传统的 Spring MVC 应用，需要 DispatcherServlet 来处理 HTTP 请求和拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 1.添加拦截器
        registry.addInterceptor(new UserInfoInterceptor());

    }
}
