package com.wave.trade.constants;

//MQ使用的常量
public interface MQConstants {
    String DELAY_EXCHANGE_NAME = "trade.delay.direct"; // 延迟交换机名称
    String DELAY_ORDER_QUEUE_NAME = "trade.delay.order.queue"; // 延迟队列名称
    String DELAY_ORDER_KEY = "delay.order.query"; // 延迟队列绑定的RoutingKey

}
