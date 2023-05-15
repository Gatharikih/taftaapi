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

    public Map<String, Object> createOrUpdateUser(Map<String, Object> userParams, String method){
        if(method.equalsIgnoreCase("create")){
            List<Map<String, Object>> createUserResponse = dbFunction.createUser(userParams);

            if(createUserResponse.size() > 0){
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
        }else {
            List<Map<String, Object>> updateUserResponse = dbFunction.updateUser(userParams);

            if(updateUserResponse.size() > 0){
                return new HashMap<>() {{
                    put("response_code", "201");
                    put("description", "Success");
                    put("data", updateUserResponse);
                }};
            }else{
                return new HashMap<>() {{
                    put("response_code", "200");
                    put("description", "Record not updated");
                    put("data", null);
                }};
            }
        }
    }
}
