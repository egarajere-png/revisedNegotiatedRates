package com.abcbank.negotiatedrates.services;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.abcbank.negotiatedrates.config.RabbitMQConfig;
import com.abcbank.negotiatedrates.dto.RateRequestEvent;

/**
 * Publishes rate request events to RabbitMQ for asynchronous processing.
 *
 * RabbitTemplate is the Spring abstraction used to send messages to queues
 * without requiring low-level broker interaction.
 * Messages are sent to queues to enable decoupled, event-driven architecture
 * where consumers process events independently of request handling.
 */
@Service
public class RabbitMQProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * Sends a RateRequestEvent to the configured queue.
     *
     * @param event Event payload representing a rate request lifecycle action
     */
    public void sendRateRequestEvent(
            RateRequestEvent event) { //convert to json first then convert back to object

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.RATE_REQUEST_QUEUE,
                event);

        System.out.println(
                "Message Sent To Queue: " + event);
    }
}