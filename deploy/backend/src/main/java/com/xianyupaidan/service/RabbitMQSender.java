package com.xianyupaidan.service;

import com.xianyupaidan.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class RabbitMQSender {

    private final RabbitTemplate rabbitTemplate;

    public void sendEmailNotification(String to, String nickname, String statusText) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_EMAIL,
                Map.of("to", to, "nickname", nickname, "statusText", statusText));
    }

    public void sendStatusChange(Long orderId, String oldStatus, String newStatus, Long userId) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_STATUS,
                Map.of("orderId", orderId, "oldStatus", oldStatus,
                        "newStatus", newStatus, "userId", userId));
    }
}
