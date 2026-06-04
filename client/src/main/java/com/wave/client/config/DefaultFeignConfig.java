package com.wave.client.config;



import com.wave.client.api.fallback.*;
import com.wave.common.utils.UserContext;
import feign.Logger;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;

public class DefaultFeignConfig {

    //添加feign日志级别Bean
    @Bean
    public Logger.Level feignLogLevel(){
        return Logger.Level.FULL;
    }
    //添加Bean 使用requestTemplate保存用户信息到请求头
    @Bean
    public RequestInterceptor unserInfoRequestInterceptor(){
        return template -> {
            //获取用户信息
            Long userId = UserContext.getUser();
            if (userId != null){
                //用户信息不为空 存入头部
                template.header("user-info",userId.toString());
            }
        };
    }

    //添加ItmeClient客户端的降级逻辑处理类的Bean
    @Bean
    public ItmeClientFallback itmeClientFallback(){
        return new ItmeClientFallback();
    }

    //添加CartClient客户端的降级逻辑处理类的Bean
    @Bean
    public CartClientFallback cartClientFallback(){
        return new CartClientFallback();
    }

    //添加TradeClient客户端的降级逻辑处理类的Bean
    @Bean
    public TradeClientFallback tradeClientFallback(){
        return new TradeClientFallback();
    }

    //添加UserClient客户端的降级逻辑处理类的Bean
    @Bean
    public UserClientFallback userClientFallback(){
        return new UserClientFallback();
    }

    //添加PayClient客户端的降级逻辑处理类的Bean
    @Bean
    public PayClientFallback payClientFallback(){
        return new PayClientFallback();
    }
}
