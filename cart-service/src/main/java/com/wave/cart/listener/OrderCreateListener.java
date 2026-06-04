package com.wave.cart.listener;

import com.wave.cart.service.ICartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreateListener {

    private final ICartService cartService;

    //监听订单创建消息，成功后清理购物车商品
    //交换机trade.topic 队列 cart.clear.queue RoutingKey order.create
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "cart.clear.queue"),
            exchange = @Exchange(value = "trade.topic", type = "topic"),
            key = "order.create"
    ))
    public void clearCart(Set<Long> itemIds) {
        //TODO 删除购物车商品
        try {
            cartService.removeByItemIds(itemIds);
        } catch (Exception e) {
            System.out.println("删除购物车商品失败!");
        }
    }
}
