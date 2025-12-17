package com.shdev.security.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Custom authentication token for JWT-based authentication.
 * This token is set in Spring Security context after JWT validation.
 *
 * @author Shailesh Halor
 */
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final String principal;
    private final Object credentials;

    /**
     * Creates an authenticated token with authorities.
     *
     * @param principal the authenticated user identifier
     * @param authorities the user's granted authorities (roles/permissions)
     */
    public JwtAuthenticationToken(String principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = null;
        setAuthenticated(true);
    }

    /**
     * Creates an unauthenticated token (for authentication process).
     *
     * @param principal the user identifier
     * @param credentials the credentials (token)
     */
    public JwtAuthenticationToken(String principal, Object credentials) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
        setAuthenticated(false);
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}

