package com.abcbank.negotiatedrates.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Event payload used for messaging negotiated rate lifecycle actions via RabbitMQ.
 *
 * DTOs are used for messaging because they carry only the necessary event data
 * and can be serialized to JSON for queue delivery, keeping message contracts
 * simple and stable.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RateRequestEvent {

    private Integer requestId;

    private String customerId;

    private String customerName;

    private String action;
}
