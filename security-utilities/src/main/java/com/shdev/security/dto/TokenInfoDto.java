package com.shdev.security.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * DTO representing the token info response from security-service.
 * Maps to the response from /oauth2/rest/token/info endpoint.
 *
 * @author Shailesh Halor
 */
public record TokenInfoDto(
        @JsonProperty("iss") String issuer,
        @JsonProperty("aud") List<String> audience,
        @JsonProperty("exp") Long expiration,
        @JsonProperty("jti") String jwtId,
        @JsonProperty("iat") Long issuedAt,
        @JsonProperty("sub") String subject,
        @JsonProperty("client") String client,
        @JsonProperty("scope") List<String> scope,
        @JsonProperty("domain") String domain,
        @JsonProperty("v") String version,
        @JsonProperty("userRole") String userRole  // Colon-separated roles: "ADMIN:USER"
) {
}

