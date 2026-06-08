package com.abcbank.negotiatedrates.dto;

import lombok.Data;
import lombok.ToString;

/**
 * Represents transfer details posted by a customer after rate approval.
 *
 * This DTO is used to capture beneficiary and amount data for a transfer
 * associated with a negotiated rate request.
 */
@ToString
@Data
public class DTOTransfer {
	private int id;
	private int rateRequestId;
	private Double amount;
	private String sourceAccount;
	private String custId;
	private String sourceCurrency;
	private String destinationCurrency;
	private String recipientName;
	private String recipientAddress;
	private String recipientBank;
	private String recipientBankAddress;
	private String createdBy;
}