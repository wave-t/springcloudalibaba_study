package com.wave.gateway.filters;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

// 自定义GatewayFilter 继承 AbstractGatewayFilterFactory无参泛型给Object
@Component
public class PrintInfoGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {
    @Override
    public GatewayFilter apply(Object config) {
        return new GatewayFilter() {
            /**
             * 过滤逻辑
             * @param exchange 请求对象
             * @param chain 过滤器链
             * @return 响应对象
             */
            @Override
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                //获取请求对象
                ServerHttpRequest request = exchange.getRequest();
                System.out.println("PrintInfo过滤器执行了，请求方式：" + request.getMethod());
                //打印当前过滤器顺序号
                //放行，继续执行过滤器链
                return chain.filter( exchange);
            }
        };
    }
}
