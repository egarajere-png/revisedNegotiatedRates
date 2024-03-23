package com.abcbank.negotiatedrates.dto;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class DTORateResponse {
	private int negotiatedRateId;
	private String custId;
	private String sourceCurrency;
	private String destinationCurrency;
	private Double amountLimit;
	private Double grantedRate;
	private boolean appeal;
}