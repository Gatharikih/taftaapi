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
    private DBFunctionImpl dbFunction;

    public Map<String, Object> createPermission(Map<String, Object> permissionParams){
        List<Map<String, Object>> createPermissionResponse = dbFunction.createPermission(permissionParams);

        if(createPermissionResponse != null && createPermissionResponse.size() > 0){
            return new HashMap<>() {{
                put("response_code", "201");
                put("description", "Success");
                put("data", createPermissionResponse);
            }};
        }else{
            return new HashMap<>() {{
                put("response_code", "200");
                put("description", "Record not updated");
                put("data", null);
            }};
        }
    }
    public Map<String, Object> updatePermission(Map<String, Object> permissionParams, String permissionId){
        Map<String, Object> permissionResponse = dbFunction.searchPermissionById(permissionId);

        if (permissionResponse != null) {
            permissionParams.put("id", permissionId);

            List<Map<String, Object>> updateCompanyResponse = dbFunction.updatePermission(permissionParams);

            if(updateCompanyResponse != null){
                if(updateCompanyResponse.size() > 0){
                    return new HashMap<>() {{
                        put("response_code", "201");
                        put("description", "Success");
                        put("data", updateCompanyResponse);
                    }};
                }else{
                    return new HashMap<>() {{
                        put("response_code", "400");
                        put("description", "Unrecognized status");
                        put("data", null);
                    }};
                }
            }else{
                return new HashMap<>() {{
                    put("response_code", "200");
                    put("description", "Record not updated");
                    put("data", null);
                }};
            }
        } else {
            return new HashMap<>() {{
                put("response_code", "404");
                put("description", "Permission not found");
                put("data", null);
            }};
        }
    }
    public Map<String, Object> listAllPermissions(Map<String, Object> queryParams){
        List<Map<String, Object>> listAllPermissionsResponse = dbFunction.listAllPermissions(queryParams);

        if(listAllPermissionsResponse != null){
            return new HashMap<>() {{
                put("response_code", "200");
                put("description", "Success");
                put("data", listAllPermissionsResponse);
                put("page_size", listAllPermissionsResponse.size());
            }};
        }else{
            return new HashMap<>() {{
                put("response_code", "404");
                put("description", "No permission found");
                put("data", null);
            }};
        }
    }
    public Map<String, Object> searchPermissionById(String id){
        Map<String, Object> searchPermissionResponse = dbFunction.searchPermissionById(id);

        if(searchPermissionResponse != null){
            return new HashMap<>() {{
                put("response_code", "200");
                put("description", "Success");
                put("data", searchPermissionResponse);
            }};
        }else{
            return new HashMap<>() {{
                put("response_code", "404");
                put("description", "No permission found");
                put("data", null);
            }};
        }
    }
    public Map<String, Object> deletePermission(String id){
        Map<String, Object> searchPermissionResponse = dbFunction.searchPermissionById(id);

        if(searchPermissionResponse != null){
            Map<String, Object> deletePermissionResponse = dbFunction.deletePermission(id);

            if(deletePermissionResponse != null){
                return new HashMap<>() {{
                    put("response_code", "200");
                    put("description", "Success");
                    put("data", null);
                }};
            }else{
                return new HashMap<>() {{
                    put("response_code", "200");
                    put("description", "Permission not deleted");
                    put("data", null);
                }};
            }
        }else{
            return new HashMap<>() {{
                put("response_code", "200");
                put("description", "Permission not found");
                put("data", null);
            }};
        }
    }
}