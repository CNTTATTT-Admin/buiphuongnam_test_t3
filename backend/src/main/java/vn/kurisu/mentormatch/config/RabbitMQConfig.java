package vn.kurisu.mentormatch.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String QUEUE_NOTIFICATION = "notification_queue";
    public static final String EXCHANGE_MENTORMATCH = "mentormatch_exchange";
    public static final String ROUTING_KEY_NOTIFICATION = "notify_key";

    @Bean
    public Queue notificationQueue() {
        // Tham số 'true' giúp Queue không bị mất data khi sập server
        return new Queue(QUEUE_NOTIFICATION, true); 
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE_MENTORMATCH);
    }

    @Bean
    public Binding binding(Queue notificationQueue, DirectExchange exchange) {
        return BindingBuilder.bind(notificationQueue).to(exchange).with(ROUTING_KEY_NOTIFICATION);
    }

    @Bean // Ép Spring Boot tự động chuyển DTO thành chuỗi JSON
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}