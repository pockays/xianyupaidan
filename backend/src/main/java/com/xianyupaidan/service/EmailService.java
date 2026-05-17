package com.xianyupaidan.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String mailFrom;

    public void sendOrderStatusNotification(String to, String nickname, String statusText) {
        if (to == null || to.isBlank()) {
            log.warn("Email skipped: no recipient address");
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mailFrom);
            message.setTo(to);
            message.setSubject("排单状态更新通知");
            message.setText("亲爱的 " + nickname + "：\n\n" +
                    "您的排单状态已更新为：" + statusText + "\n\n" +
                    "请登录系统查看详情。\n\n" +
                    "此邮件由系统自动发送，请勿回复。");
            mailSender.send(message);
            log.info("Email sent to {} for order status: {}", to, statusText);
        } catch (Exception e) {
            log.error("Failed to send email to {}", to, e);
        }
    }
}
