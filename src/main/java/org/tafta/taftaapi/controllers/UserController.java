package org.tafta.taftaapi.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tafta.taftaapi.services.DataValidation;
import org.tafta.taftaapi.services.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<Object> searchUser(@RequestParam("email") String email) {
        Boolean isEmailValid = dataValidation.isEmailValid(email);

        log.error("isEmailValid : " + isEmailValid);
        ResponseEntity.status(200).body(new HashMap<>(){{
            put("is_email_valid", isEmailValid.toString());
        }});

        return null;
    }

    @RequestMapping(value ="/api/v1/users", method = {RequestMethod.POST, RequestMethod.PUT})
    public ResponseEntity<Object> createOrUpdateUser(@RequestBody Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<String> requiredFields = new ArrayList<>();

            requiredFields.add("fullname");
            requiredFields.add("email");
            requiredFields.add("msisdn");

            Map<String, Object> dataValidationResult = dataValidation.areFieldsValid(body, requiredFields);

            if (Boolean.parseBoolean(dataValidationResult.get("valid").toString())) {
                Map<String, Object> createOrUpdateUserResponse = userService.createOrUpdateUser(body);

                return ResponseEntity.status(Integer.parseInt(createOrUpdateUserResponse.get("response_code").toString()))
                        .body(createOrUpdateUserResponse);
            } else {
                Map validationErrorMap = (Map) dataValidationResult.get("errors");

                response.put("response_code", "400");
                response.put("description", "Failed");
                response.put("errors", List.of(validationErrorMap.get("message")));

                return ResponseEntity.status(400).body(response);
            }
        } catch (Exception e) {
            e.printStackTrace();

            response.put("response_code", "500");
            response.put("description", "Failed");
            response.put("errors",  List.of(new HashMap<>(){{
                put("description", "Internal error occurred");
            }}));

            return ResponseEntity.status(500).body(response);
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
