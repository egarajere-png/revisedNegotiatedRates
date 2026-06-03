package com.abcbank.negotiatedrates.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RateRequestEvent {

    private Integer requestId;

    private String customerId;

    private String customerName;

    private String action;
}