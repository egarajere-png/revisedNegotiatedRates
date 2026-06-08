package com.abcbank.negotiatedrates.dto;

import lombok.Data;
import lombok.ToString;

/**
 * Represents the payload used when a treasury user approves a negotiated rate.
 *
 * This DTO includes the granted amount limit, granted rate, approver identity,
 * and the status of the approval operation.
 */
@ToString
@Data
public class DTORateApproval {
	private int id;
	private Double grantedAmountLimit;
	private Double grantedRate;
	private String grantedBy;
	private int status;
	private String uuid;
}