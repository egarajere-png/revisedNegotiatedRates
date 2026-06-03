package com.abcbank.negotiatedrates.services;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.abcbank.negotiatedrates.config.RabbitMQConfig;
import com.abcbank.negotiatedrates.dto.RateRequestEvent;

@Service
public class RabbitMQConsumer {

    @RabbitListener(
            queues = RabbitMQConfig.RATE_REQUEST_QUEUE)
    public void receiveMessage(
            RateRequestEvent event) {

        System.out.println(
                "Received Message: " + event);
    }
}