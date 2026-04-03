package vn.kurisu.mentormatch.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import vn.kurisu.mentormatch.config.RabbitMQConfig;
import vn.kurisu.mentormatch.dto.request.NotificationEventDto;
import vn.kurisu.mentormatch.entity.User;
import vn.kurisu.mentormatch.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailConsumer {

    private final JavaMailSender mailSender;
    private final UserRepository userRepository;

    @Value("${spring.mail.username:noreply@mentormatch.vn}")
    private String senderEmail;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NOTIFICATION)
    public void consumeMessage(NotificationEventDto event) {
        try {
            log.info("Received notification event from RabbitMQ: {}", event.getTitle());

            User user = userRepository.findById(event.getUserId().intValue())
                    .orElse(null);

            if (user != null && user.getEmail() != null && !user.getEmail().isEmpty()) {
                sendEmail(user.getEmail(), event.getTitle(), event.getMessage());
            } else {
                log.warn("User {} not found or has no email address. Cannot send email.", event.getUserId());
            }
        } catch (Exception e) {
            log.error("Error processing message from RabbitMQ: {}", e.getMessage(), e);
        }
    }

    private void sendEmail(String toAddress, String subject, String body) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(senderEmail);
            mailMessage.setTo(toAddress);
            mailMessage.setSubject(subject);
            mailMessage.setText(body);
            
            mailSender.send(mailMessage);
            log.info("Email sent successfully to {}", toAddress);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", toAddress, e.getMessage());
        }
    }
}
