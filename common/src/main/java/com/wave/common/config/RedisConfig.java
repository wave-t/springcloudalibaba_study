package com.wave.common.config;

import io.lettuce.core.ReadFrom;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//redis配置类，配置redis读写分离 配置类需要加入到自动注入配置文件中 spring.factories
@Configuration
public class RedisConfig {

    //注入bean 配置redis的读写分离
    @Bean
    public LettuceClientConfigurationBuilderCustomizer clientConfigurationBuilderCustomizer(){
        /**
         * - MASTER：从主节点读取
         * - MASTER_PREFERRED：优先从master节点读取，master不可用才读取slave
         * - REPLICA：从slave节点读取
         * - REPLICA_PREFERRED：优先从slave节点读取，所有的slave都不可用才读取master
         */
        return builder -> builder.readFrom(ReadFrom.REPLICA_PREFERRED);
    }

}
