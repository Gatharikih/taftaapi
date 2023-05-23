package org.tafta.taftaapi.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tafta.taftaapi.services.DataValidation;
import org.tafta.taftaapi.services.PermissionService;
import org.tafta.taftaapi.services.RoleService;

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
    private DataValidation dataValidation;

    @RequestMapping(value ="/api/v1/roles/role/{role_id}", method = RequestMethod.GET)
    public ResponseEntity<Object> getRole(@PathVariable("role_id") String roleId) {
        try {
            if (!roleId.trim().isEmpty()) {
                Map<String, Object> searchRoleResponse = roleService.searchRoleById(roleId.trim());

                return ResponseEntity.status(Integer.parseInt(searchRoleResponse.get("response_code").toString())).body(searchRoleResponse);
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

    @RequestMapping(value ="/api/v1/roles/list", method = RequestMethod.GET)
    public ResponseEntity<Object> listAllRoles(@RequestParam("page_number") Optional<String> pageNumber, @RequestParam("status") Optional<String> status) {
        try {
            Map<String, Object> searchMap = new HashMap<>();

            pageNumber.ifPresent(s -> searchMap.put("page_number", s));
            status.ifPresent(s -> searchMap.put("status", s));

            Map<String, Object> listAllRolesResponse = roleService.listAllRoles(searchMap);

            return ResponseEntity.status(Integer.parseInt(listAllRolesResponse.get("response_code").toString())).body(listAllRolesResponse);
        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity.status(500).body(new HashMap<>() {{
                put("response_code", "500");
                put("description", "Internal error occurred");
                put("data", null);
            }});
        }
    }

    @RequestMapping(value ="/api/v1/roles/{role_id}", method = RequestMethod.PUT)
    public ResponseEntity<Object> updateRole(@PathVariable("role_id") String roleId, @RequestBody Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();

        try {
            Map<String, Object> updateRoleResponse = roleService.updateRole(body, roleId);

            return ResponseEntity.status(Integer.parseInt(updateRoleResponse.get("response_code").toString())).body(updateRoleResponse);
        } catch (Exception e) {
            e.printStackTrace();

            if (e.getCause() != null){
                if (e.getCause().getMessage().contains("duplicate key") || e.getCause().getMessage().contains("unique constraint")) {
                    response.put("response_code", "400");
                    response.put("description", "Failed");
                    response.put("errors", List.of(new HashMap<>() {{
                        put("description", "Record already exists");
                    }}));
                }

                if (e.getCause().getMessage().contains("foreign key")) {
                    response.put("response_code", "400");
                    response.put("description", "Failed");
                    response.put("errors", List.of(new HashMap<>() {{
                        put("description", "Role not found");
                    }}));
                }
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

    @RequestMapping(value ="/api/v1/roles", method = RequestMethod.POST)
    public ResponseEntity<Object> createRole(@RequestBody Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<String> requiredFields = new ArrayList<>();

            requiredFields.add("name");
            requiredFields.add("permissions");

            Map<String, Object> dataValidationResult = dataValidation.areFieldsValid(body, requiredFields);

            if (Boolean.parseBoolean(dataValidationResult.get("valid").toString())) {
                Map<String, Object> createRoleResponse = roleService.createRole(body);

                return ResponseEntity.status(Integer.parseInt(createRoleResponse.get("response_code").toString())).body(createRoleResponse);
            } else {
                Map validationErrorMap = (Map) dataValidationResult.get("errors");

                response.put("response_code", "400");
                response.put("description", "Failed");
                response.put("errors", List.of(validationErrorMap.get("message")));

                return ResponseEntity.status(400).body(response);
            }
        } catch (Exception e) {
            e.printStackTrace();

            if (e.getCause() != null && (e.getCause().getMessage().contains("duplicate key") || e.getCause().getMessage().contains("unique constraint"))){
                response.put("response_code", "400");
                response.put("description", "Failed");
                response.put("errors", List.of(new HashMap<>() {{
                    put("description", "Role already exists");
                }}));
            }else if (e.getCause() != null && e.getCause().getMessage().contains("foreign key constraint")){
                response.put("response_code", "400");
                response.put("description", "Failed");
                response.put("errors", List.of(new HashMap<>() {{
                    put("description", "Permission does not exist");
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

    @RequestMapping(value ="/api/v1/roles/{role_id}", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteRole(@PathVariable("role_id") String roleId) {
        try {
            if (!roleId.trim().equalsIgnoreCase("")) {
                Map<String, Object> deleteRoleResponse = roleService.deleteRole(roleId.trim());

                return ResponseEntity.status(Integer.parseInt(deleteRoleResponse.get("response_code").toString())).body(deleteRoleResponse);
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