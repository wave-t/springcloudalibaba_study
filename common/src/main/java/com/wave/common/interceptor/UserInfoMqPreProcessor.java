package com.wave.common.interceptor;


import com.wave.common.utils.UserContext;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;

//创建MQ消息后置处理器，获取消息头中的用户信息，存入本地线程
public class UserInfoMqPreProcessor implements MessagePostProcessor {
    @Override
    public Message postProcessMessage(Message message) throws AmqpException {
        String userId = message.getMessageProperties().getHeader("user-info");
        if (userId != null && !userId.isEmpty()){
            UserContext.setUser(Long.valueOf(userId));
        }
        return message;
    }
}
