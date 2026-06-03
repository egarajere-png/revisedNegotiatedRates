package com.abcbank.negotiatedrates.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abcbank.negotiatedrates.dto.RateRequestEvent;
import com.abcbank.negotiatedrates.services.RabbitMQProducer;

@RestController
public class RabbitMQTestController {

    @Autowired
    private RabbitMQProducer producer;

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

