package org.tafta.taftaapi.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tafta.taftaapi.repo.DBFunctionImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Gathariki Ngigi
 * Created on May 20, 2023.
 * Time 0928h
 */

@Slf4j
@Service
public class RoleService {
    @Autowired
    DBFunctionImpl dbFunction;

    ObjectMapper mapper = new ObjectMapper();

    public Map<String, Object> createRole(Map<String, Object> roleParams){
        Map<String, Object> response = new HashMap<>();

        try {
            Map<String, Object> createRoleResponse = dbFunction.createRole(roleParams);

            if(createRoleResponse != null){
                response.put("response_code", "201");
                response.put("description", "Success");
                response.put("data", createRoleResponse);
            }else{
                response.put("response_code", "400");
                response.put("description", "Role not created - assign active permissions");
                response.put("data", null);
            }
        } catch (Exception e) {
            log.error(e.getMessage());

            response.put("response_code", "500");
            response.put("description", "Internal error");
            response.put("data", null);

            if(e.getMessage() != null && (e.getMessage().contains("violates unique") || e.getMessage().contains("duplicate key"))){
                response.put("response_code", "400");
                response.put("description", "Role action already exists");
                response.put("data", null);
            }
        }

        return response;
    }

    public Map<String, Object> updateRole(Map<String, Object> roleParams, String roleId){
        Map<String, Object> response = new HashMap<>();

        try {
            Map<String, Object> roleResponse = dbFunction.searchRoleById(roleId);

            if (roleResponse != null) {
                roleParams.put("id", roleResponse.get("id"));

                Map<String, Object> updateRoleResponse = dbFunction.updateRole(roleParams);

                if(updateRoleResponse != null){
                    response.put("response_code", "200");
                    response.put("description", "Success");
                    response.put("data", updateRoleResponse.get("id"));
                }else{
                    response.put("response_code", "400");
                    response.put("description", "Role not updated");
                    response.put("data", null);
                }
            } else {
                response.put("response_code", "404");
                response.put("description", "Role not found");
                response.put("data", null);
            }
        } catch (Exception e) {
            log.error(e.getMessage());

            response.put("response_code", "500");
            response.put("description", "Internal error");
            response.put("data", null);
        }

        return response;
    }

    public Map<String, Object> listAllRoles(Map<String, Object> queryParams){
        Map<String, Object> response = new HashMap<>();

        try {
            List<Map<String, Object>> listAllRolesResponse = dbFunction.listAllRoles(queryParams);

            if(listAllRolesResponse != null){
                response.put("response_code", "200");
                response.put("description", "Success");
                response.put("data", listAllRolesResponse);
                response.put("page_size", listAllRolesResponse.size());
            }else{
                response.put("response_code", "404");
                response.put("description", "No role found");
                response.put("data", null);
            }
        } catch (Exception e) {
            log.error(e.getMessage());

            response.put("response_code", "500");
            response.put("description", "Internal error");
            response.put("data", null);
        }

        return response;
    }

    public Map<String, Object> searchRoleById(String id){
        Map<String, Object> response = new HashMap<>();

        try {
            Map<String, Object> searchRoleResponse = dbFunction.searchRoleById(id);

            if(searchRoleResponse != null){
                response.put("response_code", "200");
                response.put("description", "Success");
                response.put("data", searchRoleResponse);
            }else{
                response.put("response_code", "404");
                response.put("description", "No role found");
                response.put("data", null);
            }
        } catch (Exception e) {
            log.error(e.getMessage());

            response.put("response_code", "500");
            response.put("description", "Internal error");
            response.put("data", null);
        }

        return response;
    }

    public Map<String, Object> deleteRole(String id){
        Map<String, Object> response = new HashMap<>();

        try {
            Map<String, Object> searchRoleResponse = dbFunction.searchRoleById(id);

            if(searchRoleResponse != null){
                Map<String, Object> deleteRoleResponse = dbFunction.deleteRole(id);

                if(deleteRoleResponse != null){
                    response.put("response_code", "200");
                    response.put("description", "Success");
                    response.put("data", deleteRoleResponse.get("id"));
                }else{
                    response.put("response_code", "400");
                    response.put("description", "Role not deleted");
                    response.put("data", null);
                }
            }else{
                response.put("response_code", "400");
                response.put("description", "Role not found");
                response.put("data", null);
            }
        } catch (Exception e) {
            log.error(e.getMessage());

            response.put("response_code", "500");
            response.put("description", "Internal error");
            response.put("data", null);
        }

        return response;
    }

    public Map<String, Object> searchRolePermissions(String roleId){
        Map<String, Object> response = new HashMap<>();

        try {
            Map<String, Object> thisUserRole = dbFunction.searchRoleById(roleId);

            if(thisUserRole != null){
                List<String> permissionsProvidedStr = mapper.convertValue(thisUserRole.get("permissions"), new TypeReference<>() {});

                if (permissionsProvidedStr != null && !permissionsProvidedStr.isEmpty()) {
                    List<String> permissions = permissionsProvidedStr.stream().map(String::trim).toList();
                    List<Map<String, Object>> permissionsFound = dbFunction.searchPermissions(permissions);

                    if(permissionsFound != null && !permissionsFound.isEmpty()){
                        response.put("response_code", "200");
                        response.put("response_description", "Success");
                        response.put("response_data", permissionsFound);
                    }else{
                        response.put("response_code", "404");
                        response.put("response_description", "User role permissions not found");
                        response.put("response_data", null);
                    }
                } else {
                    response.put("response_code", "404");
                    response.put("response_description", "No permissions not found");
                    response.put("response_data", null);
                }
            }else{
                response.put("response_code", "404");
                response.put("response_description", "User role not found");
                response.put("response_data", null);
            }
        } catch (Exception e) {
            log.error(e.getMessage());

            response.put("response_code", "500");
            response.put("response_description", "Internal error");
            response.put("response_data", null);
        }

        return response;
    }

    public Map<String, Object> searchRolesPermissions(List<String> permissions){
        Map<String, Object> response = new HashMap<>();

        try {
            if(permissions != null && !permissions.isEmpty()){
                List<Map<String, Object>> permissionsFound = dbFunction.findAllPermissionsAssignedToRoles(permissions);

                if(!permissionsFound.isEmpty()){
                    response.put("response_code", "200");
                    response.put("response_description", "Success");
                    response.put("response_data", permissionsFound);
                }else{
                    response.put("response_code", "404");
                    response.put("response_description", "Permissions not found");
                    response.put("response_data", null);
                }
            }else{
                response.put("response_code", "404");
                response.put("response_description", "No roles provided");
                response.put("response_data", null);
            }
        } catch (Exception e) {
            log.error(e.getMessage());

            response.put("response_code", "500");
            response.put("response_description", "Internal error");
            response.put("response_data", null);
        }

        return response;
    }
}