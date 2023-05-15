package org.tafta.taftaapi.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tafta.taftaapi.services.DataValidation;
import org.tafta.taftaapi.services.UserService;

import java.util.*;

/**
 * @author Gathariki Ngigi
 * Created on April 25, 2023.
 * Time 1514h
 */

@RestController
@Slf4j
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    private DataValidation dataValidation;

    @RequestMapping(value ="/api/v1/users/{user_id}", method = RequestMethod.GET)
    public ResponseEntity<Object> getUser() {
        return null;
    }

    // query param - scope(one, all), page(default = 1), per_page(default = 10), search, order(default = desc), orderby(default = date)
    @RequestMapping(value ="/api/v1/users", method = RequestMethod.GET)
    public ResponseEntity<Object> searchUser(@RequestParam("search_term") String searchTerm) {
        if (!searchTerm.isEmpty()) {
            Map<String, Object> searchUserResponse = userService.searchUserByEmailOrPhoneNumber(searchTerm);

            return ResponseEntity.status(Integer.parseInt(searchUserResponse.get("response_code").toString())).body(searchUserResponse);
        } else {
            return ResponseEntity.status(404).body(new HashMap<>() {{
                put("response_code", "404");
                put("description", "Success");
                put("data", null);
            }});
        }
    }

    @RequestMapping(value ="/api/v1/users", method = RequestMethod.PUT)
    public ResponseEntity<Object> updateUser(@RequestHeader Map<String, Object> headers, @RequestBody Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<String> requiredFields = new ArrayList<>();

            requiredFields.add("fullname");
            requiredFields.add("email");
            requiredFields.add("msisdn");

            Map<String, Object> dataValidationResult = dataValidation.areFieldsValid(body, requiredFields);

            if (Boolean.parseBoolean(dataValidationResult.get("valid").toString())) {
                Map<String, Object> updateUserResponse = userService.createOrUpdateUser(body, "update");

                return ResponseEntity.status(Integer.parseInt(updateUserResponse.get("response_code").toString()))
                        .body(updateUserResponse);
            } else {
                Map validationErrorMap = (Map) dataValidationResult.get("errors");

                response.put("response_code", "400");
                response.put("description", "Failed");
                response.put("errors", List.of(validationErrorMap.get("message")));

                return ResponseEntity.status(400).body(response);
            }
        } catch (Exception e) {
            e.printStackTrace();

            if (e.getCause().getMessage().contains("duplicate key") || e.getCause().getMessage().contains("unique constraint")){
                response.put("response_code", "400");
                response.put("description", "Failed");
                response.put("errors", List.of(new HashMap<>() {{
                    put("description", "Email/phone number already exists");
                }}));
            }else {
                response.put("response_code", "500");
                response.put("description", "Failed");
                response.put("errors", List.of(new HashMap<>() {{
                    put("description", "Internal error occurred");
                }}));
            }

            return ResponseEntity.status(Integer.parseInt(response.get("response_code").toString()))
                    .body(response);
        }
    }

    @RequestMapping(value ="/api/v1/users", method = RequestMethod.POST)
    public ResponseEntity<Object> createUser(@RequestHeader Map<String, Object> headers, @RequestBody Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<String> requiredFields = new ArrayList<>();

            requiredFields.add("fullname");
            requiredFields.add("email");
            requiredFields.add("msisdn");

            Map<String, Object> dataValidationResult = dataValidation.areFieldsValid(body, requiredFields);

            if (Boolean.parseBoolean(dataValidationResult.get("valid").toString())) {
                Map<String, Object> createUserResponse = userService.createOrUpdateUser(body, "create");

                return ResponseEntity.status(Integer.parseInt(createUserResponse.get("response_code").toString()))
                        .body(createUserResponse);
            } else {
                Map validationErrorMap = (Map) dataValidationResult.get("errors");

                response.put("response_code", "400");
                response.put("description", "Failed");
                response.put("errors", List.of(validationErrorMap.get("message")));

                return ResponseEntity.status(400).body(response);
            }
        } catch (Exception e) {
//            e.printStackTrace();

            if (e.getCause().getMessage().contains("duplicate key") || e.getCause().getMessage().contains("unique constraint")){
                response.put("response_code", "400");
                response.put("description", "Failed");
                response.put("errors", List.of(new HashMap<>() {{
                    put("description", "Email/phone number already exists");
                }}));
            }else {
                response.put("response_code", "500");
                response.put("description", "Failed");
                response.put("errors", List.of(new HashMap<>() {{
                    put("description", "Internal error occurred");
                }}));
            }

            return ResponseEntity.status(Integer.parseInt(response.get("response_code").toString()))
                    .body(response);
        }
    }

    @RequestMapping(value ="/api/v1/users/{user_id}", method = RequestMethod.PUT)
    public ResponseEntity<Object> updateUser() {
        return null;
    }
    @RequestMapping(value ="/api/v1/users/{user_id}", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteUser() {
        return null;
    }
}
