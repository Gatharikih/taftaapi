package org.tafta.taftaapi.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tafta.taftaapi.repo.DBFunctionImpl;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Gathariki Ngigi
 * Created on April 25, 2023.
 * Time 1518h
 */

@Slf4j
@Service
public class UserService {
    @Autowired
    DBFunctionImpl dbFunction;

    public Map<String, Object> createUser(Map<String, Object> userParams){
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> createUserResponse = dbFunction.createUser(userParams);

        if(createUserResponse != null && !createUserResponse.isEmpty()){
            response.put("response_code", "201");
            response.put("response_description", "Success");
            response.put("response_data", createUserResponse);
        }else{
            response.put("response_code", "200");
            response.put("response_description", "Record not updated");
            response.put("response_data", null);
        }

        return response;
    }

    public Map<String, Object> updateUser(Map<String, Object> userParams, String userId){
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> userResponse = dbFunction.searchUserById(userId);

        if (userResponse != null) {
            userParams.putIfAbsent("id", userId);

            List<Map<String, Object>> updateUserResponse = dbFunction.updateUser(userParams);

            if(updateUserResponse != null){
                if(!updateUserResponse.isEmpty()){
                    response.put("response_code", "201");
                    response.put("response_description", "Success");
                    response.put("response_data", updateUserResponse);
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
            response.put("response_description", "User not found");
            response.put("response_data", null);
        }

        return response;
    }

    public Map<String, Object> searchUser(String searchTerm){
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> searchUserResponse = dbFunction.searchUser(searchTerm);

        if(searchUserResponse != null){
            response.put("response_code", "200");
            response.put("response_description", "Success");
            response.put("response_data", searchUserResponse);
        }else{
            response.put("response_code", "404");
            response.put("response_description", "User not found");
            response.put("response_data", null);
        }

        return response;
    }

    public Map<String, Object> listAllUsers(Map<String, Object> queryParams){
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> listAllUsersResponse = dbFunction.listAllUsers(queryParams);

        if(listAllUsersResponse != null){
            response.put("response_code", "200");
            response.put("response_description", "Success");
            response.put("response_data", listAllUsersResponse);
            response.put("page_size", listAllUsersResponse.size());
        }else{
            response.put("response_code", "404");
            response.put("response_description", "No user found");
            response.put("response_data", null);
        }

        return response;
    }

    public Map<String, Object> searchUserById(String id){
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> searchUserResponse = dbFunction.searchUserById(id);

        if(searchUserResponse != null){
            response.put("response_code", "200");
            response.put("response_description", "Success");
            response.put("response_data", searchUserResponse);
        }else{
            response.put("response_code", "404");
            response.put("response_description", "User not found");
            response.put("response_data", null);
        }

        return response;
    }

    public Map<String, Object> deleteUser(String id){
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> searchUserResponse = dbFunction.searchUserById(id);

        if(searchUserResponse != null){
            Map<String, Object> deleteUserResponse = dbFunction.deleteUser(id);

            if(deleteUserResponse != null){
                response.put("response_code", "200");
                response.put("response_description", "Success");
                response.put("response_data", null);
            }else{
                response.put("response_code", "200");
                response.put("response_description", "User not deleted");
                response.put("response_data", null);
            }
        }else{
            response.put("response_code", "200");
            response.put("response_description", "User not found");
            response.put("response_data", null);
        }

        return response;
    }
}