package com.wave.common.config;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 消费者重试次数耗尽后，将失败的消息投放到此交换机，后续由人工处理
 */
@Configuration
// 判断是否开启MQ消息重试功能，开启的情况下，才会生效
@ConditionalOnProperty(name = "spring.rabbitmq.listener.simple.retry.enabled", havingValue = "true")
public class MqConsumeErrorAutoConfiguration {

    @Value("${spring.application.name}")
    private String errorRoutingKey;

    private final String errorQueueName = "error.queue";

    private final String errorExchangeName = "error.direct";

    //声明一个交换机，名为error.direct，类型为direct
    @Bean
    public DirectExchange errorDirectExchange() {
        return new DirectExchange(errorExchangeName);
    }

    //声明一个队列，名为：微服务名 + error.queue，也就是说要动态获取
    @Bean
    public Queue errorQueue() {
        String queueName = errorRoutingKey + "." + errorQueueName ;
        return new Queue(queueName, true);
    }

    //将队列与交换机绑定，绑定时的RoutingKey就是微服务名
    @Bean
    public Binding errorBinding(Queue errorQueue, DirectExchange errorDirectExchange) {
        return BindingBuilder.bind(errorQueue).to(errorDirectExchange).with(errorRoutingKey);
    }

    //定义RepublishMessageRecoverer,关联交换机到队列
    @Bean
    public MessageRecoverer republishMessageRecoverer(RabbitTemplate rabbitTemplate) {
        return new RepublishMessageRecoverer(rabbitTemplate, errorExchangeName, errorRoutingKey);

    }
}
