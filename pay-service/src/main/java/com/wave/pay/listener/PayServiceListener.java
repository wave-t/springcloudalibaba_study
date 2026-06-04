package com.wave.pay.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

//监听交易服务错误队列系消息
@Component
public class PayServiceListener {

    //监控服务队列
    @RabbitListener(queues = "error.queue")
    public void payService(String orderId){
        //TODO 订单支付成功，处理业务逻辑
        System.out.println("订单支付成功，处理业务逻辑");
    }
}
