package com.abcbank.negotiatedrates.dto;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class DTORateRequest {
	private int id;
	private String sourceAccount;
	private String sourceCurrency;
	private String destinationCurrency;
	private Double amountLimit;
	private Double requestedRate;
	private String requestedBy;
	private String transferType;
	private String uuid;
}