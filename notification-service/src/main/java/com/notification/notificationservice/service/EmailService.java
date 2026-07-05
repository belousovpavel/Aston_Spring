package com.notification.notificationservice.service;

import com.notification.dto.EmailRequestDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.name:User Service}")
    private String appName;

    @Async
    public void sendAccountCreatedEmail(String to, String name) {
        log.info("Sending account creation email to: {}", to);

        String subject = "Добро пожаловать в " + appName + "!";
        String body = buildEmailBody(to, name, "created");

        sendEmail(to, subject, body);
    }

    @Async
    public void sendAccountDeletedEmail(String to, String name) {
        log.info("Sending account deletion email to: {}", to);

        String subject = "Ваш аккаунт был удален";
        String body = buildEmailBody(to, name, "deleted");

        sendEmail(to, subject, body);
    }

    @Async
    public void sendCustomEmail(EmailRequestDTO request) {
        log.info("Sending custom email to: {}", request.getTo());
        sendEmail(request.getTo(), request.getSubject(), request.getBody());
    }

    private String buildEmailBody(String to, String name, String type) {
        Context context = new Context();
        context.setVariable("name", name != null ? name : "User");
        context.setVariable("email", to);
        context.setVariable("appName", appName);
        context.setVariable("type", type);

        return templateEngine.process("email-template", context);
    }

    private void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true); // true = HTML

            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);

        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
