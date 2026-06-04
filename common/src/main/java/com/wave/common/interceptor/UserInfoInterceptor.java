package com.wave.common.interceptor;


import cn.hutool.core.util.StrUtil;
import com.wave.common.utils.UserContext;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//微服务的拦截器,该拦截器需要获取到用户信息，并将用户信息存入到本地线程中
public class UserInfoInterceptor implements HandlerInterceptor {

    // 在请求之前进行数据操作 将头部的用户信息存入本地线程UserContext中
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        //从头部获取到网关微服务传入的用户信息user-info
        String userInfo = request.getHeader("user-info");
        //判断用户信息是否为空
        if (StrUtil.isNotBlank(userInfo)){
            //将用户信息存入到本地线程中
            UserContext.setUser(Long.valueOf(userInfo));
        }
        return true; //放行，false 拦截
    }

    // 请求结束，将用户信息从本地线程中删除
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 删除本地线程中的用户信息，防止内存溢出
        UserContext.removeUser();
    }
}
