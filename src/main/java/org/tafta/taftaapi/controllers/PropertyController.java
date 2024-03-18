package org.tafta.taftaapi.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tafta.taftaapi.models.Property;
import org.tafta.taftaapi.repo.DBFunctionImpl;
import org.tafta.taftaapi.services.DataValidation;
import org.tafta.taftaapi.services.PropertyService;
import org.tafta.taftaapi.utility.Utility;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Autowired
    DBFunctionImpl dbFunction;

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
                .status(Integer.parseInt(String.valueOf(searchPropertyResponse.get("response_code"))))
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
                .status(Integer.parseInt(String.valueOf(searchPropertiesResponse.get("response_code"))))
                .body(searchPropertiesResponse);
    }

    @RequestMapping(value ="/properties/list", method = RequestMethod.GET)
    public ResponseEntity<Object> listAllProperties(@RequestParam(value = "page_number", required = false) String pageNumber,
                                                    @RequestParam(value = "status", required = false) String status) {
        Map<String, Object> listAllPropertiesResponse = new HashMap<>();

        try {
            Map<String, Object> searchMap = new HashMap<>(){{
                put("page_number", pageNumber);
                put("status", status);
            }};

            searchMap = Utility.cleanMap(searchMap);

            listAllPropertiesResponse = propertyService.listAllProperties(searchMap);
        } catch (Exception e) {
            log.error(e.getMessage());

            listAllPropertiesResponse.put("response_code", "500");
            listAllPropertiesResponse.put("response_description", "Internal error occurred");
            listAllPropertiesResponse.put("response_data", null);
        }

        return ResponseEntity
                .status(Integer.parseInt(String.valueOf(listAllPropertiesResponse.get("response_code"))))
                .body(listAllPropertiesResponse);
    }

    @RequestMapping(value ="/properties/{property_id}", method = RequestMethod.PUT, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
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
                .status(Integer.parseInt(String.valueOf(updatePropertyResponse.get("response_code"))))
                .body(updatePropertyResponse);
    }

    // @RequestPart
    @RequestMapping(value ="/properties", method = RequestMethod.POST, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }, // , MediaType.MULTIPART_MIXED_VALUE
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createProperty(@ModelAttribute Property property) {
        Map<String, Object> response = new HashMap<>();
        String propertyId = property.getProperty_id();

        log.info("property id: " + propertyId);

        try {
            if (propertyId != null) {
                Map<String, Object> savedProperty = dbFunction.searchPropertyById(propertyId);

                if (savedProperty != null) {
                    List<Map<String, Object>> savedPropertyPhotos = dbFunction.getPropertyPhotos(propertyId);

                    savedProperty.put("photos", savedPropertyPhotos);

                    response.put("response_code", "200");
                    response.put("response_description", "Success");
                    response.put("response_data", savedProperty);
                } else {
                    Map<String, Object> dataValidationResult = dataValidation.validatePropertyObject(property);

                    response.putAll(dataValidationResult);

                    if (Boolean.parseBoolean(String.valueOf(dataValidationResult.get("valid")))) {
                        response = propertyService.createProperty(property);

                        // response.put("response_code", "200");
                        // response.put("response_description", "OK");
                    } else {
                        response.put("response_code", "400");
                        response.put("response_description", "VALIDATION FAILED");
                    }
                }
            } else {
                response.put("response_code", "400");
                response.put("response_description", "VALIDATION FAILED - Property ID is null");
            }
        } catch (Exception e) {
            log.error(e.getMessage());

            if (e.getCause() != null || e.getCause().getMessage().contains("duplicate key") || e.getCause().getMessage().contains("unique constraint")){
                response.put("response_code", "400");
                response.put("response_description", "FAILED");
                response.put("errors", "Property already exists");
            }else {
                response.put("response_code", "500");
                response.put("response_description", "FAILED");
                response.put("errors", "Internal error occurred");
            }
        }

        return ResponseEntity
                .status(Integer.parseInt(String.valueOf(response.get("response_code"))))
                .body(response);
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
                .status(Integer.parseInt(String.valueOf(deletePropertyResponse.get("response_code"))))
                .body(deletePropertyResponse);
    }
}