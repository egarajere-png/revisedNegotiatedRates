package com.abcbank.negotiatedrates.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Represents credentials submitted to Keycloak to obtain an access token.
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DTOAuthPayload {
    String username;
    String password;
}
