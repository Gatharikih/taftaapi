package org.tafta.taftaapi.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tafta.taftaapi.repo.DBFunctionImpl;
import org.tafta.taftaapi.services.DataValidation;
import org.tafta.taftaapi.services.RoleService;
import org.tafta.taftaapi.services.UserService;
import org.tafta.taftaapi.utility.Utility;

import java.util.*;
import java.util.stream.Collectors;

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
    RoleService roleService;
    @Autowired
    DataValidation dataValidation;
    @Autowired
    DBFunctionImpl dbFunction;

    @RequestMapping(value ="/users/{user_id}", method = RequestMethod.GET)
    public ResponseEntity<Object> searchUserById(@PathVariable("user_id") String userId) {
        Map<String, Object> searchUserResponse = new HashMap<>();

        try {
            if (!userId.trim().isEmpty()) {
                searchUserResponse = userService.searchUserById(userId.trim());
            } else {
                searchUserResponse.put("response_code", "404");
                searchUserResponse.put("response_description", "Success");
                searchUserResponse.put("response_data", null);
            }
        } catch (Exception e) {
            log.error(e.getMessage());

            searchUserResponse.put("response_code", "500");
            searchUserResponse.put("response_description", "Internal Error");
            searchUserResponse.put("response_data", null);
        }

        return ResponseEntity
                .status(Integer.parseInt(String.valueOf(searchUserResponse.get("response_code"))))
                .body(searchUserResponse);
    }

    @RequestMapping(value ="/users", method = RequestMethod.GET)
    public ResponseEntity<Object> searchUserByEmailAndMsisdn(@RequestParam("search_term") String searchTerm) {
        Map<String, Object> searchUserResponse = new HashMap<>();

        try {
            if (!searchTerm.isEmpty()) {
                searchUserResponse = userService.searchUser(searchTerm);
            } else {
                searchUserResponse.put("response_code", "404");
                searchUserResponse.put("response_description", "User not found");
                searchUserResponse.put("response_data", null);
            }
        } catch (Exception e) {
            log.error(e.getMessage());

            searchUserResponse.put("response_code", "500");
            searchUserResponse.put("response_description", "Internal Error");
            searchUserResponse.put("response_data", null);
        }

        return ResponseEntity
                .status(Integer.parseInt(String.valueOf(searchUserResponse.get("response_code"))))
                .body(searchUserResponse);
    }

    @RequestMapping(value ="/users/list", method = RequestMethod.GET)
    public ResponseEntity<Object> listAllUsers(@RequestParam(value = "page_number", required = false) String pageNumber,
                                               @RequestParam(value = "status", required = false) String status) {
        Map<String, Object> listAllUsersResponse = new HashMap<>();

        try {
            Map<String, Object> params = new HashMap<>(){{
                put("page_number", pageNumber);
                put("status", status);
            }};

            params = Utility.cleanMap(params);

            listAllUsersResponse = userService.listAllUsers(params);
        } catch (Exception e) {
            log.error(e.getMessage());

            listAllUsersResponse.put("response_code", "500");
            listAllUsersResponse.put("response_description", "Internal Error");
            listAllUsersResponse.put("response_data", null);
        }

        return ResponseEntity
                .status(Integer.parseInt(String.valueOf(listAllUsersResponse.get("response_code"))))
                .body(listAllUsersResponse);
    }

    @RequestMapping(value ="/users/{user_id}", method = RequestMethod.PUT)
    public ResponseEntity<Object> updateUser(@PathVariable("user_id") String userId,
                                             @RequestBody Map<String, Object> body) {
        Map<String, Object> updateUserResponse = new HashMap<>();

        try {
            updateUserResponse = userService.updateUser(body, userId);
        } catch (Exception e) {
            log.error(e.getMessage());

            if (e.getCause() != null || e.getCause().getMessage().contains("duplicate key") || e.getCause().getMessage().contains("unique constraint")){
                updateUserResponse.put("response_code", "400");
                updateUserResponse.put("response_description", "Failed");
                updateUserResponse.put("errors", "Email/phone number already exists");
            }else {
                updateUserResponse.put("response_code", "500");
                updateUserResponse.put("response_description", "Failed");
                updateUserResponse.put("errors", "Internal Error");
            }
        }

        return ResponseEntity
                .status(Integer.parseInt(String.valueOf(updateUserResponse.get("response_code"))))
                .body(updateUserResponse);
    }

    @RequestMapping(value ="/users", method = RequestMethod.POST)
    public ResponseEntity<Object> createUser(@RequestHeader Map<String, Object> headers,
                                             @RequestBody Map<String, Object> body) {
        Map<String, Object> createUserResponse = new HashMap<>();

        try {
            List<String> requiredFields = new ArrayList<>(){{
                add("fullname");
                add("email");
                add("msisdn");
            }};

            Map<String, Object> dataValidationResult = dataValidation.areFieldsValid(body, requiredFields);

            if (Boolean.parseBoolean(dataValidationResult.get("valid").toString())) {
                createUserResponse = userService.createUser(body);
            } else {
                createUserResponse.put("response_code", "400");
                createUserResponse.put("response_description", "Failed");
                createUserResponse.put("errors", dataValidationResult.get("errors"));
            }
        } catch (Exception e) {
            log.error(e.getMessage());

            if (e.getCause() != null || e.getCause().getMessage().contains("duplicate key") ||
                    e.getCause().getMessage().contains("unique constraint")){
                createUserResponse.put("response_code", "400");
                createUserResponse.put("response_description", "Failed");
                createUserResponse.put("errors", "Email/phone number already exists");
            }else {
                createUserResponse.put("response_code", "500");
                createUserResponse.put("response_description", "Failed");
                createUserResponse.put("errors", "Internal Error");
            }
        }

        return ResponseEntity
                .status(Integer.parseInt(String.valueOf(createUserResponse.get("response_code"))))
                .body(createUserResponse);
    }

    @RequestMapping(value ="/users/{user_id}", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteUser(@PathVariable("user_id") String userId) {
        Map<String, Object> deleteUserResponse = new HashMap<>();

        try {
            if (!userId.trim().equalsIgnoreCase("")) {
                deleteUserResponse = userService.deleteUser(userId.trim());
            } else {
                deleteUserResponse.put("response_code", "404");
                deleteUserResponse.put("response_description", "User not found");
                deleteUserResponse.put("response_data", null);
            }
        } catch (Exception e) {
            log.error(e.getMessage());

            deleteUserResponse.put("response_code", "500");
            deleteUserResponse.put("response_description", "Internal Error");
            deleteUserResponse.put("response_data", null);
        }

        return ResponseEntity
                .status(Integer.parseInt(String.valueOf(deleteUserResponse.get("response_code"))))
                .body(deleteUserResponse);
    }

    @RequestMapping(value ="/test", method = RequestMethod.POST)
    public ResponseEntity<Object> test(@RequestBody Map<String, Object> body) {
        Map<String, Object> assignedPermissions = null;

        if (body.get("role") != null && !String.valueOf(body.get("role")).isEmpty()) {
            List<String> roles = Arrays.stream(String.valueOf(body.get("role")).split(",")).toList();
            List<Map<String, Object>> rolesToSearch = dbFunction.searchRoles(roles);

            String rolePermissions = rolesToSearch.stream()
                    .map(stringObjectMap -> String.valueOf(stringObjectMap.get("permissions")))
                    .collect(Collectors.joining(","));

            log.info("rolePermissions: " + rolePermissions);

            assignedPermissions = roleService.searchRolesPermissions(List.of(rolePermissions.split(",")));
        }

        log.info("assignedPermissions: " + assignedPermissions);

        return ResponseEntity
                .status(Integer.parseInt("200"))
                .body(assignedPermissions);
    }
}