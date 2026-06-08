package com.abcbank.negotiatedrates.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.abcbank.negotiatedrates.dto.DTOAuthPayload;
import com.abcbank.negotiatedrates.dto.DTOAuthPayloadResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * Handles authentication token retrieval from Keycloak for the negotiated rates API.
 *
 * This controller posts user credentials to Keycloak and returns the OAuth2 token
 * response used by clients to authenticate against protected endpoints.
 */
@Slf4j
@RestController
public class AuthController {
	
	@Value("${params.keycloak.config.token-url}")
    String KEYCLOAK_URL;
	
	@Value("${params.keycloak.config.clientid}")
	private String keyCloakClientId;
	
    /**
     * Exchanges user credentials for a Keycloak access token.
     *
     * Builds a form-encoded password grant request and forwards it to the
     * configured Keycloak token endpoint.
     *
     * @param authPayload User credentials for authentication
     * @return Token response from Keycloak or an empty response on failure
     */
	@PostMapping("/negotiated-rates/api/auth-token")
    public DTOAuthPayloadResponse authenticateUser(@RequestBody DTOAuthPayload authPayload) {
		HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", keyCloakClientId);
        map.add("username", authPayload.getUsername());
        map.add("password", authPayload.getPassword());
        map.add("grant_type", "password");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
        try {
            return new RestTemplate().exchange(KEYCLOAK_URL,HttpMethod.POST,entity,DTOAuthPayloadResponse.class).getBody();
        } catch (Exception exception) {
            log.info(exception.getLocalizedMessage());
            String responseError = exception.getLocalizedMessage().replace("400 Bad Request: ", "");
            log.error(responseError);
            return new DTOAuthPayloadResponse();
        }
	}
}
