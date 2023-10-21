package org.tafta.taftaapi.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tafta.taftaapi.services.DataValidation;
import org.tafta.taftaapi.services.PropertyService;
import org.tafta.taftaapi.utility.Utility;

import java.util.*;

/**
 * @author Gathariki Ngigi
 * Created on May 17, 2023.
 * Time 0826h
 */

@RestController
@Slf4j
public class PropertyController {
    @Autowired
    PropertyService propertyService;
    @Autowired
    DataValidation dataValidation;
    ObjectMapper mapper = new ObjectMapper();

    @RequestMapping(value ="/properties/{property_id}", method = RequestMethod.GET)
    public ResponseEntity<Object> getProperty(@PathVariable("property_id") String propertyId) {
        Map<String, Object> searchPropertyResponse = new HashMap<>();

        try {
            if (propertyId != null && !propertyId.trim().isEmpty()) {
                searchPropertyResponse = propertyService.searchPropertyById(propertyId.trim());
            } else {
                searchPropertyResponse.put("response_code", "404");
                searchPropertyResponse.put("response_description", "Property ID not provided");
                searchPropertyResponse.put("response_data", null);
            }
        } catch (Exception e) {
            log.error(e.getMessage());

            searchPropertyResponse.put("response_code", "500");
            searchPropertyResponse.put("response_description", "Internal error occurred");
            searchPropertyResponse.put("response_data", null);
        }

        return ResponseEntity
                .status(Integer.parseInt(searchPropertyResponse.get("response_code").toString()))
                .body(searchPropertyResponse);
    }

    @RequestMapping(value ="/properties", method = RequestMethod.GET)
    public ResponseEntity<Object> searchProperties(@RequestParam(value = "county", required = false) String county,
                                                   @RequestParam(value = "max_price", required = false) String maxPrice,
                                                   @RequestParam(value = "min_price", required = false) String minPrice,
                                                   @RequestParam(value = "location", required = false) String location) {
        Map<String, Object> searchPropertiesResponse = new HashMap<>();

        try {
            Map<String, Object> searchMap = new HashMap<>();

            searchMap.put("county", county != null ? county.trim().toLowerCase() : null);
            searchMap.put("max_price", maxPrice != null ? maxPrice.trim().toLowerCase() : null);
            searchMap.put("min_price", minPrice != null ? minPrice.trim().toLowerCase() : null);
            searchMap.put("location", location != null ? location.trim().toLowerCase() : null);

            searchMap = Utility.cleanMap(searchMap);

            searchPropertiesResponse = propertyService.searchProperties(searchMap);
        } catch (Exception e) {
            log.error(e.getMessage());

            searchPropertiesResponse.put("response_code", "500");
            searchPropertiesResponse.put("response_description", "Internal error occurred");
            searchPropertiesResponse.put("response_data", null);
        }

        return ResponseEntity
                .status(Integer.parseInt(searchPropertiesResponse.get("response_code").toString()))
                .body(searchPropertiesResponse);
    }

    @RequestMapping(value ="/properties/list", method = RequestMethod.GET)
    public ResponseEntity<Object> listAllProperties(@RequestParam(value = "page_number", required = false) String pageNumber,
                                                    @RequestParam(value = "status", required = false) String status) {
        Map<String, Object> listAllPropertiesResponse = new HashMap<>();

        try {
            Map<String, Object> searchMap = new HashMap<>();

            searchMap.put("page_number", pageNumber);
            searchMap.put("status", status);

            searchMap = Utility.cleanMap(searchMap);

            log.info("searchMap: " + searchMap);

            listAllPropertiesResponse = propertyService.listAllProperties(searchMap);
        } catch (Exception e) {
            log.error(e.getMessage());

            listAllPropertiesResponse.put("response_code", "500");
            listAllPropertiesResponse.put("response_description", "Internal error occurred");
            listAllPropertiesResponse.put("response_data", null);
        }

        return ResponseEntity
                .status(Integer.parseInt(listAllPropertiesResponse.get("response_code").toString()))
                .body(listAllPropertiesResponse);
    }

