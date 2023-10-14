package org.tafta.taftaapi.services;

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

    public Map<String, Object> createRole(Map<String, Object> roleParams){
        Map<String, Object> response = new HashMap<>();

        try {
            Map<String, Object> createRoleResponse = dbFunction.createRole(roleParams);

            if(createRoleResponse != null){
                response.put("response_code", "201");
                response.put("description", "Success");
                response.put("data", createRoleResponse);
            }else{
                response.put("response_code", "200");
                response.put("description", "Record not created");
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
                roleParams.put("id", roleId);
                roleParams.put("role_id", roleResponse.get("role_id").toString());

                Map<String, Object> updateRoleResponse = dbFunction.updateRole(roleParams);

                if(updateRoleResponse != null){
                    response.put("response_code", "201");
                    response.put("description", "Success");
                    response.put("data", null);
                }else{
                    response.put("response_code", "200");
                    response.put("description", "Record not updated");
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
                    response.put("response_code", "200");
                    response.put("description", "Role not deleted");
                    response.put("data", null);
                }
            }else{
                response.put("response_code", "200");
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
}