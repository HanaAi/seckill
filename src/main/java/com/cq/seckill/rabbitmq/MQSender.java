package com.cq.seckill.rabbitmq;

import com.cq.seckill.pojo.SecKillMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MQSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

//    public void send1(Object msg){
//        log.info("消息发送到queue1:"+msg);
//        rabbitTemplate.convertAndSend("topicExchange","a.b.queue.c.d",msg);
//    }
//
//    public void send2(Object msg){
//        log.info("消息发送到两个queue:"+msg);
//        rabbitTemplate.convertAndSend("topicExchange","a.queue.c.d",msg);
//    }

    /**
     * 发送秒杀消息
     * @param message
     */
    public void sendSecKillMessage(String message) {
        log.info("发送消息:"+ message);
        rabbitTemplate.convertAndSend("seckillExchange","seckill.message",message);
    }
}
