package org.tafta.taftaapi.services;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Gathariki Ngigi
 * Created on September 05, 2023.
 * Time 1543h
 */

@Slf4j
@Service
public class SecurityService {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public SecurityService() {
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder(
                BCryptPasswordEncoder.BCryptVersion.$2A, 10, new SecureRandom("XXL".getBytes(StandardCharsets.UTF_8)));
    }

    public String encryptPassword(String plainPassword) {
        return bCryptPasswordEncoder.encode(plainPassword);
    }

    /** Compare two passwords
     * @param rawPassword the raw password to encode and match
     * @param encodedPassword the encoded password from data store to compare with
     * @return boolean
     * */
    public boolean comparePasswords(CharSequence rawPassword, String encodedPassword) {
        try {
            return new BCryptPasswordEncoder().matches(rawPassword, encodedPassword);
        } catch (Exception e) {
            log.error(e.getMessage());

            return false;
        }
    }
}