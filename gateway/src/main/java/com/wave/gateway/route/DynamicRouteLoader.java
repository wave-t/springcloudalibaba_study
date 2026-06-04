package com.wave.gateway.route;

import cn.hutool.json.JSONUtil;
import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;

import com.wave.common.utils.CollUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

//动态路由加载器
@Component
@Slf4j
@RequiredArgsConstructor
public class DynamicRouteLoader {

    private final RouteDefinitionWriter writer; // 路由定义写入器
    private final NacosConfigManager nacosConfigManager; // nacos配置管理器

    //指定路由配置文件ID和分组
    private String dataId = "gateway-route.json"; //Nacos 配置文件ID
    private String group = "DEFAULT_GROUP"; // 默认分组
    //保存更新过的路由ID
    private Set<String> routeIds = new HashSet<>();

    //启动时加载路由配置
    // @PostConstruct 是 Java 提供的生命周期注解，用于在对象实例化并完成依赖注入后立即执行某个方法。
    @PostConstruct
    public void initRouteConfigListener() throws NacosException {
        //注册监听器并首次拉取配置
        String configInfo = nacosConfigManager.getConfigService().getConfigAndSignListener(dataId, group, 5000, new Listener() {
            @Override
            public Executor getExecutor() {
                return null;
            }
            @Override
            public void receiveConfigInfo(String configInfo) {
                //更新路由配置
                updateConfigInfo(configInfo);
            }
        });
        //启动时，更新一次配置
        updateConfigInfo(configInfo);
    }

    private void updateConfigInfo(String configInfo) {
        log.debug("监听到路由配置变更,{}",configInfo);
        //1.反序列化
        List<RouteDefinition> list = JSONUtil.toList(configInfo, RouteDefinition.class);
        //更新前清空旧路由
        for (String routeId : routeIds){
            writer.delete(Mono.just(routeId)).subscribe();
        }
        routeIds.clear();
        //判断是否有新路由需要更新
        if (CollUtils.isEmpty(list)){
            return;
        }
        //更新路由
        list.forEach(routeDefinition -> {
            //添加路由
            writer.save(Mono.just(routeDefinition)).subscribe();
            //保存路由ID 方便后续删除
            routeIds.add(routeDefinition.getId());
        });
    }
}
