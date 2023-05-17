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
 * Created on April 25, 2023.
 * Time 1518h
 */

@Slf4j
@Service
public class UserService {
    @Autowired
    private DBFunctionImpl dbFunction;

    public Map<String, Object> createUser(Map<String, Object> userParams){
        List<Map<String, Object>> createUserResponse = dbFunction.createUser(userParams);

        if(createUserResponse != null && createUserResponse.size() > 0){
            return new HashMap<>() {{
                put("response_code", "201");
                put("description", "Success");
                put("data", createUserResponse);
            }};
        }else{
            return new HashMap<>() {{
                put("response_code", "200");
                put("description", "Record not updated");
                put("data", null);
            }};
        }
    }
    public Map<String, Object> updateUser(Map<String, Object> userParams, String userId){
        Map<String, Object> userResponse = dbFunction.searchUserById(userId);

        if (userResponse != null) {
            userParams.putIfAbsent("id", userId);

            List<Map<String, Object>> updateUserResponse = dbFunction.updateUser(userParams);

            if(updateUserResponse != null){
                if(updateUserResponse.size() > 0){
                    return new HashMap<>() {{
                        put("response_code", "201");
                        put("description", "Success");
                        put("data", updateUserResponse);
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
                put("description", "User not found");
                put("data", null);
            }};
        }
    }
    public Map<String, Object> searchUserByEmailOrPhoneNumber(String searchTerm){
        List<Map<String, Object>> searchUserResponse = dbFunction.searchUserByEmailOrPhoneNumber(searchTerm);

        if(searchUserResponse != null){
            return new HashMap<>() {{
                put("response_code", "200");
                put("description", "Success");
                put("data", searchUserResponse);
            }};
        }else{
            return new HashMap<>() {{
                put("response_code", "404");
                put("description", "User not found");
                put("data", null);
            }};
        }
    }
    public Map<String, Object> listAllUsers(Map<String, Object> queryParams){
        List<Map<String, Object>> listAllUsersResponse = dbFunction.listAllUsers(queryParams);

        if(listAllUsersResponse != null){
            return new HashMap<>() {{
                put("response_code", "200");
                put("description", "Success");
                put("data", listAllUsersResponse);
            }};
        }else{
            return new HashMap<>() {{
                put("response_code", "404");
                put("description", "No user found");
                put("data", null);
            }};
        }
    }
    public Map<String, Object> searchUserById(String id){
        Map<String, Object> searchUserResponse = dbFunction.searchUserById(id);

        if(searchUserResponse != null){
            return new HashMap<>() {{
                put("response_code", "200");
                put("description", "Success");
                put("data", searchUserResponse);
            }};
        }else{
            return new HashMap<>() {{
                put("response_code", "404");
                put("description", "User not found");
                put("data", null);
            }};
        }
    }
    public Map<String, Object> deleteUser(String id){
        Map<String, Object> searchUserResponse = dbFunction.searchUserById(id);

        if(searchUserResponse != null){
            Map<String, Object> deleteUserResponse = dbFunction.deleteUser(id);

            if(deleteUserResponse != null){
                return new HashMap<>() {{
                    put("response_code", "200");
                    put("description", "Success");
                    put("data", null);
                }};
            }else{
                return new HashMap<>() {{
                    put("response_code", "200");
                    put("description", "User not deleted");
                    put("data", null);
                }};
            }
        }else{
            return new HashMap<>() {{
                put("response_code", "200");
                put("description", "User not deleted");
                put("data", null);
            }};
        }
    }
}