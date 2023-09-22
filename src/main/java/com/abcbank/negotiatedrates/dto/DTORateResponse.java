package com.abcbank.negotiatedrates.dto;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class DTORateResponse {
	private String sourceAccount;
	private String sourceCurrency;
	private String destinationCurrency;
	private Double amountLimit;
	private Double grantedRate;
	private boolean appeal;
}