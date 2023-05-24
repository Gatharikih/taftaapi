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
    private DBFunctionImpl dbFunction;

    public Map<String, Object> createRole(Map<String, Object> roleParams){
        Map<String, Object> createRoleResponse = dbFunction.createRole(roleParams);

        if(createRoleResponse != null){
            return new HashMap<>() {{
                put("response_code", "201");
                put("description", "Success");
                put("data", createRoleResponse);
            }};
        }else{
            return new HashMap<>() {{
                put("response_code", "200");
                put("description", "Record not updated");
                put("data", null);
            }};
        }
    }
    public Map<String, Object> updateRole(Map<String, Object> roleParams, String roleId){
        Map<String, Object> roleResponse = dbFunction.searchRoleById(roleId);

        if (roleResponse != null) {
            roleParams.put("id", roleId);
            roleParams.put("role_id", roleResponse.get("role_id").toString());

            Map<String, Object> updateRoleResponse = dbFunction.updateRole(roleParams);

            if(updateRoleResponse != null){
                return new HashMap<>() {{
                    put("response_code", "201");
                    put("description", "Success");
                    put("data", null);
                }};
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
                put("description", "Role not found");
                put("data", null);
            }};
        }
    }
    public Map<String, Object> listAllRoles(Map<String, Object> queryParams){
        List<Map<String, Object>> listAllRolesResponse = dbFunction.listAllRoles(queryParams);

        if(listAllRolesResponse != null){
            return new HashMap<>() {{
                put("response_code", "200");
                put("description", "Success");
                put("data", listAllRolesResponse);
                put("page_size", listAllRolesResponse.size());
            }};
        }else{
            return new HashMap<>() {{
                put("response_code", "404");
                put("description", "No role found");
                put("data", null);
            }};
        }
    }
    public Map<String, Object> searchRoleById(String id){
        Map<String, Object> searchRoleResponse = dbFunction.searchRoleById(id);

        if(searchRoleResponse != null){
            return new HashMap<>() {{
                put("response_code", "200");
                put("description", "Success");
                put("data", searchRoleResponse);
            }};
        }else{
            return new HashMap<>() {{
                put("response_code", "404");
                put("description", "No role found");
                put("data", null);
            }};
        }
    }
    public Map<String, Object> deleteRole(String id){
        Map<String, Object> searchRoleResponse = dbFunction.searchRoleById(id);

        if(searchRoleResponse != null){
            Map<String, Object> deleteRoleResponse = dbFunction.deleteRole(id);

            if(deleteRoleResponse != null){
                return new HashMap<>() {{
                    put("response_code", "200");
                    put("description", "Success");
                    put("data", null);
                }};
            }else{
                return new HashMap<>() {{
                    put("response_code", "200");
                    put("description", "Role not deleted");
                    put("data", null);
                }};
            }
        }else{
            return new HashMap<>() {{
                put("response_code", "200");
                put("description", "Role not found");
                put("data", null);
            }};
        }
    }
}