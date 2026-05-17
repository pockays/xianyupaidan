package com.xianyupaidan.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "xianyupaidan.exchange";
    public static final String QUEUE_EMAIL = "xianyupaidan.email.queue";
    public static final String QUEUE_STATUS = "xianyupaidan.status.queue";
    public static final String ROUTING_EMAIL = "xianyupaidan.email";
    public static final String ROUTING_STATUS = "xianyupaidan.status";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable(QUEUE_EMAIL).build();
    }

    @Bean
    public Queue statusQueue() {
        return QueueBuilder.durable(QUEUE_STATUS).build();
    }

    @Bean
    public Binding emailBinding() {
        return BindingBuilder.bind(emailQueue()).to(exchange()).with(ROUTING_EMAIL);
    }

    @Bean
    public Binding statusBinding() {
        return BindingBuilder.bind(statusQueue()).to(exchange()).with(ROUTING_STATUS);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
