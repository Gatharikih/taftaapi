package org.tafta.taftaapi.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tafta.taftaapi.services.DataValidation;
import org.tafta.taftaapi.services.PropertyService;
import org.tafta.taftaapi.services.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Gathariki Ngigi
 * Created on May 17, 2023.
 * Time 0826h
 */

@RestController
@Slf4j
public class PropertyController {
    @Autowired
    PropertyService propertyService;
    @Autowired
    private DataValidation dataValidation;

    @RequestMapping(value ="/api/v1/properties/property/{property_id}", method = RequestMethod.GET)
    public ResponseEntity<Object> getProperty(@PathVariable("property_id") String propertyId) {
        try {
            if (!propertyId.trim().isEmpty()) {
                Map<String, Object> searchPropertyResponse = propertyService.searchPropertyById(propertyId.trim());

                return ResponseEntity.status(Integer.parseInt(searchPropertyResponse.get("response_code").toString())).body(searchPropertyResponse);
            } else {
                return ResponseEntity.status(404).body(new HashMap<>() {{
                    put("response_code", "404");
                    put("description", "Success");
                    put("data", null);
                }});
            }
        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity.status(500).body(new HashMap<>() {{
                put("response_code", "500");
                put("description", "Internal error occurred");
                put("data", null);
            }});
        }
    }

    @RequestMapping(value ="/api/v1/properties", method = RequestMethod.GET)
    public ResponseEntity<Object> searchProperties(@RequestParam("search_term") String searchTerm) {
        try {
            if (!searchTerm.isEmpty()) {
                Map<String, Object> searchPropertiesResponse = propertyService.searchProperties(searchTerm);

                return ResponseEntity.status(Integer.parseInt(searchPropertiesResponse.get("response_code").toString())).body(searchPropertiesResponse);
            } else {
                return ResponseEntity.status(404).body(new HashMap<>() {{
                    put("response_code", "404");
                    put("description", "Success");
                    put("data", null);
                }});
            }
        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity.status(500).body(new HashMap<>() {{
                put("response_code", "500");
                put("description", "Internal error occurred");
                put("data", null);
            }});
        }
    }

//    // query param - scope(one, all), page(default = 1), per_page(default = 10), search, order(default = desc), order by(default = date)
//    @RequestMapping(value ="/api/v1/users/list", method = RequestMethod.GET)
//    public ResponseEntity<Object> listAllUsers(@RequestParam("page_number") String pageNumber, @RequestParam("status") String status) {
//        try {
//            Map<String, Object> listAllUsersResponse = userService.listAllUsers(new HashMap<>(){{
//                put("page_number", pageNumber);
//                put("status", status);
//            }});
//
//            return ResponseEntity.status(Integer.parseInt(listAllUsersResponse.get("response_code").toString())).body(listAllUsersResponse);
//        } catch (Exception e) {
//            e.printStackTrace();
//
//            return ResponseEntity.status(500).body(new HashMap<>() {{
//                put("response_code", "500");
//                put("description", "Internal error occurred");
//                put("data", null);
//            }});
//        }
//    }
//
//    @RequestMapping(value ="/api/v1/users/{user_id}", method = RequestMethod.PUT)
//    public ResponseEntity<Object> updateUser(@PathVariable("user_id") String userId, @RequestBody Map<String, Object> body) {
//        Map<String, Object> response = new HashMap<>();
//
//        try {
//            Map<String, Object> updateUserResponse = userService.updateUser(body, userId);
//
//            return ResponseEntity.status(Integer.parseInt(updateUserResponse.get("response_code").toString()))
//                    .body(updateUserResponse);
//        } catch (Exception e) {
//            e.printStackTrace();
//
//            log.error("error here");
//
//            if (e.getCause() != null || e.getCause().getMessage().contains("duplicate key") || e.getCause().getMessage().contains("unique constraint")){
//                response.put("response_code", "400");
//                response.put("description", "Failed");
//                response.put("errors", List.of(new HashMap<>() {{
//                    put("description", "Email/phone number already exists");
//                }}));
//            }else {
//                response.put("response_code", "500");
//                response.put("description", "Failed");
//                response.put("errors", List.of(new HashMap<>() {{
//                    put("description", "Internal error occurred");
//                }}));
//            }
//
//            return ResponseEntity.status(Integer.parseInt(response.get("response_code").toString()))
//                    .body(response);
//        }
//    }
//
    @RequestMapping(value ="/api/v1/properties", method = RequestMethod.POST)
    public ResponseEntity<Object> createProperty(@RequestBody Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<String> requiredFields = new ArrayList<>();

            requiredFields.add("fullname");
            requiredFields.add("email");
            requiredFields.add("msisdn");

            Map<String, Object> dataValidationResult = dataValidation.areFieldsValid(body, requiredFields);

            if (Boolean.parseBoolean(dataValidationResult.get("valid").toString())) {
                Map<String, Object> createUserResponse = propertyService.createProperty(body);

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
            e.printStackTrace();

            if (e.getCause() != null || e.getCause().getMessage().contains("duplicate key") || e.getCause().getMessage().contains("unique constraint")){
                response.put("response_code", "400");
                response.put("description", "Failed");
                response.put("errors", List.of(new HashMap<>() {{
                    put("description", "Property already exists");
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

    @RequestMapping(value ="/api/v1/properties/{property_id}", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteProperty(@PathVariable("property_id") String propertyId) {
        try {
            if (!propertyId.trim().equalsIgnoreCase("")) {
                Map<String, Object> deletePropertyResponse = propertyService.deleteProperty(propertyId.trim());

                return ResponseEntity.status(Integer.parseInt(deletePropertyResponse.get("response_code").toString())).body(deletePropertyResponse);
            } else {
                return ResponseEntity.status(404).body(new HashMap<>() {{
                    put("response_code", "404");
                    put("description", "Success");
                    put("data", null);
                }});
            }
        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity.status(500).body(new HashMap<>() {{
                put("response_code", "500");
                put("description", "Internal error occurred");
                put("data", null);
            }});
        }
    }
}