package com.abcbank.negotiatedrates.entities;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.ToString;

/**
 * Represents a transfer transaction associated with a negotiated rate request.
 *
 * The entity stores transfer amount, beneficiary details, currency pair data,
 * and a reference to the related RateRequest.
 */
@ToString
@Data
@Entity
public class Transfer {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private Double amount;
	private String recipientName;
	private String recipientAddress;
	private String recipientBank;
	private String recipientBankAddress;
	private String createdBy;
	private Timestamp createdOn;
	private Timestamp edittedOn;
	private String sourceAccount;
	private String sourceCurrency;
	private String destinationCurrency;
	private byte status;
	private String transferType;
	@ManyToOne(fetch = FetchType.LAZY)
	private RateRequest rateRequest;
}