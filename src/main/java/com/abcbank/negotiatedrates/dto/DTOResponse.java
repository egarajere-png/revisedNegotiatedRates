package com.abcbank.negotiatedrates.dto;

import lombok.Data;
import lombok.ToString;

/**
 * Generic API response used by endpoints to return status and messages.
 *
 * This DTO includes a response code, user-facing message, and error flag.
 */
@ToString
@Data
public class DTOResponse {
	private String responseCode;
	private String message;
	private boolean error;
}