package com.abcbank.negotiatedrates.config;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Extracts Keycloak roles from the JWT token and maps them to Spring Security authorities.
 *
 * Keycloak embeds user roles inside the realm_access claim. This converter reads
 * those roles and prefixes them with ROLE_ so method-level annotations like
 * @PreAuthorize("hasRole('CUSTOMER')") can be evaluated correctly.
 */
public class KeycloakJwtRoleConverter
        implements Converter<Jwt, Collection<SimpleGrantedAuthority>> {

    @Override
    public Collection<SimpleGrantedAuthority> convert(Jwt jwt) {

        // Extract roles from the Keycloak JWT realm_access claim.
        Map<String, Object> realmAccess =
                jwt.getClaim("realm_access");

        if (realmAccess == null) {
            return Collections.emptyList();
        }

        List<String> roles =
                (List<String>) realmAccess.get("roles");

        if (roles == null) {
            return Collections.emptyList();
        }

        return roles.stream()
                .map(role ->
                        new SimpleGrantedAuthority(
                                "ROLE_" + role))
                .collect(Collectors.toList());
    }
}