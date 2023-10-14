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
 * Created on May 19, 2023.
 * Time 1603h
 */

@Slf4j
@Service
public class PermissionService {
    @Autowired
    DBFunctionImpl dbFunction;

    public Map<String, Object> createPermission(Map<String, Object> permissionParams){
        Map<String, Object> response = new HashMap<>();

        try {
            Map<String, Object> createPermissionResponse = dbFunction.createPermission(permissionParams);

            if(createPermissionResponse != null){
                response.put("response_code", "201");
                response.put("response_description", "Success");
                response.put("response_data", createPermissionResponse);
            }else{
                response.put("response_code", "200");
                response.put("response_description", "Record not updated");
                response.put("response_data", null);
            }
        } catch (Exception e) {
            log.error(e.getMessage());

            response.put("response_code", "500");
            response.put("response_description", "Internal Error");
            response.put("response_data", null);

            if(e.getMessage() != null && (e.getMessage().contains("violates unique") || e.getMessage().contains("duplicate key"))){
                response.put("response_code", "400");
                response.put("description", "Permission action already exists");
                response.put("data", null);
            }
        }

        return response;
    }

    public Map<String, Object> updatePermission(Map<String, Object> permissionParams, String permissionId){
        Map<String, Object> response = new HashMap<>();

        try {
            Map<String, Object> permissionResponse = dbFunction.searchPermissionById(permissionId);

            if (permissionResponse != null) {
                permissionParams.put("id", permissionResponse.getOrDefault("id", permissionId));

                Map<String, Object> updateCompanyResponse = dbFunction.updatePermission(permissionParams);

                if(updateCompanyResponse != null){
                    if(!updateCompanyResponse.isEmpty()){
                        response.put("response_code", "200");
                        response.put("response_description", "Success");
                        response.put("response_data", updateCompanyResponse);
                    }else{
                        response.put("response_code", "400");
                        response.put("response_description", "Unrecognized status");
                        response.put("response_data", null);
                    }
                }else{
                    response.put("response_code", "200");
                    response.put("response_description", "Record not updated");
                    response.put("response_data", null);
                }
            } else {
                response.put("response_code", "404");
                response.put("response_description", "Permission not found");
                response.put("response_data", null);
            }
        } catch (Exception e) {
            log.error(e.getMessage());

            response.put("response_code", "500");
            response.put("response_description", "Internal Error");
            response.put("response_data", null);
        }

        return response;
    }

    public Map<String, Object> listAllPermissions(Map<String, Object> queryParams){
        Map<String, Object> response = new HashMap<>();

        try {
            List<Map<String, Object>> listAllPermissionsResponse = dbFunction.listAllPermissions(queryParams);

            if(listAllPermissionsResponse != null){
                response.put("response_code", "200");
                response.put("response_description", "Success");
                response.put("response_data", listAllPermissionsResponse);
                response.put("page_size", listAllPermissionsResponse.size());
            }else{
                response.put("response_code", "404");
                response.put("response_description", "No permission found");
                response.put("response_data", null);
            }
        } catch (Exception e) {
            log.error(e.getMessage());

            response.put("response_code", "500");
            response.put("response_description", "Internal Error");
            response.put("response_data", null);
        }

        return response;
    }

    public Map<String, Object> searchPermissionById(String id){
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> searchPermissionResponse = dbFunction.searchPermissionById(id);

        if(searchPermissionResponse != null){
            response.put("response_code", "200");
            response.put("response_description", "Success");
            response.put("response_data", searchPermissionResponse);
        }else{
            response.put("response_code", "404");
            response.put("response_description", "No permission found");
            response.put("response_data", null);
        }

        return response;
    }

    public Map<String, Object> deletePermission(String id){
        Map<String, Object> response = new HashMap<>();

        try {
            Map<String, Object> searchPermissionResponse = dbFunction.searchPermissionById(id);

            if(searchPermissionResponse != null){
                Map<String, Object> deletePermissionResponse = dbFunction.deletePermission(id);

                if(deletePermissionResponse != null){
                    response.put("response_code", "200");
                    response.put("response_description", "Success");
                    response.put("response_data", deletePermissionResponse.get("id"));
                }else{
                    response.put("response_code", "200");
                    response.put("response_description", "Permission not deleted");
                    response.put("response_data", null);
                }
            }else{
                response.put("response_code", "404");
                response.put("response_description", "Permission not found");
                response.put("response_data", null);
            }
        } catch (Exception e) {
            log.error(e.getMessage());

            response.put("response_code", "500");
            response.put("response_description", "Internal Error");
            response.put("response_data", null);
        }

        return response;
    }
}