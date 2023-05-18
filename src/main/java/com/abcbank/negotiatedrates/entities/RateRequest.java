package com.abcbank.negotiatedrates.entities;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
@Entity
@Table
public class RateRequest {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String sourceAccount;
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
	private String transferType;
	private String uuid;
}