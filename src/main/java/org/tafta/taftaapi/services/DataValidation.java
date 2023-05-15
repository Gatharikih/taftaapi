package org.tafta.taftaapi.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tafta.taftaapi.repo.DBFunctionImpl;
import org.tafta.taftaapi.utility.Errors;

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
    @Autowired
    private DBFunctionImpl dbFunction;

    final String validationErrCode = "041400";

    public boolean isEmailValid(String emailAddress) {
        String regexPattern = "^(\\w+)[@](\\w+)[.]([a-zA-Z_][a-zA-Z0-9_]*)$";

        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(emailAddress.trim());

        return matcher.matches();
    }

    public Map<String, Object> areFieldsValid(Map<String, Object> fieldsMap, List<String> requiredFields){
        Map<String, Object> response = new HashMap<>();
        List<Map<String, String>> errors = new ArrayList<>();

        // specific field to be checked
        for (String eachRequiredField : requiredFields) {
            String firstCharOfFirstFieldName;
            String restOfCharsOfFirstFieldName;
            String firstStr;
            StringBuilder secondStr = new StringBuilder();
            String fieldName;
            String[] splitFields = eachRequiredField.split("_");

            if (splitFields.length > 1) {
                firstCharOfFirstFieldName = String.valueOf(eachRequiredField.split("_")[0].charAt(0)).toUpperCase();
                restOfCharsOfFirstFieldName = splitFields[0].substring(1).toLowerCase();

                firstStr = firstCharOfFirstFieldName + restOfCharsOfFirstFieldName;

                for (int i = 1; i < splitFields.length; i++) {
                    secondStr.append(splitFields[i].toLowerCase()).append(" ");
                }

                fieldName = firstStr + " " + secondStr;
            }else {
                firstCharOfFirstFieldName = String.valueOf(eachRequiredField.split("_")[0].charAt(0)).toUpperCase();
                restOfCharsOfFirstFieldName = eachRequiredField.split("_")[0].substring(1).toLowerCase();

                firstStr = firstCharOfFirstFieldName + restOfCharsOfFirstFieldName;

                fieldName = firstStr;
            }

            if (Optional.ofNullable(fieldsMap.get(eachRequiredField)).orElse("").toString().isEmpty() || fieldsMap.get(eachRequiredField) == null) {
                errors.add(Errors.get(validationErrCode, fieldName + " cannot be null/empty"));

                response.put("valid", "false");
                response.put("errors", Errors.get(validationErrCode, fieldName + "cannot be null/empty"));

                break;
            }else{
                if(eachRequiredField.equalsIgnoreCase("email")){
                    // check email format
                    if(!isEmailValid(fieldsMap.get(eachRequiredField).toString())){
                        errors.add(Errors.get(validationErrCode, "Email is in wrong format"));

                        response.put("valid", "false");
                        response.put("errors", Errors.get(validationErrCode, "Email is in wrong format"));
                    }
                }
            }
        }

        if(!(errors.size() > 0)){
            response.put("valid", "true");
            response.put("data", null);
        }

        return response;
    }
}
