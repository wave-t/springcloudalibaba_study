package com.wave.trade.listener;

import com.wave.trade.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor //final修饰，使用构造函数注入
public class PayStatusListener {

    private  final IOrderService orderService; //订单服务

    //监听订单支付成功消息。虚拟机pay.direct，队列tread.pay.success.queue，RoutingKey pay.success
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "tread.pay.success.queue"), //队列
            exchange = @Exchange(value = "pay.direct", type = "direct"), //交换机
            key = "pay.success" //RoutingKey
    ))
    public void paySuccess(Long orderId) {
        orderService.markOrderPaySuccess(orderId);
    }
}
