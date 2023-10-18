package org.tafta.taftaapi.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tafta.taftaapi.services.DataValidation;
import org.tafta.taftaapi.services.PermissionService;
import org.tafta.taftaapi.services.RoleService;
import org.tafta.taftaapi.utility.Utility;

import java.util.*;

/**
 * @author Gathariki Ngigi
 * Created on May 20, 2023.
 * Time 0927h
 */

@RestController
@Slf4j
public class RoleController {
    @Autowired
    RoleService roleService;
    @Autowired
    DataValidation dataValidation;

    @RequestMapping(value ="/roles/{role_id}", method = RequestMethod.GET)
    public ResponseEntity<Object> getRole(@PathVariable("role_id") String roleId) {
        Map<String, Object> searchRoleResponse = new HashMap<>();

        try {
            if (!roleId.trim().isEmpty()) {
                searchRoleResponse = roleService.searchRoleById(roleId.trim());
            } else {
                searchRoleResponse.put("response_code", "404");
                searchRoleResponse.put("response_description", "Success");
                searchRoleResponse.put("data", null);
            }
        } catch (Exception e) {
            log.error(e.getMessage());

            searchRoleResponse.put("response_code", "500");
            searchRoleResponse.put("response_description", "Internal error");
            searchRoleResponse.put("data", null);
        }

        return ResponseEntity
                .status(Integer.parseInt(String.valueOf(searchRoleResponse.get("response_code"))))
                .body(searchRoleResponse);
    }

    @RequestMapping(value ="/roles/list", method = RequestMethod.GET)
    public ResponseEntity<Object> listAllRoles(@RequestParam(value = "page_number", required = false) String pageNumber,
                                               @RequestParam(value = "status", required = false) String status) {
        Map<String, Object> listAllRolesResponse = new HashMap<>();

        try {
            Map<String, Object> searchMap = new HashMap<>(){{
                put("page_number", pageNumber);
                put("status", status);
            }};

            searchMap = Utility.cleanMap(searchMap);

            listAllRolesResponse = roleService.listAllRoles(searchMap);
        } catch (Exception e) {
            log.error(e.getMessage());

            listAllRolesResponse.put("response_code", "500");
            listAllRolesResponse.put("response_description", "Internal error");
            listAllRolesResponse.put("data", null);
        }

        return ResponseEntity
                .status(Integer.parseInt(String.valueOf(listAllRolesResponse.get("response_code"))))
                .body(listAllRolesResponse);
    }

    @RequestMapping(value ="/roles/{role_id}", method = RequestMethod.PUT)
    public ResponseEntity<Object> updateRole(@PathVariable("role_id") String roleId,
                                             @RequestBody Map<String, Object> body) {
        Map<String, Object> updateRoleResponse = new HashMap<>();

        try {
            updateRoleResponse = roleService.updateRole(body, roleId);
        } catch (Exception e) {
            log.error(e.getMessage());

            if (e.getCause() != null){
                if (e.getCause().getMessage().contains("duplicate key") || e.getCause().getMessage().contains("unique constraint")) {
                    updateRoleResponse.put("response_code", "400");
                    updateRoleResponse.put("response_description", "Failed");
                    updateRoleResponse.put("errors", "Record already exists");
                } else if (e.getCause().getMessage().contains("foreign key")) {
                    updateRoleResponse.put("response_code", "400");
                    updateRoleResponse.put("response_description", "Failed");
                    updateRoleResponse.put("errors", "Permission not found");
                }else{
                    updateRoleResponse.put("response_code", "500");
                    updateRoleResponse.put("response_description", "Failed");
                    updateRoleResponse.put("errors", "Internal error");
                }
            }else {
                updateRoleResponse.put("response_code", "500");
                updateRoleResponse.put("response_description", "Failed");
                updateRoleResponse.put("errors", "Internal error");
            }
        }

        return ResponseEntity.status(Integer.parseInt(String.valueOf(updateRoleResponse.get("response_code"))))
                .body(updateRoleResponse);
    }

    @RequestMapping(value ="/roles", method = RequestMethod.POST)
    public ResponseEntity<Object> createRole(@RequestBody Map<String, Object> body) {
        Map<String, Object> createRoleResponse = new HashMap<>();

        try {
            List<String> requiredFields = new ArrayList<>();

            requiredFields.add("name");
            requiredFields.add("permissions");

            Map<String, Object> dataValidationResult = dataValidation.areFieldsValid(body, requiredFields);

            if (Boolean.parseBoolean(dataValidationResult.get("valid").toString())) {
                createRoleResponse = roleService.createRole(body);
            } else {
                createRoleResponse.put("response_code", "400");
                createRoleResponse.put("response_description", "Failed");
                createRoleResponse.put("errors", dataValidationResult.get("errors"));
            }
        } catch (Exception e) {
            log.error(e.getMessage());

            if (e.getCause() != null && (e.getCause().getMessage().contains("duplicate key") ||
                    e.getCause().getMessage().contains("unique constraint"))){
                createRoleResponse.put("response_code", "400");
                createRoleResponse.put("response_description", "Failed");
                createRoleResponse.put("errors", "Role already exists");
            }else if (e.getCause() != null && e.getCause().getMessage().contains("foreign key constraint")){
                createRoleResponse.put("response_code", "400");
                createRoleResponse.put("response_description", "Failed");
                createRoleResponse.put("errors", "Permission does not exist");
            }else {
                createRoleResponse.put("response_code", "500");
                createRoleResponse.put("response_description", "Failed");
                createRoleResponse.put("errors", "Internal error");
            }
        }

        return ResponseEntity
                .status(Integer.parseInt(String.valueOf(createRoleResponse.get("response_code"))))
                .body(createRoleResponse);
    }

    @RequestMapping(value ="/roles/{role_id}", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteRole(@PathVariable("role_id") String roleId) {
        Map<String, Object> deleteRoleResponse = new HashMap<>();

        try {
            if (!roleId.trim().isEmpty()) {
                deleteRoleResponse = roleService.deleteRole(roleId.trim());
            } else {
                deleteRoleResponse.put("response_code", "200");
                deleteRoleResponse.put("response_description", "Success");
                deleteRoleResponse.put("data", null);
            }
        } catch (Exception e) {
            log.error(e.getMessage());

            deleteRoleResponse.put("response_code", "500");
            deleteRoleResponse.put("response_description", "Internal error");
            deleteRoleResponse.put("data", null);
        }

        return ResponseEntity
                .status(Integer.parseInt(String.valueOf(deleteRoleResponse.get("response_code"))))
                .body(deleteRoleResponse);
    }
}