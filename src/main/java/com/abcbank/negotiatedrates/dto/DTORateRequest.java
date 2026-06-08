package com.abcbank.negotiatedrates.dto;

import lombok.Data;
import lombok.ToString;

/**
 * Represents a negotiated rate request submitted by a customer.
 *
 * This DTO carries information used by the API to create or update
 * a negotiated rate request, including currency pair, requested amount,
 * and customer details.
 */
@ToString
@Data
public class DTORateRequest {
	private int id;
	//private String sourceAccount;
	private String custId;
	private String sourceCurrency;
	private String destinationCurrency;
	private Double amountLimit;
	private Double requestedRate;
	private String requestedBy;
	private String transferType;
	private String uuid;
	private boolean appeal;
	private String customerName;
	private String notificationEmail;
}