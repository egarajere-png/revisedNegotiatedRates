package com.abcbank.negotiatedrates.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Holds the token response returned by Keycloak when authentication succeeds.
 *
 * Includes access token, refresh token, token type, expiration details, and
 * the session state returned by the identity provider.
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DTOAuthPayloadResponse {
    public String access_token;
    public int expires_in;
    public int refresh_expires_in;
    public String refresh_token;
    public String token_type;
    @JsonProperty("not-before-policy")
    public int notBeforePolicy;
    public String session_state;
    public String scope;
}