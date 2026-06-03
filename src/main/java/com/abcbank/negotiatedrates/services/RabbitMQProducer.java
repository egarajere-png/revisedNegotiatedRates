package com.abcbank.negotiatedrates.services;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.abcbank.negotiatedrates.config.RabbitMQConfig;
import com.abcbank.negotiatedrates.dto.RateRequestEvent;

@Service
public class RabbitMQProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendRateRequestEvent(
            RateRequestEvent event) {

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.RATE_REQUEST_QUEUE,
                event);

        System.out.println(
                "Message Sent To Queue: " + event);
    }
}