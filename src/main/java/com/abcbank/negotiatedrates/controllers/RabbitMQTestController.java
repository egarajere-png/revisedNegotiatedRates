package com.abcbank.negotiatedrates.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abcbank.negotiatedrates.dto.RateRequestEvent;
import com.abcbank.negotiatedrates.services.RabbitMQProducer;

/**
 * Provides a lightweight endpoint to verify RabbitMQ integration.
 *
 * This controller sends a sample rate request event to the configured RabbitMQ
 * queue so that the asynchronous consumer path can be validated.
 */
@RestController
public class RabbitMQTestController {

    @Autowired
    private RabbitMQProducer producer;

    /**
     * Sends a sample RateRequestEvent message to RabbitMQ.
     *
     * Useful for testing the event-driven architecture and ensuring the producer
     * can publish messages to the queue.
     *
     * @return Confirmation string after sending the event
     */
    @GetMapping("/rabbit-test")
    public String testRabbitMQ() {

        RateRequestEvent event =
                new RateRequestEvent(
                        1,
                        "CUST001",
                        "John Doe",
                        "REQUEST_CREATED");

        producer.sendRateRequestEvent(event);

        return "Message Sent";
    }
}