    @RequestMapping(value ="/properties/{property_id}", method = RequestMethod.PUT)
    public ResponseEntity<Object> updateProperty(@PathVariable("property_id") String propertyId,
                                                 @RequestBody Map<String, Object> body) {
        Map<String, Object> updatePropertyResponse = new HashMap<>();

        try {
            if (propertyId != null && !propertyId.trim().isEmpty()) {
                body = Utility.cleanMap(body);

                body.put("property_id", propertyId.trim());

                updatePropertyResponse = propertyService.updateProperty(body);
            } else {
                updatePropertyResponse.put("response_code", "404");
                updatePropertyResponse.put("response_description", "Property ID not provided");
                updatePropertyResponse.put("response_data", null);
            }
        } catch (Exception e) {
            log.error(e.getMessage());

            if (e.getCause() != null || e.getCause().getMessage().contains("duplicate key") || e.getCause().getMessage().contains("unique constraint")){
                updatePropertyResponse.put("response_code", "400");
                updatePropertyResponse.put("response_description", "FAILED");
                updatePropertyResponse.put("errors", "Record already exists");
            }else {
                updatePropertyResponse.put("response_code", "500");
                updatePropertyResponse.put("response_description", "FAILED");
                updatePropertyResponse.put("errors", "Internal error occurred");
            }
        }

        return ResponseEntity
                .status(Integer.parseInt(updatePropertyResponse.get("response_code").toString()))
                .body(updatePropertyResponse);
    }

    @RequestMapping(value ="/properties", method = RequestMethod.POST)
    public ResponseEntity<Object> createProperty(@RequestBody Map<String, Object> body) {
        Map<String, Object> createPropertyResponse = new HashMap<>();

        try {
            body.put("property_id", "PRP_" + Utility.generateRandomAlphanumeric(10));

            List<String> requiredFields = new ArrayList<>(){{
                add("county");
                add("latitude");
                add("location");
                add("longitude");
                add("property_response_description");
                add("property_name");
                add("property_price");
                add("property_id");
            }};

            Map<String, Object> dataValidationResult = dataValidation.areFieldsValid(body, requiredFields);

            if (Boolean.parseBoolean(dataValidationResult.get("valid").toString())) {
                createPropertyResponse = propertyService.createProperty(body);
            } else {
                Map<String, Object> validationErrorMap = mapper.convertValue(dataValidationResult.get("errors"), new TypeReference<>() {});

                createPropertyResponse.put("response_code", "400");
                createPropertyResponse.put("response_description", "FAILED");
                createPropertyResponse.put("errors", validationErrorMap.get("message"));
            }
        } catch (Exception e) {
            log.error(e.getMessage());

            if (e.getCause() != null || e.getCause().getMessage().contains("duplicate key") || e.getCause().getMessage().contains("unique constraint")){
                createPropertyResponse.put("response_code", "400");
                createPropertyResponse.put("response_description", "FAILED");
                createPropertyResponse.put("errors", "Property already exists");
            }else {
                createPropertyResponse.put("response_code", "500");
                createPropertyResponse.put("response_description", "FAILED");
                createPropertyResponse.put("errors", "Internal error occurred");
            }
        }

        return ResponseEntity
                .status(Integer.parseInt(createPropertyResponse.get("response_code").toString()))
                .body(createPropertyResponse);
    }

    @RequestMapping(value ="/properties/{property_id}", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteProperty(@PathVariable("property_id") String propertyId) {
        Map<String, Object> deletePropertyResponse = new HashMap<>();

        try {
            if (!propertyId.trim().equalsIgnoreCase("")) {
                deletePropertyResponse = propertyService.deleteProperty(propertyId.trim());
            } else {
                deletePropertyResponse.put("response_code", "404");
                deletePropertyResponse.put("response_description", "Success");
                deletePropertyResponse.put("response_data", null);
            }
        } catch (Exception e) {
            log.error(e.getMessage());

            deletePropertyResponse.put("response_code", "500");
            deletePropertyResponse.put("response_description", "Internal error occurred");
            deletePropertyResponse.put("response_data", null);
        }

        return ResponseEntity
                .status(Integer.parseInt(deletePropertyResponse.get("response_code").toString()))
                .body(deletePropertyResponse);
    }
}