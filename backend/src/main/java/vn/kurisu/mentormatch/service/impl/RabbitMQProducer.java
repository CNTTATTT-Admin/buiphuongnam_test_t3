package vn.kurisu.mentormatch.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import vn.kurisu.mentormatch.config.RabbitMQConfig;
import vn.kurisu.mentormatch.dto.request.NotificationEventDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitMQProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendNotificationEvent(NotificationEventDto event) {
        log.info("Sending notification event to RabbitMQ: {}", event.getTitle());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_MENTORMATCH,
                RabbitMQConfig.ROUTING_KEY_NOTIFICATION,
                event
        );
    }
}
