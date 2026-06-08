package com.abcbank.negotiatedrates.dto;

import lombok.Data;
import lombok.ToString;

/**
 * Represents the response returned to clients when querying negotiated rates.
 *
 * This DTO contains the negotiated rate, granted amount limit and currency
 * pair details associated with the rate request.
 */
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