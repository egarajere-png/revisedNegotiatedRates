package com.abcbank.negotiatedrates.dto;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class DTOResponse {
	private String responseCode;
	private String message;
	private boolean error;
}