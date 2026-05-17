package com.xianyupaidan.service;

import com.xianyupaidan.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailConsumer {

    private final EmailService emailService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_EMAIL)
    public void handleEmailNotification(Map<String, Object> message) {
        String to = (String) message.get("to");
        String nickname = (String) message.get("nickname");
        String statusText = (String) message.get("statusText");
        log.info("RabbitMQ received email task: to={}, status={}", to, statusText);
        emailService.sendOrderStatusNotification(to, nickname, statusText);
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_STATUS)
    public void handleStatusChange(Map<String, Object> message) {
        log.info("RabbitMQ received status change: orderId={}, {} -> {}",
                message.get("orderId"), message.get("oldStatus"), message.get("newStatus"));
    }
}
