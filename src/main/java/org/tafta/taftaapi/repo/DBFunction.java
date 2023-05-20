package org.tafta.taftaapi.repo;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;
import java.util.Map;

/**
 * @author Gathariki Ngigi
 * Created on April 27, 2023.
 * Time 0906h
 */

public interface DBFunction {
    List<Map<String, Object>> createUser(Map<String, Object> entryParams);
    List<Map<String, Object>> updateUser(Map<String, Object> entryParams);
    List<Map<String, Object>> getUserRoleId(String roleName);
    List<Map<String, Object>> searchUserByEmailOrPhoneNumber(String searchTerm);
    List<Map<String, Object>> listAllUsers(Map<String, Object> queryParams);
    Map<String, Object> searchUserById(String id);
    Map<String, Object> deleteUser(String id);

    Map<String, Object> searchPropertyById(String id);
    List<Map<String, Object>> searchProperties(Map<String, Object> searchMap);
    Map<String, Object> deleteProperty(String id);
    List<Map<String, Object>> createProperty(Map<String, Object> entryParams);
    List<Map<String, Object>> updateProperty(Map<String, Object> entryParams);
    List<Map<String, Object>> listAllProperties(Map<String, Object> queryParams);

    Map<String, Object> searchCompanyById(String id);
    List<Map<String, Object>> searchCompanies(Map<String, Object> searchMap);
    Map<String, Object> deleteCompany(String id);
    List<Map<String, Object>> createCompany(Map<String, Object> entryParams);
    List<Map<String, Object>> updateCompany(Map<String, Object> entryParams);
    List<Map<String, Object>> listAllCompanies(Map<String, Object> queryParams);

    Map<String, Object> searchPermissionById(String id);
    Map<String, Object> deletePermission(String id);
    List<Map<String, Object>> createPermission(Map<String, Object> entryParams);
    List<Map<String, Object>> updatePermission(Map<String, Object> entryParams);
    List<Map<String, Object>> listAllPermissions(Map<String, Object> queryParams);

    Map<String, Object> searchRoleById(String id);
    Map<String, Object> deleteRole(String id);
    List<Map<String, Object>> createRole(Map<String, Object> entryParams) throws JsonProcessingException;
    List<Map<String, Object>> updateRole(Map<String, Object> entryParams);
    List<Map<String, Object>> listAllRoles(Map<String, Object> queryParams);
}