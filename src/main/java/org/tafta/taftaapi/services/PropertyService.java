package org.tafta.taftaapi.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.multipart.MultipartFile;
import org.tafta.taftaapi.config.PropConfiguration;
import org.tafta.taftaapi.models.Property;
import org.tafta.taftaapi.repo.DBFunctionImpl;
import org.tafta.taftaapi.utility.Utility;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
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
@EnableTransactionManagement
public class PropertyService {
    @Autowired
    DBFunctionImpl dbFunction;
    @Autowired
    PropConfiguration propConfiguration;
    ObjectMapper mapper = new ObjectMapper();

    public Map<String, Object> createProperty(Property property){
        Map<String, Object> response = new HashMap<>();

        try {
            List<MultipartFile> propertyPhotos = property.getPhotos();
            List<Map<String, Object>> photosToSave = new ArrayList<>();
            List<Map<String, Object>> savedFiles = new ArrayList<>();

            log.info("property: " + property);

            if (propertyPhotos != null && !propertyPhotos.isEmpty()){
                propertyPhotos.forEach(multipartFile -> {
                    if (!multipartFile.isEmpty()) {
                        String userDirectory = propConfiguration.getFilesPath();
                        String fileName = multipartFile.getOriginalFilename(); // multipartFile.getResource().getFilename()

                        float fileSize_f = Float.parseFloat(multipartFile.getSize() + ".0") /1000000;
                        String fileSize_str = Utility.roundOffDecimal(fileSize_f, 2) + " MB";
                        Path fileNameAndPath = Paths.get(userDirectory, fileName);

                        assert fileName != null;
                        String ext = fileName.split("\\.")[1];
                        String mime = multipartFile.getContentType();
                        String photoId = Utility.generateRandomAlphanumeric(5) + "_" + property.getProperty_id();

                        Map<String, Object> photosBody = new HashMap<>(){{
                            put("alternative_text", fileName);
                            put("caption", null);
                            put("ext", ext);
                            put("file_format", mime);
                            put("file_name", fileName);
                            put("file_size", fileSize_str);
                            put("hash", null);
                            put("height", null);
                            put("mime", mime);
                            put("parent_id", property.getProperty_id());
                            put("photo_id", photoId);
                            put("width", null);
                        }};

                        log.info("filename: {}, type: {}, size: {} ", fileName, multipartFile.getContentType(), fileSize_str);

                        try {
                            Path userDirPath = Path.of(userDirectory);

                            if (!Files.exists(userDirPath)) {
                                Files.createDirectories(userDirPath);
                            }

                            try (InputStream inputStream = multipartFile.getInputStream()){
                                long fileBytesCopied = Files.copy(inputStream, fileNameAndPath, StandardCopyOption.REPLACE_EXISTING);

                                if (fileBytesCopied == multipartFile.getSize()){
                                    photosToSave.add(photosBody);
                                }
                            } catch (Exception e) {
                                log.info("Could not save file: " + e.getMessage());
                            }
                        } catch (Exception e) {
                            log.info(e.getMessage());
                        }
                    }
                });
            }

            // remove propertyPhotos to convert Property POJO to Map. NOTE: DO NOT DELETE
            property.setPhotos(null);

            Map<String, Object> propertyParams = mapper.convertValue(property, new TypeReference<>() {});
            Map<String, Object> createPropertyResponse = dbFunction.createProperty(propertyParams);

            if(createPropertyResponse != null && !createPropertyResponse.isEmpty()){
                if (!photosToSave.isEmpty()){
                    photosToSave.forEach(photoToSave -> {
                        Map<String, Object> newPhoto = dbFunction.createPhoto(photoToSave);

                        if (newPhoto != null){
//                                savedFiles.add(newPhoto);
                            throw new RuntimeException("Photos exception");
                        }
                    });
                }

                createPropertyResponse.put("photos", savedFiles);

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