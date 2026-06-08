package com.abcbank.negotiatedrates.services;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.abcbank.negotiatedrates.config.RabbitMQConfig;
import com.abcbank.negotiatedrates.dto.RateRequestEvent;

import lombok.extern.slf4j.Slf4j;

/**
 * Listens for RabbitMQ messages and processes rate request events.
 *
 * @RabbitListener registers this method as an asynchronous consumer of the
 * configured queue. The consumer receives messages whenever they arrive,
 * enabling background processing without blocking REST request threads.
 */
@Slf4j
@Service
public class RabbitMQConsumer {

    @RabbitListener(
            queues = RabbitMQConfig.RATE_REQUEST_QUEUE)// -> Wattch this string forever unti one has been picked
    public void receiveMessage(
            RateRequestEvent event) {// convert to json first then convert back to object

        log.info(
                "RabbitMQ Event Received: {}",
                event);

        // Handle event actions as soon as the consumer receives them from the queue.
        // This allows asynchronous, event-driven processing decoupled from HTTP requests.

        switch (event.getAction()) {

            case "REQUEST_CREATED":
                log.info(
                        "Processing New Rate Request For Customer {}",
                        event.getCustomerName());
                break;

            case "REQUEST_APPROVED":
                log.info(
                        "Processing Approved Rate Request {}",
                        event.getRequestId());
                break;

            case "REQUEST_REJECTED":
                log.info(
                        "Processing Rejected Rate Request {}",
                        event.getRequestId());
                break;

            default:
                log.info(
                        "Unknown Event Type");
        }
    }
}