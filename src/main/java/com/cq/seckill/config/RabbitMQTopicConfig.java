package com.cq.seckill.config;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.impl.AMQImpl;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQTopicConfig {
//    private static final String QUEUE01 = "queue_topic01";
//    private static final String QUEUE02 = "queue_topic02";
//    private static final String EXCHANGE ="topicExchange";
//    private static final String ROUTINGKEY1 = "#.queue.#";
//    private static final String ROUTINGKEY2 = "*.queue.#";
//
//    @Bean
//    public Queue queue1(){
//        return new Queue(QUEUE01);
//    }
//
//    @Bean
//    public Queue queue2(){
//        return new Queue(QUEUE02);
//    }
//
//    @Bean
//    public TopicExchange topicExchange(){
//        return new TopicExchange(EXCHANGE);
//    }
//
//    @Bean
//    public Binding binding1(){
//        return BindingBuilder.bind(queue1()).to(topicExchange()).with(ROUTINGKEY1);
//    }
//
//    @Bean
//    public Binding binding2(){
//        return BindingBuilder.bind(queue2()).to(topicExchange()).with(ROUTINGKEY2);
//    }
}
