package com.wave.trade.listener;

import com.wave.client.api.PayClient;
import com.wave.client.dto.PayOrderDTO;
import com.wave.trade.constants.MQConstants;
import com.wave.trade.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderDelayMessageListener {

    private final IOrderService orderService;
    private  final PayClient payClient;
    //监听订单支付超时消息
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MQConstants.DELAY_ORDER_QUEUE_NAME), //队列
            exchange = @Exchange(value = MQConstants.DELAY_EXCHANGE_NAME, delayed = "true"), //交换机
            key = MQConstants.DELAY_ORDER_KEY //RoutingKey
    ))
    public void listenOrderDelayMessage(Long orderId) {
        //根据订单ID查询订单
        Order order = orderService.getById(orderId);
        //检测订单状态，判断是否已支付
        if (order == null || order.getStatus() != 1 ){
            return;
        }
        //未支付，需要查询支付流水状态
        PayOrderDTO payOrder =payClient.queryPayOrderByBizOrderNo(orderId);
        //判断是否支付
        if (payOrder != null && payOrder.getStatus() == 3){
            // 已支付，标记订单为已经支付
            orderService.markOrderPaySuccess(orderId);
        }else {
            //未支付，取消订单 恢复库存
            orderService.cancelOrder(orderId);
        }

    }
}
