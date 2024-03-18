package org.tafta.taftaapi.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.stereotype.Service;
import org.tafta.taftaapi.models.Property;

import java.lang.reflect.Field;
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
        log.info("------------------ DATA VALIDATION - FIELDS VALIDATION ------------------");

        Map<String, Object> response = new HashMap<>();
        List<String> errors = new ArrayList<>();

        try {
            String[] schemes = new String[]{"http","https"};
            UrlValidator urlValidator = new UrlValidator(schemes);

            Map<String, Object> fieldsMap;

            if (fieldsObj instanceof Map){
                fieldsMap = mapper.convertValue(fieldsObj, new TypeReference<>(){});
            } else if (fieldsObj instanceof JsonNode){
                fieldsMap = mapper.convertValue((mapper.convertValue(fieldsObj, JsonNode.class)), new TypeReference<>(){});
            } else {
                throw new RuntimeException("Object not recognized - only Map and JsonNode allowed");
            }

            if (fieldsMap != null) {
                // specific field to be checked
                for (String eachRequiredField : requiredFields) {
                    // log.info(eachRequiredField + " : " + fieldsMap.get(eachRequiredField));

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
                        errors.add(fieldName.trim() + " cannot be null/empty");

                        // break;
                    }else { // Fields not empty
                        // Callback URL maximum characters check - 200 max
                        if(eachRequiredField.trim().equalsIgnoreCase("callback_url")){
                            if(String.valueOf(fieldsMap.get("callback_url")).length() > 200){
                                errors.add("Callback URL can exceeded 200 characters limit");
                            }

                            try {
                                if(!urlValidator.isValid(String.valueOf(fieldsMap.get("callback_url")))){
                                    errors.add("Invalid callback URL");
                                }
                            } catch (Exception e) {
                                errors.add("Invalid callback URL");
                            }
                        }

                        // Creditor Identifier Type == BANK for other bank transfers, MSISDN for Mobile Money transfers & WALLET for AzamPesa
                        if(eachRequiredField.trim().equalsIgnoreCase("creditor_identifier_type")){
                            String creditorIdType = String.valueOf(fieldsMap.get("creditor_identifier_type"));

                            if(!(creditorIdType.equalsIgnoreCase("BANK") || creditorIdType.equalsIgnoreCase("MSISDN") ||
                                    creditorIdType.equalsIgnoreCase("WALLET"))){
                                errors.add("Wrong creditor identifier type. Required: BANK, MSISDN or WALLET for AzamPesa");
                            }
                        }
                    }
                }
            } else {
                errors.add("Error parsing object - Only JSON and Map objects are allowed");
            }
        } catch (Exception e) {
            errors.add("Error in data validation: " + e.getMessage());
        }

        response.put("errors", errors);

        if(errors.isEmpty()){
            response.put("valid", "true");
        } else {
            response.put("valid", "false");
        }

        return response;
    }

    /** Property data validation
     * @param property Pojo to evaluate
     * */
    public Map<String, Object> validatePropertyObject(Property property) {
        log.info("------------------ PROPERTY DATA VALIDATION --------------------");

        Map<String, Object> response = new HashMap<>(){{
            put("valid", "false");
        }};

        List<String> errors = new ArrayList<>();

        try{
            if (property.getCompany() == null || property.getCompany().isBlank()){
                errors.add("Company associated with this property cannot be empty/null");
            }

            if (property.getLatitude() == null || property.getLatitude().isBlank()){
                errors.add("Latitude cannot be empty/null");
            }

            if (property.getLongitude() == null || property.getLongitude().isBlank()){
                errors.add("Longitude cannot be empty/null");
            }

            if (property.getPrice_range() == null){
                errors.add("Longitude cannot be empty/null");
            } else {
                if (property.getPrice_range()){
                    if (property.getMaximum_price() == null || property.getMaximum_price().isBlank()){
                        errors.add("Property maximum price cannot be empty/null");
                    } else if (property.getMinimum_price() == null || property.getMinimum_price().isBlank()){
                        errors.add("Property minimum price cannot be empty/null");
                    }
                }else {
                    if (property.getPrice() == null || property.getPrice().isBlank()){
                        errors.add("Property price cannot be empty/null");
                    }
                }
            }

            if (property.getPhotos() == null || property.getPhotos().isEmpty()){
                errors.add("Photos cannot be empty/null");
            }

            if (property.getAmenities() == null || property.getAmenities().isBlank()){
                errors.add("Amenities cannot be empty/null");
            }

            if (property.getDescription() == null || property.getDescription().isBlank()){
                errors.add("Property description cannot be empty/null");
            }

            if (property.getProperty_id() == null || property.getProperty_id().isBlank()){
                errors.add("Property ID cannot be empty/null");
            }

            if (property.getName() == null || property.getName().isBlank()){
                errors.add("Property name cannot be empty/null");
            }

            if (property.getType() == null || property.getType().isBlank()){
                errors.add("Property type cannot be empty/null");
            }
        }catch (Exception e){
            log.error(e.getMessage());

            errors.add(e.getMessage());
        }

        response.put("errors", errors);

        if (errors.isEmpty()){
            response.put("valid", "true");
        }

        return response;
    }
}