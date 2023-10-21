package org.tafta.taftaapi.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.tafta.taftaapi.utility.Utility;

/**
 * @author Gathariki Ngigi
 * Created on July 24, 2023.
 * Time 1456h
 *
 * <p>
 * The class provides configurations on how different API endpoints will be authorized & authenticated
 * <b>: Configuring SecurityFilterChain</b>
 * <p> Provide Description on how each end point should be handled in term of security</p>
 * @EnableMethodSecurity class level tag will allow method level access control Based on Role (RBAC)
 * This helps to determine what user can or cannot access (Authorization) having provided the right credential
 * (Authentication)
 */

@Configuration
@EnableMethodSecurity(jsr250Enabled = true)
@EnableWebMvc
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