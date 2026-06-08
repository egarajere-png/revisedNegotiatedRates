package com.abcbank.negotiatedrates.config;

import java.util.Collection;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configures Spring Security for the negotiated rates application.
 *
 * This configuration disables CSRF for API access, permits public OpenAPI and
 * authentication endpoints, and secures all other routes with JWT bearer tokens.
 * It also integrates Keycloak role conversion so Keycloak realm roles are used
 * for method-level role-based authorization.
 *
 * OAuth2 Resource Server functionality is enabled via JWT validation.
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    /**
     * Configures HTTP security rules for API access.
     *
     * Public access is granted to Swagger/OpenAPI documentation and the
     * authentication token endpoint. All other requests require authentication.
     * The application is configured as an OAuth2 Resource Server using JWT.
     *
     * @param http HttpSecurity builder
     * @return Built security filter chain
     * @throws Exception If configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http)
            throws Exception {

        http
                .csrf().disable()

                .authorizeRequests()

                .antMatchers(
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**"
                ).permitAll()

                .antMatchers(
                        "/negotiated-rates/api/auth-token"
                ).permitAll()

                .anyRequest()
                .authenticated()

                .and()

                .oauth2ResourceServer()
                .jwt()
                .jwtAuthenticationConverter(
                        jwtAuthenticationConverter());

        return http.build();
    }

    /**
     * Converts JWT claims into Spring Security granted authorities.
     *
     * This converter uses the KeycloakJwtRoleConverter to extract roles from the
     * Keycloak JWT realm_access claim and maps them into ROLE_* authorities.
     *
     * @return JwtAuthenticationConverter configured for Keycloak roles
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtAuthenticationConverter converter =
                new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(
                new Converter<Jwt, Collection<GrantedAuthority>>() {

                    @Override
                    public Collection<GrantedAuthority> convert(
                            Jwt jwt) {

                        return (Collection)
                                new KeycloakJwtRoleConverter()
                                        .convert(jwt);
                    }
                });

        return converter;
    }
}