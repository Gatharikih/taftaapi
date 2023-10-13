package org.tafta.taftaapi.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tafta.taftaapi.services.CompanyService;
import org.tafta.taftaapi.services.DataValidation;
import org.tafta.taftaapi.services.PermissionService;
import org.tafta.taftaapi.utility.Utility;

import java.util.*;

/**
 * @author Gathariki Ngigi
 * Created on May 19, 2023.
 * Time 1602h
 */

@RestController
@Slf4j
public class PermissionController {
    @Autowired
    PermissionService permissionService;
    @Autowired
    DataValidation dataValidation;

    @RequestMapping(value ="/permissions/{permission_id}", method = RequestMethod.GET)
    public ResponseEntity<Object> getPermission(@PathVariable("permission_id") String permissionId) {
        Map<String, Object> searchPermissionResponse = new HashMap<>();

        try {
            if (!permissionId.trim().isEmpty()) {
                searchPermissionResponse = permissionService.searchPermissionById(permissionId.trim());
            } else {
                searchPermissionResponse.put("response_code", "404");
                searchPermissionResponse.put("response_description", "Success");
                searchPermissionResponse.put("response_data", null);
            }
        } catch (Exception e) {
            log.error(e.getMessage());

            searchPermissionResponse.put("response_code", "500");
            searchPermissionResponse.put("response_description", "Internal error occurred");
            searchPermissionResponse.put("response_data", null);
        }

        return ResponseEntity
                .status(Integer.parseInt(String.valueOf(searchPermissionResponse.get("response_code"))))
                .body(searchPermissionResponse);
    }

    @RequestMapping(value ="/permissions/list", method = RequestMethod.GET)
    public ResponseEntity<Object> listAllPermissions(@RequestParam(value = "page_number", required = false) String pageNumber,
                                                     @RequestParam(value = "status", required = false) String status) {
        Map<String, Object> listAllPermissionsResponse = new HashMap<>();

        try {
            Map<String, Object> searchMap = new HashMap<>(){{
                put("page_number", pageNumber);
                put("status", status);
            }};

            searchMap = Utility.cleanMap(searchMap);

            listAllPermissionsResponse = permissionService.listAllPermissions(searchMap);
        } catch (Exception e) {
            log.error(e.getMessage());

            listAllPermissionsResponse.put("response_code", "500");
            listAllPermissionsResponse.put("response_description", "Internal error occurred");
            listAllPermissionsResponse.put("response_data", null);
        }

        return ResponseEntity
                .status(Integer.parseInt(String.valueOf(listAllPermissionsResponse.get("response_code"))))
                .body(listAllPermissionsResponse);
    }

    @RequestMapping(value ="/permissions/{permission_id}", method = RequestMethod.PUT)
    public ResponseEntity<Object> updatePermission(@PathVariable("permission_id") String permissionId,
                                                   @RequestBody Map<String, Object> body) {
        Map<String, Object> updatePermissionResponse = new HashMap<>();

        try {
            body = Utility.cleanMap(body);

            updatePermissionResponse = permissionService.updatePermission(body, permissionId);
        } catch (Exception e) {
            log.error(e.getMessage());

            if (e.getCause() != null || e.getCause().getMessage().contains("duplicate key") ||
                    e.getCause().getMessage().contains("unique constraint")){
                updatePermissionResponse.put("response_code", "400");
                updatePermissionResponse.put("response_description", "Failed");
                updatePermissionResponse.put("errors", "Record already exists");
            }else {
                updatePermissionResponse.put("response_code", "500");
                updatePermissionResponse.put("response_description", "Failed");
                updatePermissionResponse.put("errors", "Internal error occurred");
            }
        }

        return ResponseEntity
                .status(Integer.parseInt(String.valueOf(updatePermissionResponse.get("response_code"))))
                .body(updatePermissionResponse);
    }

    @RequestMapping(value ="/permissions", method = RequestMethod.POST)
    public ResponseEntity<Object> createPermission(@RequestBody Map<String, Object> body) {
        Map<String, Object> createPermissionResponse = new HashMap<>();

        try {
            List<String> requiredFields = new ArrayList<>();

            requiredFields.add("action");

            Map<String, Object> dataValidationResult = dataValidation.areFieldsValid(body, requiredFields);

            if (Boolean.parseBoolean(dataValidationResult.get("valid").toString())) {
                createPermissionResponse = permissionService.createPermission(body);
            } else {
                createPermissionResponse.put("response_code", "400");
                createPermissionResponse.put("response_description", "Failed");
                createPermissionResponse.put("errors", String.valueOf(dataValidationResult.get("errors")));
            }
        } catch (Exception e) {
            log.error(e.getMessage());

            if (e.getCause() != null || e.getCause().getMessage().contains("duplicate key") || e.getCause().getMessage().contains("unique constraint")){
                createPermissionResponse.put("response_code", "400");
                createPermissionResponse.put("response_description", "Failed");
                createPermissionResponse.put("errors", List.of(new HashMap<>() {{
                    put("response_description", "Permission already exists");
                }}));
            }else {
                createPermissionResponse.put("response_code", "500");
                createPermissionResponse.put("response_description", "Failed");
                createPermissionResponse.put("errors", List.of(new HashMap<>() {{
                    put("response_description", "Internal error occurred");
                }}));
            }
        }

        return ResponseEntity
                .status(Integer.parseInt(String.valueOf(createPermissionResponse.get("response_code"))))
                .body(createPermissionResponse);
    }

    @RequestMapping(value ="/permissions/{permission_id}", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deletePermission(@PathVariable("permission_id") String permissionId) {
        Map<String, Object> deleteCompanyResponse = new HashMap<>();

        try {
            if (!permissionId.trim().isEmpty()) {
                deleteCompanyResponse = permissionService.deletePermission(permissionId.trim());
            } else {
                deleteCompanyResponse.put("response_code", "404");
                deleteCompanyResponse.put("response_description", "Success");
                deleteCompanyResponse.put("response_data", null);
            }
        } catch (Exception e) {
            log.error(e.getMessage());

            deleteCompanyResponse.put("response_code", "500");
            deleteCompanyResponse.put("response_description", "Internal error occurred");
            deleteCompanyResponse.put("response_data", null);
        }

        return ResponseEntity
                .status(Integer.parseInt(String.valueOf(deleteCompanyResponse.get("response_code"))))
                .body(deleteCompanyResponse);
    }
}