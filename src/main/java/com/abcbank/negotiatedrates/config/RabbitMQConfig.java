package com.abcbank.negotiatedrates.config;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures RabbitMQ queue and messaging infrastructure for negotiated rates events.
 *
 * Defines a durable queue, message converter, and RabbitTemplate used by the
 * producer to publish events in a JSON format.
 */
@Configuration
public class RabbitMQConfig {

    public static final String RATE_REQUEST_QUEUE =
            "rate-request-queue";

    @Bean
    public Queue rateRequestQueue() {
        // Declare a durable queue for rate request events so messages survive broker restarts.
        return new Queue(RATE_REQUEST_QUEUE, true);
    }

    @Bean
    public Jackson2JsonMessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(
            ConnectionFactory connectionFactory) {

        RabbitTemplate rabbitTemplate =
                new RabbitTemplate(connectionFactory);

        rabbitTemplate.setMessageConverter(converter());

        return rabbitTemplate;
    }
}