package com.wave.common.config;

import com.wave.common.interceptor.UserInfoMqPostProcessor;
import com.wave.common.interceptor.UserInfoMqPreProcessor;
import com.wave.common.utils.RabbitMqHelper;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// RabbitMQ 配置类 ，因为在common公共模块中，需要将配置类加入自动配置文件中
@Configuration
@ConditionalOnClass(value = {MessageConverter.class, RabbitTemplate.class})
public class RabbitMQConfig {

    //注入Bean 将JDK序列化修改为JSON
    @Bean
    //@ConditionalOnBean(ObjectMapper.class) //ObjectMapper是Jackson的序列化类
    public MessageConverter messageConverter(){
        //定义消息转换器
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        //设置消息ID
        converter.setCreateMessageIds(true);
        return converter;
    }

    //注入RabbitMqHelper bean
    @Bean
    public RabbitMqHelper rabbitMqHelper(RabbitTemplate rabbitTemplate){
        return new RabbitMqHelper(rabbitTemplate);
    }
    //注入UserInfoMqPostProcessor MQ前置处理器Bean
    @Bean
    public UserInfoMqPostProcessor userInfoMqPostProcessor(){
        return new UserInfoMqPostProcessor();
    }

    //注入UserInfoMqPreProcessor MQ后置处理器Bean
    @Bean
    public UserInfoMqPreProcessor userInfoMqPreProcessor(){
        return new UserInfoMqPreProcessor();
    }

    //将MQ前置信息处理器加入自动配置中
    @Bean
    public RabbitTemplate rabbitTemplateWithUserInfo(ConnectionFactory connectionFactory,
                                                     MessageConverter messageConverter,
                                                     UserInfoMqPostProcessor postProcessor) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        template.setBeforePublishPostProcessors(postProcessor);
        return template;
    }

    //将MQ后置信息处理器加入自动配置中
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter,
            UserInfoMqPreProcessor preProcessor) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setAfterReceivePostProcessors(preProcessor);
        return factory;
    }
}
