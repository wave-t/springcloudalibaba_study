package com.wave.cart.config;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.loadbalancer.NacosLoadBalancer;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * OpenFeign 配置负载均衡算法：轮询 随机 优先集群
 * 配置类千万不要加@Configuration注解，也不要被SpringBootApplication扫描到
 * 由于OpenFeignConfig没有加@Configuration注解，也就不会被Spring加载，
 * 因此是不会生效的，我们要在启动类上通过注解来声明这个配置。
 * - 全局配置：对所有服务生效
 * 注解LoadBalancerClients(defaultConfiguration = OpenFeignConfig.class)
 * - 局部配置：只对某个服务生效
 * 注解LoadBalancerClients({
 *         LoadBalancerClient(value = "item-service", configuration = OpenFeignConfig.class)
 * })
 */
public class OpenFeignConfig {
    /**
     * 配置基于Nacos的响应式负载均衡器
     * 使用NacosLoadBalancer替代默认的RoundRobinLoadBalancer，
     * 以支持Nacos服务发现的权重、保护阈值等高级特性
     *
     * @param environment Spring环境变量，用于获取当前服务名称
     * @param properties Nacos服务发现配置属性，包含集群名称、分组等信息
     * @param loadBalancerClientFactory 负载均衡客户端工厂，用于获取服务实例列表提供者
     * @return ReactorLoadBalancer<ServiceInstance> 响应式负载均衡器实例
     */
    @Bean
    public ReactorLoadBalancer<ServiceInstance> reactorServiceInstanceLoadBalancer(
            Environment environment, NacosDiscoveryProperties properties,
            LoadBalancerClientFactory loadBalancerClientFactory) {
        String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);

        return new NacosLoadBalancer(
                loadBalancerClientFactory.getLazyProvider(name, ServiceInstanceListSupplier.class), name, properties);
    }


}
