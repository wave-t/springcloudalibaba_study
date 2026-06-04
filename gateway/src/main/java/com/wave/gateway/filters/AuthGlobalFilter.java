package com.wave.gateway.filters;



import com.wave.common.exception.UnauthorizedException;
import com.wave.common.utils.CollUtils;
import com.wave.gateway.config.AuthProperties;
import com.wave.gateway.utils.JwtTool;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor // 自动注入Final修饰的属性
@EnableConfigurationProperties(AuthProperties.class) // 自动注入配置类
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private final JwtTool jwtTool; //jwt工具类
    private final AuthProperties authProperties; // 配置类
    private final AntPathMatcher antPathMatcher = new AntPathMatcher(); // 路径匹配器

    /**
     * 用户登录验证过滤器逻辑
     * 业务逻辑：
     * 1.判断是否是需要拦截的path地址
     * 2.获取token
     * 3.根据token解析用户ID（用户ID为空或者Token为空 返回401状态码）
     * 4.解析成功继续执行
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        System.out.println("进入用户登录验证过滤器");
        //获取request
        ServerHttpRequest request = exchange.getRequest();
        //判断path是否需要拦截
        if (isExclude(request.getPath().toString())){
            //不需要拦截 放行
            return chain.filter(exchange);
        }
        //获取请求头中的 token
        String token = null;
        // 获取headers中authorization属性值
        List<String> headers = request.getHeaders().get("authorization");
        if (!CollUtils.isEmpty(headers)){
             token = headers.get(0);
        }
        //校验token，解析 token
        Long userId = null;
        try {
            userId = jwtTool.parseToken(token);
        } catch (UnauthorizedException e) {
            // token无效
            ServerHttpResponse response = exchange.getResponse();
            response.setRawStatusCode(401); // 设置状态码 401 登录失效
            return response.setComplete(); // 返回
        }
        String userinfo = userId.toString();
        //保存用户信息到请求头
        ServerWebExchange ex = exchange.mutate().request(b -> b.header("user-info", userinfo)).build();
        //放行 将包含用户信息的请求传递给下一个过滤器
        return chain.filter(ex);
    }

    // 判断path是否需要拦截
    private boolean isExclude(String path) {
        // 获取配置中的放行路径集合
        for (String  excludePath: authProperties.getExcludePaths()) {
            // 使用路径匹配器匹配path值
            if (antPathMatcher.match(excludePath, path)) {
                // 放行
                return true;
            }
        }
        // 路径不存在放行集合中，需要验证用户信息
        return false;
    }

    @Override
    public int getOrder() {
        // 优先级 值小的优先级高
        return 0;
    }
}
