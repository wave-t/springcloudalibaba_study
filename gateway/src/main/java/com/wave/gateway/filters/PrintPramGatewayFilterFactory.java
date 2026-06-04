package com.wave.gateway.filters;


import lombok.Data;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

// 自定义带参数的GatewayFilter 继承 AbstractGatewayFilterFactory有参泛型给静态内部类Config
@Component
public class PrintPramGatewayFilterFactory extends AbstractGatewayFilterFactory<PrintPramGatewayFilterFactory.Config> {

    @Override
    public GatewayFilter apply(Config config) {
                /**
                 * 创建一个OrderedGatewayFilter
                 * 1.创建一个GatewayFilter
                 * 2.设置GatewayFilter的顺序
                 */
        return new OrderedGatewayFilter(
                new GatewayFilter() {
                    @Override
                    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                        //获取Config参数信息
                        String name = config.getName();
                        String age = config.getAge();
                        String sex = config.getSex();
                        //打印config信息
                        System.out.println("PrintInfo打印Config参数：");
                        System.out.println("name:" + name + " age:" + age + " sex:" + sex);
                        //放行，继续执行过滤器链
                        return chain.filter(exchange);
                    }
                }
                ,100);
    }

    // 自定义配置属性，成员变量名称很重要，下面会用到
    @Data
    static class Config {
        private String name;
        private String age;
        private String sex;
    }

    // 将变量名称依次返回，顺序很重要，将来读取参数时需要按顺序获取
    @Override
    public List<String> shortcutFieldOrder() {
        return List.of("name","age","sex");
    }

    // 返回当前配置类的类型，也就是内部的Config
    @Override
    public Class<Config> getConfigClass() {
        return Config.class;
    }
}
