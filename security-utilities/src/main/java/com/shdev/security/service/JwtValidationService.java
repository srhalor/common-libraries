package com.shdev.security.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shdev.common.constants.HeaderConstants;
import com.shdev.security.constants.SecurityConstants;
import com.shdev.security.dto.TokenInfoDto;
import com.shdev.security.exception.TokenValidationException;
import com.shdev.security.util.ErrorMessageExtractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


/**
 * Service for validating JWT tokens with security-service.
 *
 * @author Shailesh Halor
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JwtValidationService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Validate JWT token by calling security-service token info endpoint.
     *
     * @param token          the JWT token to validate
     * @param identityDomain the identity domain name
     * @param validationUrl  the security-service validation endpoint URL
     * @return TokenInfoDto containing token information including userRole
     * @throws TokenValidationException if validation fails
     */
    public TokenInfoDto validateToken(String token, String identityDomain, String validationUrl) {
        try {
            String url = validationUrl + "?access_token=" + token;
            log.debug("Validating token with security-service: {}", validationUrl);

            HttpHeaders headers = new HttpHeaders();
            if (StringUtils.hasText(identityDomain)) {
                headers.set(HeaderConstants.OAUTH_IDENTITY_DOMAIN_NAME, identityDomain);
            }

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<TokenInfoDto> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    TokenInfoDto.class
            );

            TokenInfoDto tokenInfo = response.getBody();

            if (tokenInfo == null) {
                throw new TokenValidationException(SecurityConstants.ERROR_EMPTY_VALIDATION_RESPONSE);
            }

            log.debug("Token validated successfully. Subject: {}, UserRole: {}",
                     tokenInfo.subject(), tokenInfo.userRole());

            return tokenInfo;

        } catch (HttpClientErrorException e) {
            String errorMessage = ErrorMessageExtractor.extractErrorMessage(e, objectMapper);
            log.error("Token validation failed: {}", errorMessage);
            throw new TokenValidationException(errorMessage, e);
        } catch (Exception e) {
            log.error("Token validation failed: ", e);
            throw new TokenValidationException(SecurityConstants.ERROR_TOKEN_VALIDATION_FAILED, e);
        }
    }
}

