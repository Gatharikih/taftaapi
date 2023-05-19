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
 * Created on May 17, 2023.
 * Time 0826h
 */

@Slf4j
@Service
public class PropertyService {
    @Autowired
    private DBFunctionImpl dbFunction;

    public Map<String, Object> createProperty(Map<String, Object> propertyParams){
        List<Map<String, Object>> createPropertyResponse = dbFunction.createProperty(propertyParams);

        if(createPropertyResponse != null && createPropertyResponse.size() > 0){
            return new HashMap<>() {{
                put("response_code", "201");
                put("description", "Success");
                put("data", createPropertyResponse);
            }};
        }else{
            return new HashMap<>() {{
                put("response_code", "200");
                put("description", "Record not updated");
                put("data", null);
            }};
        }
    }
    public Map<String, Object> updateProperty(Map<String, Object> propertyParams, String propertyId){
        Map<String, Object> propertyResponse = dbFunction.searchPropertyById(propertyId);

        if (propertyResponse != null) {
            List<Map<String, Object>> updatePropertyResponse = dbFunction.updateProperty(propertyParams);

            if(updatePropertyResponse != null){
                if(updatePropertyResponse.size() > 0){
                    return new HashMap<>() {{
                        put("response_code", "201");
                        put("description", "Success");
                        put("data", updatePropertyResponse);
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
                put("description", "Property not found");
                put("data", null);
            }};
        }
    }
    public Map<String, Object> searchProperties(Map<String, Object> searchMap){
        List<Map<String, Object>> searchPropertiesResponse = dbFunction.searchProperties(searchMap);

        if(searchPropertiesResponse != null){
            return new HashMap<>() {{
                put("response_code", "200");
                put("description", "Success");
                put("data", searchPropertiesResponse);
            }};
        }else{
            return new HashMap<>() {{
                put("response_code", "404");
                put("description", "No property found");
                put("data", null);
            }};
        }
    }
    public Map<String, Object> listAllProperties(Map<String, Object> queryParams){
        List<Map<String, Object>> listAllPropertiesResponse = dbFunction.listAllProperties(queryParams);

        if(listAllPropertiesResponse != null){
            return new HashMap<>() {{
                put("response_code", "200");
                put("description", "Success");
                put("data", listAllPropertiesResponse);
                put("page_size", listAllPropertiesResponse.size());
            }};
        }else{
            return new HashMap<>() {{
                put("response_code", "404");
                put("description", "No property found");
                put("data", null);
            }};
        }
    }
    public Map<String, Object> searchPropertyById(String id){
        Map<String, Object> searchPropertyResponse = dbFunction.searchPropertyById(id);

        if(searchPropertyResponse != null){
            return new HashMap<>() {{
                put("response_code", "200");
                put("description", "Success");
                put("data", searchPropertyResponse);
            }};
        }else{
            return new HashMap<>() {{
                put("response_code", "404");
                put("description", "No property found");
                put("data", null);
            }};
        }
    }
    public Map<String, Object> deleteProperty(String id){
        Map<String, Object> searchPropertyResponse = dbFunction.searchPropertyById(id);

        if(searchPropertyResponse != null){
            Map<String, Object> deletePropertyResponse = dbFunction.deleteProperty(id);

            if(deletePropertyResponse != null){
                return new HashMap<>() {{
                    put("response_code", "200");
                    put("description", "Success");
                    put("data", null);
                }};
            }else{
                return new HashMap<>() {{
                    put("response_code", "200");
                    put("description", "Property not deleted");
                    put("data", null);
                }};
            }
        }else{
            return new HashMap<>() {{
                put("response_code", "200");
                put("description", "Property not found");
                put("data", null);
            }};
        }
    }
}