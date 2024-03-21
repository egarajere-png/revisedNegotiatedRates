package com.abcbank.negotiatedrates.dto;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class DTOTransfer {
	private int id;
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