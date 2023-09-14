package org.tafta.taftaapi.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author Gathariki Ngigi
 * Created on July 24, 2023.
 * Time 1444h
 * <p>
 * Provide Authentication logics for request hitting th API
 * After successful Authentication a valid Authentication object is returned
 */

@Slf4j
public class ApiKeyAuthentication extends AbstractAuthenticationToken {
    public ApiKeyAuthentication(Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }
}