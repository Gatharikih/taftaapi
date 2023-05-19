package org.tafta.taftaapi.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tafta.taftaapi.services.CompanyService;
import org.tafta.taftaapi.services.DataValidation;
import org.tafta.taftaapi.services.PermissionService;

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
    private DataValidation dataValidation;

    @RequestMapping(value ="/api/v1/permissions/permission/{permission_id}", method = RequestMethod.GET)
    public ResponseEntity<Object> getPermission(@PathVariable("permission_id") String permissionId) {
        try {
            if (!permissionId.trim().isEmpty()) {
                Map<String, Object> searchPermissionResponse = permissionService.searchPermissionById(permissionId.trim());

                return ResponseEntity.status(Integer.parseInt(searchPermissionResponse.get("response_code").toString())).body(searchPermissionResponse);
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

    @RequestMapping(value ="/api/v1/permissions/list", method = RequestMethod.GET)
    public ResponseEntity<Object> listAllPermissions(@RequestParam("page_number") Optional<String> pageNumber, @RequestParam("status") Optional<String> status) {
        try {
            Map<String, Object> searchMap = new HashMap<>();

            pageNumber.ifPresent(s -> searchMap.put("page_number", s));
            status.ifPresent(s -> searchMap.put("status", s));

            Map<String, Object> listAllPermissionsResponse = permissionService.listAllPermissions(searchMap);

            return ResponseEntity.status(Integer.parseInt(listAllPermissionsResponse.get("response_code").toString())).body(listAllPermissionsResponse);
        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity.status(500).body(new HashMap<>() {{
                put("response_code", "500");
                put("description", "Internal error occurred");
                put("data", null);
            }});
        }
    }

    @RequestMapping(value ="/api/v1/permissions/{permission_id}", method = RequestMethod.PUT)
    public ResponseEntity<Object> updatePermission(@PathVariable("permission_id") String permissionId, @RequestBody Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();

        try {
            Map<String, Object> updatePermissionResponse = permissionService.updatePermission(body, permissionId);

            return ResponseEntity.status(Integer.parseInt(updatePermissionResponse.get("response_code").toString())).body(updatePermissionResponse);
        } catch (Exception e) {
            e.printStackTrace();

            if (e.getCause() != null || e.getCause().getMessage().contains("duplicate key") || e.getCause().getMessage().contains("unique constraint")){
                response.put("response_code", "400");
                response.put("description", "Failed");
                response.put("errors", List.of(new HashMap<>() {{
                    put("description", "Record already exists");
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

    @RequestMapping(value ="/api/v1/permissions", method = RequestMethod.POST)
    public ResponseEntity<Object> createPermission(@RequestBody Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<String> requiredFields = new ArrayList<>();

            requiredFields.add("action");

            Map<String, Object> dataValidationResult = dataValidation.areFieldsValid(body, requiredFields);

            if (Boolean.parseBoolean(dataValidationResult.get("valid").toString())) {
                Map<String, Object> createPermissionResponse = permissionService.createPermission(body);

                return ResponseEntity.status(Integer.parseInt(createPermissionResponse.get("response_code").toString())).body(createPermissionResponse);
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
                    put("description", "Permission already exists");
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

    @RequestMapping(value ="/api/v1/permissions/{permission_id}", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deletePermission(@PathVariable("permission_id") String permissionId) {
        try {
            if (!permissionId.trim().equalsIgnoreCase("")) {
                Map<String, Object> deleteCompanyResponse = permissionService.deletePermission(permissionId.trim());

                return ResponseEntity.status(Integer.parseInt(deleteCompanyResponse.get("response_code").toString())).body(deleteCompanyResponse);
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