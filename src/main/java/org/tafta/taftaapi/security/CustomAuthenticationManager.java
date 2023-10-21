package org.tafta.taftaapi.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.tafta.taftaapi.services.SecurityService;

/**
 * @author Gathariki Ngigi
 * Created on October 21, 2023.
 * Time 0026h
 * <p>
 * Processes an Authentication request.
 */
@Slf4j
public class CustomAuthenticationManager implements AuthenticationManager {
    @Autowired
    UserSecurityDetailsService userSecurityDetailsService;
    @Autowired
    SecurityService securityService;

    /**
     * @param authentication Authentication object
     * @return Authentication object
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = String.valueOf(authentication.getCredentials());

        CustomUserDetails userDetails = userSecurityDetailsService.loadUserByUsername(username);

        if (userDetails.isEnabled()) {
            if (!(userDetails.getPassword().equals(password) && userDetails.getUsername().equals(username)
                    || (userDetails.getUsername().equals(username) && securityService.comparePasswords(password, userDetails.getPassword())))) {
                throw new BadCredentialsException("Invalid Credentials");
            } else {
                if (userDetails.isEnabled()) {
                    return successfulAuthentication(authentication, userDetails);
                }
            }
        }

        throw new BadCredentialsException("Invalid Credentials");
    }

    private Authentication successfulAuthentication(final Authentication authentication, final CustomUserDetails user) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                user, user.getPassword(), user.getAuthorities());

        usernamePasswordAuthenticationToken.setDetails(authentication.getDetails());

        return usernamePasswordAuthenticationToken;
    }
}
