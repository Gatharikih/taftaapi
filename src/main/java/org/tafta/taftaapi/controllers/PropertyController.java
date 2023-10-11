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

    @RequestMapping(value ="/api/v1/properties/property/{property_id}", method = RequestMethod.GET)
    public ResponseEntity<Object> getProperty(@PathVariable("property_id") String propertyId) {
        try {
            if (!propertyId.trim().isEmpty()) {
                Map<String, Object> searchPropertyResponse = propertyService.searchPropertyById(propertyId.trim());

                return ResponseEntity.status(Integer.parseInt(searchPropertyResponse.get("response_code").toString())).body(searchPropertyResponse);
            } else {
                return ResponseEntity.status(404).body(new HashMap<>() {{
                    put("response_code", "404");
                    put("description", "Success");
                    put("data", null);
                }});
            }
        } catch (Exception e) {
            log.error(e.getMessage());

            return ResponseEntity.status(500).body(new HashMap<>() {{
                put("response_code", "500");
                put("description", "Internal error occurred");
                put("data", null);
            }});
        }
    }

    @RequestMapping(value ="/api/v1/properties", method = RequestMethod.GET)
    public ResponseEntity<Object> searchProperties(@RequestParam(value = "county", required = false) String county,
                                                   @RequestParam(value = "property_name", required = false) String propertyName,
                                                   @RequestParam(value = "min_price", required = false) String minPrice,
                                                   @RequestParam(value = "max_price", required = false) String maxPrice,
                                                   @RequestParam(value = "price", required = false) String price,
                                                   @RequestParam(value = "description", required = false) String description,
                                                   @RequestParam(value = "location", required = false) String location) {
        try {
            Map<String, Object> searchMap = new HashMap<>();

            searchMap.put("county", county);
            searchMap.put("property_name", propertyName);
            searchMap.put("min_price", minPrice);
            searchMap.put("max_price", maxPrice);
            searchMap.put("price", price);
            searchMap.put("description", description);
            searchMap.put("location", location);

            searchMap = Utility.cleanMap(searchMap);

            Map<String, Object> searchPropertiesResponse = propertyService.searchProperties(searchMap);

            return ResponseEntity
                    .status(Integer.parseInt(searchPropertiesResponse.get("response_code").toString()))
                    .body(searchPropertiesResponse);
        } catch (Exception e) {
            log.error(e.getMessage());

            return ResponseEntity.status(500).body(new HashMap<>() {{
                put("response_code", "500");
                put("description", "Internal error occurred");
                put("data", null);
            }});
        }
    }

    @RequestMapping(value ="/api/v1/properties/list", method = RequestMethod.GET)
    public ResponseEntity<Object> listAllProperties(@RequestParam(value = "page_number", required = false) String pageNumber,
                                                    @RequestParam(value = "status", required = false) String status) {
        try {
            Map<String, Object> searchMap = new HashMap<>();

            searchMap.put("page_number", pageNumber);
            searchMap.put("status", status);

            searchMap = Utility.cleanMap(searchMap);

            Map<String, Object> listAllPropertiesResponse = propertyService.listAllProperties(searchMap);

            return ResponseEntity
                    .status(Integer.parseInt(listAllPropertiesResponse.get("response_code").toString()))
                    .body(listAllPropertiesResponse);
        } catch (Exception e) {
            log.error(e.getMessage());

            return ResponseEntity.status(500).body(new HashMap<>() {{
                put("response_code", "500");
                put("description", "Internal error occurred");
                put("data", null);
            }});
        }
    }

    @RequestMapping(value ="/api/v1/properties/{property_id}", method = RequestMethod.PUT)
    public ResponseEntity<Object> updateProperty(@PathVariable("property_id") String propertyId,
                                                 @RequestBody Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();

        try {
            Map<String, Object> updatePropertyResponse = propertyService.updateProperty(body, propertyId);

            return ResponseEntity.status(Integer.parseInt(updatePropertyResponse.get("response_code").toString()))
                    .body(updatePropertyResponse);
        } catch (Exception e) {
            log.error(e.getMessage());

            if (e.getCause() != null || e.getCause().getMessage().contains("duplicate key") || e.getCause().getMessage().contains("unique constraint")){
                response.put("response_code", "400");
                response.put("description", "Failed");
                response.put("errors", List.of(new HashMap<>() {{
                    put("description", "Record already exists");
                }}));
            }else {
                response.put("response_code", "500");
                response.put("description", "Failed");
                response.put("errors", List.of(new HashMap<>() {{
                    put("description", "Internal error occurred");
                }}));
            }

            return ResponseEntity.status(Integer.parseInt(response.get("response_code").toString()))
                    .body(response);
        }
    }

    @RequestMapping(value ="/api/v1/properties", method = RequestMethod.POST)
    public ResponseEntity<Object> createProperty(@RequestBody Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<String> requiredFields = new ArrayList<>();

            requiredFields.add("county");
            requiredFields.add("created_by");
            requiredFields.add("created_by");
            requiredFields.add("latitude");
            requiredFields.add("location");
            requiredFields.add("longitude");
            requiredFields.add("property_description");
            requiredFields.add("property_name");
            requiredFields.add("property_price");

            Map<String, Object> dataValidationResult = dataValidation.areFieldsValid(body, requiredFields);

            if (Boolean.parseBoolean(dataValidationResult.get("valid").toString())) {
                Map<String, Object> createUserResponse = propertyService.createProperty(body);

                return ResponseEntity.status(Integer.parseInt(createUserResponse.get("response_code").toString()))
                        .body(createUserResponse);
            } else {
                Map<String, Object> validationErrorMap = mapper.convertValue(dataValidationResult.get("errors"), new TypeReference<>() {});

                response.put("response_code", "400");
                response.put("description", "Failed");
                response.put("errors", List.of(validationErrorMap.get("message")));

                return ResponseEntity.status(400).body(response);
            }
        } catch (Exception e) {
            log.error(e.getMessage());

            if (e.getCause() != null || e.getCause().getMessage().contains("duplicate key") || e.getCause().getMessage().contains("unique constraint")){
                response.put("response_code", "400");
                response.put("description", "Failed");
                response.put("errors", List.of(new HashMap<>() {{
                    put("description", "Property already exists");
                }}));
            }else {
                response.put("response_code", "500");
                response.put("description", "Failed");
                response.put("errors", List.of(new HashMap<>() {{
                    put("description", "Internal error occurred");
                }}));
            }

            return ResponseEntity.status(Integer.parseInt(response.get("response_code").toString()))
                    .body(response);
        }
    }

    @RequestMapping(value ="/api/v1/properties/{property_id}", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteProperty(@PathVariable("property_id") String propertyId) {
        try {
            if (!propertyId.trim().equalsIgnoreCase("")) {
                Map<String, Object> deletePropertyResponse = propertyService.deleteProperty(propertyId.trim());

                return ResponseEntity.status(Integer.parseInt(deletePropertyResponse.get("response_code").toString())).body(deletePropertyResponse);
            } else {
                return ResponseEntity.status(404).body(new HashMap<>() {{
                    put("response_code", "404");
                    put("description", "Success");
                    put("data", null);
                }});
            }
        } catch (Exception e) {
            log.error(e.getMessage());

            return ResponseEntity.status(500).body(new HashMap<>() {{
                put("response_code", "500");
                put("description", "Internal error occurred");
                put("data", null);
            }});
        }
    }
}