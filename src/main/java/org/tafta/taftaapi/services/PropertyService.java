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
    DBFunctionImpl dbFunction;

    public Map<String, Object> createProperty(Map<String, Object> propertyParams){
        Map<String, Object> response = new HashMap<>();

        try {
            Map<String, Object> createPropertyResponse = dbFunction.createProperty(propertyParams);

            if(createPropertyResponse != null && !createPropertyResponse.isEmpty()){
                response.put("response_code", "201");
                response.put("response_description", "Success");
                response.put("response_data", createPropertyResponse);
            }else{
                response.put("response_code", "400");
                response.put("response_description", "Property not updated");
                response.put("response_data", null);
            }
        } catch (Exception e) {
            log.error(e.getMessage());

            response.put("response_code", "500");
            response.put("response_description", "Internal Error");
            response.put("response_data", null);

            if(e.getMessage() != null && (e.getMessage().contains("violates unique") || e.getMessage().contains("duplicate key"))){
                response.put("response_code", "400");
                response.put("description", "Property already exists");
                response.put("data", null);
            }
        }

        return response;
    }

    public Map<String, Object> updateProperty(Map<String, Object> propertyParams){
        Map<String, Object> response = new HashMap<>();

        try {
            String propertyId = String.valueOf(propertyParams.get("property_id"));
            Map<String, Object> propertyResponse = dbFunction.searchPropertyById(propertyId);

            if (propertyResponse != null) {
                Map<String, Object> updatePropertyResponse = dbFunction.updateProperty(propertyParams);

                log.info("updatePropertyResponse: " + updatePropertyResponse);

                if(updatePropertyResponse != null){
                    if(!updatePropertyResponse.isEmpty()){
                        response.put("response_code", "200");
                        response.put("response_description", "Success");
                        response.put("response_data", updatePropertyResponse);
                    }else{
                        response.put("response_code", "400");
                        response.put("response_description", "Unrecognized status");
                        response.put("response_data", null);
                    }
                }else{
                    response.put("response_code", "400");
                    response.put("response_description", "Property not updated");
                    response.put("response_data", null);
                }
            } else {
                response.put("response_code", "404");
                response.put("response_description", "Property not found");
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

    public Map<String, Object> searchProperties(Map<String, Object> searchMap){
        Map<String, Object> response = new HashMap<>();

        try {
            List<Map<String, Object>> searchPropertiesResponse = dbFunction.searchProperties(searchMap);

            if(searchPropertiesResponse != null){
                response.put("response_code", "200");
                response.put("response_description", "Success");
                response.put("response_data", searchPropertiesResponse);
                response.put("page_size", searchPropertiesResponse.size());
            }else{
                response.put("response_code", "404");
                response.put("response_description", "No property found");
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

    public Map<String, Object> listAllProperties(Map<String, Object> queryParams){
        Map<String, Object> response = new HashMap<>();

        try {
            List<Map<String, Object>> listAllPropertiesResponse = dbFunction.listAllProperties(queryParams);

            if(listAllPropertiesResponse != null){
                response.put("response_code", "200");
                response.put("response_description", "Success");
                response.put("response_data", listAllPropertiesResponse);
                response.put("page_size", listAllPropertiesResponse.size());
            }else{
                response.put("response_code", "404");
                response.put("response_description", "No property found");
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

    public Map<String, Object> searchPropertyById(String id){
        Map<String, Object> response = new HashMap<>();

        try {
            Map<String, Object> searchPropertyResponse = dbFunction.searchPropertyById(id);

            if(searchPropertyResponse != null){
                response.put("response_code", "200");
                response.put("response_description", "Success");
                response.put("response_data", searchPropertyResponse);
            }else{
                response.put("response_code", "404");
                response.put("response_description", "No property found");
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

    public Map<String, Object> deleteProperty(String propertyId){
        Map<String, Object> response = new HashMap<>();

        try {
            Map<String, Object> searchPropertyResponse = dbFunction.searchPropertyById(propertyId);

            if(searchPropertyResponse != null){
                Map<String, Object> deletePropertyResponse = dbFunction.deleteProperty(propertyId);

                if(deletePropertyResponse != null){
                    response.put("response_code", "200");
                    response.put("response_description", "Success");
                    response.put("response_data", null);
                }else{
                    response.put("response_code", "400");
                    response.put("response_description", "Property not deleted");
                    response.put("response_data", null);
                }
            }else{
                response.put("response_code", "200");
                response.put("response_description", "Property not found");
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