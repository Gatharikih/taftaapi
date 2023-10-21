package org.tafta.taftaapi.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.tafta.taftaapi.config.PropConfiguration;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Gathariki Ngigi
 * Created on July 24, 2023.
 * Time 1455h
 */

@Slf4j
@Component
public class Utility {
    @Autowired
    PropConfiguration propConfiguration;

    static ObjectMapper mapper = new ObjectMapper();

    public static String generateRandomFromUUID(){
        return UUID.randomUUID().toString();
    }

    public static String generateRandomAlphanumeric(int targetStringLength){
        try {
            int leftLimit = 48; // numeral '0'
            int rightLimit = 122; // letter 'z'
            SecureRandom random = new SecureRandom();

            return random.ints(leftLimit, rightLimit + 1)
                    .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                    .limit(targetStringLength)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint,
                            StringBuilder::append)
                    .toString();
        } catch (Exception e) {
            log.error(e.getMessage());

            return generateRandomFromUUID();
        }
    }

    public static String appInstanceActivityID() {
        return "ACT-" + UUID.randomUUID().toString().substring(0, 6) + "__" +
                LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) + "-REQ";
    }

    public static void responseLogger(HttpServletRequest request, HttpServletResponse response,
                                      ContentCachingResponseWrapper responseWrapper, String reqID, long startTime, String msg) {
        long timeTaken = System.currentTimeMillis() - startTime;
        String responseBody;
        int status = response.getStatus();

        // TODO: Blocked request body due to log size
        if (HttpStatus.valueOf(status).is2xxSuccessful()) {
            responseBody = ConstVal.LOG_BLOCK_MSG;
        }else{
            responseBody = getStringValue(responseWrapper.getContentAsByteArray(), response.getCharacterEncoding());
        }

        log.info("{} Finished Processing TimeTaken={} ResCode={} Response={}", Utility.appInstanceActivityID() +
                reqID, timeTaken, status, responseBody);
    }

    private static String getStringValue(byte[] contentAsByteArray, String characterEncoding) {
        try {
            return new String(contentAsByteArray, characterEncoding);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return "";
    }

    /**
     * Merge two Map Objects
     * @param map1 first map
     * @param map2 first map
     * @return Map
     * */
    public static Map<?, ?> combineTwoMaps(Map<?, ?> map1, Map<?, ?> map2){
        Map<?, ?> combinedMaps = Stream.concat(map1.entrySet().stream(), map2.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        log.info("combinedMaps2: " + combinedMaps);

        return combinedMaps;
    }

    public static void markResponseUnauthorised(HttpServletResponse response){
        try {
            Map<String, Object> unauthorizedPaymentResponseObj = new HashMap<>(){{
                put("response_code", "401");
                put("response_description", "UNAUTHORIZED");
            }};

            String error = mapper.writeValueAsString(unauthorizedPaymentResponseObj);

            response.reset();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setContentLength(error.length());
            response.getWriter().write(error);

            SecurityContextHolder.clearContext();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public SessionCreationPolicy getSessionCreationPolicy() {
        SessionCreationPolicy sessionCreationPolicy;

        if (propConfiguration.isApplicationLive()){
            sessionCreationPolicy = SessionCreationPolicy.STATELESS; // Block Session creation
        }else{
            sessionCreationPolicy = SessionCreationPolicy.IF_REQUIRED; // Allow Session creation
        }
        return sessionCreationPolicy;
    }

    public static Map<String, Object> cleanMap(Map<String, Object> map) {
        if (map != null) {
            Map<String, Object> retMap = new HashMap<>();

            for (Map.Entry<String, Object> param : map.entrySet()) {
                if (param.getValue() != null) {
                    retMap.put(param.getKey(), param.getValue());
                }
            }

            return retMap;
        }

        return null;
    }

    public static LinkedHashMap<String, Object> cleanMap(LinkedHashMap<String, Object> map) {
        if (map != null) {
            LinkedHashMap<String, Object> retMap = new LinkedHashMap<>();

            for (Map.Entry<String, Object> param : map.entrySet()) {
                if (param.getValue() != null) {
                    retMap.put(param.getKey(), param.getValue());
                }
            }

            return retMap;
        }

        return null;
    }

    /**
     * Format LocalDateTime based on the Pattern specified
     *
     * @param localDateTime time in LocalDateTime
     * @param datePattern   pattern to format the date
     * @return formatted date string
     * @see LocalDateTime
     * @see DateTimeFormatter
     */
    public static String formatDate(LocalDateTime localDateTime, String datePattern) {
        try {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(datePattern);
            return dateTimeFormatter.format(localDateTime);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return null;
    }

    /**
     * Parse given date-time string based on the Pattern specified
     *
     * @param dateTimeStr date-time to parse
     * @param datePattern pattern to format the date
     * @return LocalDateTime
     * @see LocalDateTime
     * @see DateTimeFormatter
     */
    public static LocalDateTime parseDate(String dateTimeStr, String datePattern) {
        try {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(datePattern);
            return LocalDateTime.parse(dateTimeStr, dateTimeFormatter);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return null;
    }

    /** Remove fields in a Map
     * @param mapToModify the Map object to modify
     * @param fieldsToPurge List of keys to remove from the map
     * @return Map
     * */
    public static Object removeFieldsFromMap(Map<String, Object> mapToModify, List<String> fieldsToPurge) {
        try {
            if (mapToModify != null && !mapToModify.isEmpty() && fieldsToPurge != null && !fieldsToPurge.isEmpty()) {
                mapToModify.entrySet().removeIf(stringObjectEntry -> fieldsToPurge.contains(stringObjectEntry.getKey()));
            }
        } catch (Exception e) {
            log.error("removeFieldsFromMap: " + e.getMessage());
        }

        return mapToModify;
    }

    public static Map<String, Object> getLimitAndOffset(int limit, Map<String, Object> queryParams){
        int offset = (Integer.parseInt(String.valueOf(queryParams.getOrDefault("page_number", "0"))) - 1) * limit;

        return new HashMap<>(){{
            put("limit", limit);
            put("offset", queryParams.get("page_number") != null ? Math.max(offset, 0) : 0);
        }};
    }

    public static JsonNode jsonNode(Object objectToConvert){
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readTree(mapper.writeValueAsString(objectToConvert));

        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public static JsonNode  jsonNodeFromStr(String data){
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readTree(data);

        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            return null;
        }
    }
    public static String jsonString(Object objectToConvert){
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(objectToConvert);

        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public static <T> T objectOfType(Object objectToConvert){
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(objectToConvert);
            return (T) mapper.convertValue(json, objectToConvert.getClass());

        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            return null;
        }

    }

    /** Get the elements that are not similar
     * @param greaterList
     * @param smallerList
     * @return ArrayList - Elements that are not contained in both lists
     * */
    public static List<Object> itemsNotPresentInList(List<Object> greaterList, List<Object> smallerList){
        List<Object> unlikeElements = new ArrayList<>();
        int count = 0;

        List<Object> greaterList_sorted  = greaterList.stream().sorted().toList();
        List<Object> smallerList_sorted = smallerList.stream().sorted().toList();

        while (count < greaterList_sorted.size()) {
            for (int i = 0; i < greaterList_sorted.size(); i++) {
                if ((i + 1) <= smallerList_sorted.size() ) {
                    if(!greaterList_sorted.get(i).equals(smallerList_sorted.get(i))){
                        unlikeElements.add(greaterList_sorted.get(i));
                    }
                } else {
                    unlikeElements.add(greaterList_sorted.get(i));
                }

                count = count + 1;
            }
        }

        return unlikeElements;
    }
}