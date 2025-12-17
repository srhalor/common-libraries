package com.shdev.security.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Utility class for parsing and converting user roles.
 *
 * @author Shailesh Halor
 */
@Slf4j
public class RoleParser {

    private RoleParser() {
        // Utility class - prevent instantiation
    }

    /**
     * Parse colon-separated role string from security-service into list.
     * Example: "ADMIN:USER:MANAGER" → ["ADMIN", "USER", "MANAGER"]
     *
     * @param userRole colon-separated role string
     * @return list of role strings
     */
    public static List<String> parseRoles(String userRole) {
        List<String> roles = new ArrayList<>();

        if (userRole == null || userRole.trim().isEmpty()) {
            log.debug("No roles provided, returning empty list");
            return roles;
        }

        String[] roleParts = userRole.split(":");
        for (String role : roleParts) {
            String trimmedRole = role.trim();
            if (!trimmedRole.isEmpty()) {
                roles.add(trimmedRole);
            }
        }

        log.debug("Parsed {} roles from userRole string: {}", roles.size(), roles);
        return roles;
    }

    /**
     * Convert role list to Spring Security GrantedAuthority collection.
     * Automatically adds "ROLE_" prefix if not present.
     *
     * @param roles list of role strings
     * @return collection of GrantedAuthority
     */
    public static Collection<GrantedAuthority> toAuthorities(List<String> roles) {
        if (roles == null || roles.isEmpty()) {
            log.debug("No roles provided, returning default ROLE_USER");
            return List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }

        List<GrantedAuthority> authorities = new ArrayList<>();

        for (String role : roles) {
            String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role;
            authorities.add(new SimpleGrantedAuthority(authority));
        }

        log.debug("Converted {} roles to {} authorities", roles.size(), authorities.size());
        return authorities;
    }

    /**
     * Parse colon-separated roles and convert to Spring Security authorities.
     * Convenience method combining parseRoles and toAuthorities.
     *
     * @param userRole colon-separated role string from security-service
     * @return collection of GrantedAuthority
     */
    public static Collection<GrantedAuthority> parseAndConvertToAuthorities(String userRole) {
        List<String> roles = parseRoles(userRole);
        return toAuthorities(roles);
    }
}

