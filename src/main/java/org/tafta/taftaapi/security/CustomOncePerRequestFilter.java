package org.tafta.taftaapi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.tafta.taftaapi.requestfiltergate.CachedHttpRequestWrapper;
import org.tafta.taftaapi.utility.Utility;

import java.io.IOException;

/**
 * @author Gathariki Ngigi
 * Created on July 24, 2023.
 * Time 1444h
 * <p>
 * Help achieve printing of response
 */

@Component
@Slf4j
public class CustomOncePerRequestFilter extends OncePerRequestFilter {
    @Autowired
    CustomAuthenticationManager customAuthenticationManager;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws IOException {
        CachedHttpRequestWrapper cachedHttpRequestWrapper = new CachedHttpRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        String reqID = request.getRequestId();
        long startTime = System.currentTimeMillis();
        String msg = "Authenticated successfully";

        try {
            Authentication authentication = customAuthenticationManager.authenticateApiKey(request);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            if(authentication.isAuthenticated()){
                filterChain.doFilter(cachedHttpRequestWrapper, responseWrapper);
            }else {
                Utility.markResponseUnauthorised(response);
            }
        } catch (Exception e) {
            log.error(e.getMessage());

            msg = e.getMessage();

            if(e instanceof BadCredentialsException){
                Utility.markResponseUnauthorised(response);
            }
        }

        Utility.responseLogger(request, response, responseWrapper, reqID, startTime, msg);

        // !!! NEVER DELETE OR COMMENT THE LINE BLOW DO YOU RESEARCH COZ IT'S A WRONG STORY !!!
        responseWrapper.copyBodyToResponse();
    }
}