package org.tafta.taftaapi.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Gathariki Ngigi
 * Created on April 27, 2023.
 * Time 0905h
 */

@Slf4j
@Service
public class DataValidation {
    ObjectMapper mapper = new ObjectMapper();

    public boolean isEmailValid(String emailAddress) {
        String regexPattern = "^(\\w+)[@](\\w+)[.]([a-zA-Z_][a-zA-Z0-9_]*)$";

        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(emailAddress.trim());

        return matcher.matches();
    }

    /**
     * Check validity of mandatory headers
     * @param fieldsObj Object of the data .e.g. Map, JsonNode
     * @param requiredFields List of the mandatory fields
     * @return boolean
     */
    public Map<String, Object> areFieldsValid(Object fieldsObj, List<String> requiredFields){
        log.info("\n\n------------------ DATA VALIDATION - FIELDS VALIDATION ------------------\n");

        Map<String, Object> response = new HashMap<>();
        try {
            String[] schemes = new String[]{"http","https"};
            UrlValidator urlValidator = new UrlValidator(schemes);

            Map<String, Object> fieldsMap = null;

            if (fieldsObj instanceof Map){
                fieldsMap = mapper.convertValue(fieldsObj, new TypeReference<>(){});
            }

            if (fieldsObj instanceof JsonNode){
                fieldsMap = mapper.convertValue((mapper.convertValue(fieldsObj, JsonNode.class)), new TypeReference<>(){});
            }

            if (fieldsMap != null) {
                // specific field to be checked
                for (String eachRequiredField : requiredFields) {
//                    log.info(eachRequiredField + " : " + fieldsMap.get(eachRequiredField));

                    String firstCharOfFirstFieldName;
                    String restOfCharsOfFirstFieldName;
                    String firstStr;
                    StringBuilder secondStr = new StringBuilder();
                    String fieldName;
                    String[] splitFields = eachRequiredField.split("_");

                    if (splitFields.length > 1) {
                        firstCharOfFirstFieldName = String.valueOf(eachRequiredField.split("_")[0]
                                .charAt(0)).toUpperCase();
                        restOfCharsOfFirstFieldName = splitFields[0].substring(1).toLowerCase();

                        firstStr = firstCharOfFirstFieldName + restOfCharsOfFirstFieldName;

                        for (int i = 1; i < splitFields.length; i++) {
                            secondStr.append(splitFields[i].toLowerCase()).append(" ");
                        }

                        fieldName = firstStr + " " + secondStr;
                    }else {
                        firstCharOfFirstFieldName = String.valueOf(eachRequiredField.split("_")[0]
                                .charAt(0)).toUpperCase();
                        restOfCharsOfFirstFieldName = eachRequiredField.split("_")[0]
                                .substring(1).toLowerCase();

                        firstStr = firstCharOfFirstFieldName + restOfCharsOfFirstFieldName;

                        fieldName = firstStr;
                    }

                    if (Optional.ofNullable(fieldsMap.get(eachRequiredField)).orElse("").toString().isEmpty()) {
                        response.put("valid", "false");
                        response.put("errors", fieldName.trim() + " cannot be null/empty");

                        break;
                    }else { // Fields not empty
                        // Callback URL maximum characters check - 200 max
                        if(eachRequiredField.trim().equalsIgnoreCase("callback_url")){
                            if(String.valueOf(fieldsMap.get("callback_url")).length() > 200){
                                response.put("valid", "false");
                                response.put("errors", "Callback URL can exceeded 200 characters limit");
                            }

                            try {
                                if(!urlValidator.isValid(String.valueOf(fieldsMap.get("callback_url")))){
                                    response.put("valid", "false");
                                    response.put("errors", "Invalid callback URL");
                                }
                            } catch (Exception e) {
                                response.put("valid", "false");
                                response.put("errors", "Invalid callback URL");
                            }
                        }

                        // Creditor Identifier Type == BANK for other bank transfers, MSISDN for Mobile Money transfers & WALLET for AzamPesa
                        if(eachRequiredField.trim().equalsIgnoreCase("creditor_identifier_type")){
                            String creditorIdType = String.valueOf(fieldsMap.get("creditor_identifier_type"));

                            if(!(creditorIdType.equalsIgnoreCase("BANK") || creditorIdType.equalsIgnoreCase("MSISDN") ||
                                    creditorIdType.equalsIgnoreCase("WALLET"))){
                                response.put("valid", "false");
                                response.put("errors", "Wrong creditor identifier type. Required: BANK, MSISDN or WALLET for AzamPesa");
                            }
                        }
                    }
                }
            }else {
                response.put("valid", "false");
                response.put("errors", "Error parsing object - Only JSON and Map objects are allowed");
            }
        } catch (Exception e) {
            response.put("valid", "false");
            response.put("errors", "Error in data validation: " + e.getMessage());
        }

        if(response.get("errors") == null){
            response.put("valid", "true");
            response.put("errors", null);
        }

        return response;
    }
}