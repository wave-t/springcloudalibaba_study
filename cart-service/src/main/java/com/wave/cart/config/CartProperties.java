package com.wave.cart.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

//cart服务的配置属性类
//基于Nacos的热加载配置类。对应的是Nacos配置管理中的 服务名.yaml（cart-service.yaml）配置
//该类中的属性和值支持热加载，在Nacos的配置管理中修改后，不用重启服务，直接生效
@Data
@Component
@ConfigurationProperties(prefix = "hm.cart")
public class CartProperties {
    private Integer maxAmount; // 最大购物车数量
}
