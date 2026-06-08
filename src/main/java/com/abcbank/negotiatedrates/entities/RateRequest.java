package com.abcbank.negotiatedrates.entities;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;
import lombok.ToString;

/**
 * Represents a negotiated rate request in the persistence layer.
 *
 * The entity stores customer details, requested and granted currency rates,
 * approval status, and tracking fields such as UUID and timestamps.
 */
@ToString
@Data
@Entity
public class RateRequest {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String custId;
	//private String sourceAccount;
	private String sourceCurrency;
	private String destinationCurrency;
	private Double requestedAmountLimit;
	private Double grantedAmountLimit;
	private Double requestedRate;
	private Double grantedRate;
	private String requestedBy;
	private String grantedBy;
	private Timestamp createdOn;
	private Timestamp edittedOn;
	private byte status;
	private boolean appeal;
	private String transferType;
	private String uuid;
	private String customerName;
	private String notificationEmail;
}