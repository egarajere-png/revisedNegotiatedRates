package com.abcbank.negotiatedrates.dto;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class DTORateApproval {
	private int id;
	private Double grantedAmountLimit;
	private Double grantedRate;
	private String grantedBy;
	private byte status;
	private String uuid;
}