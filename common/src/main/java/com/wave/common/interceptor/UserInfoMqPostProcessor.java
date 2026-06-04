package com.wave.common.interceptor;


import com.wave.common.utils.UserContext;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;

//创建MQ前置消息处理器，将用户信息放入信息头中
public class UserInfoMqPostProcessor implements MessagePostProcessor {
    @Override
    public Message postProcessMessage(Message message) throws AmqpException {
        //清理用户信息，防止用户信息残留，导致脏数据污染
        UserContext.removeUser();
        //获取用户信息
        Long userId = UserContext.getUser();
        if (userId != null){
            message.getMessageProperties().setHeader("user-info",userId);
        }
        return message;
    }
}
