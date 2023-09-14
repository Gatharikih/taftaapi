package org.tafta.taftaapi.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.tafta.taftaapi.utility.Utility;

/**
 * @author Gathariki Ngigi
 * Created on July 24, 2023.
 * Time 1456h
 */

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {
    @Autowired
    CustomOncePerRequestFilter perRequestFilter;
    @Autowired
    Utility utility;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        /* ------------------------------------------------------------------------------------------------------------
            To Prevent Access Of the application using JSESSIONID Cookie on live it should be disabled
            JSESSIONID cookie is important only when accessing application over the browser which is only applicable
            when accessing Swagger documentation since it is secured. On live environment the Swagger is expected to be
             disabled, thus no session generation is required.

            Controlled Over Environment variable API_ENV_LIVE.
         --------------------------------------------------------------------------------------------------------------
         */

        SessionCreationPolicy sessionCreationPolicy = utility.getSessionCreationPolicy();
//        SessionCreationPolicy sessionCreationPolicy = SessionCreationPolicy.STATELESS;

        http.csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->
//                        authorizationManagerRequestMatcherRegistry.anyRequest().permitAll()) // TODO: Remove after security implementation
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->
                        authorizationManagerRequestMatcherRegistry.anyRequest().authenticated())
                .sessionManagement(httpSecuritySessionManagementConfigurer ->
                        httpSecuritySessionManagementConfigurer.sessionCreationPolicy(sessionCreationPolicy))
                .addFilterBefore(perRequestFilter, UsernamePasswordAuthenticationFilter.class);
//

        return http.build();
    }
}