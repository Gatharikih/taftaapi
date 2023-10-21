package org.tafta.taftaapi.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tafta.taftaapi.services.CompanyService;
import org.tafta.taftaapi.services.DataValidation;
import org.tafta.taftaapi.services.PropertyService;
import org.tafta.taftaapi.utility.Utility;

import java.util.*;

/**
 * @author Gathariki Ngigi
 * Created on May 19, 2023.
 * Time 1445h
 */

@RestController
@Slf4j
public class CompanyController {
    @Autowired
    CompanyService companyService;
    @Autowired
    DataValidation dataValidation;

    @RequestMapping(value ="/companies/{company_id}", method = RequestMethod.GET)
    public ResponseEntity<Object> getCompany(@PathVariable("company_id") String companyId) {
        Map<String, Object> searchCompanyResponse = new HashMap<>();

        try {
            if (!companyId.isEmpty()) {
                searchCompanyResponse = companyService.searchCompanyById(companyId.trim());
            } else {
                searchCompanyResponse.put("response_code", "400");
                searchCompanyResponse.put("response_description", "Company id cannot be null");
                searchCompanyResponse.put("response_data", null);
            }
        } catch (Exception e) {
            log.error(e.getMessage());

            searchCompanyResponse.put("response_code", "500");
            searchCompanyResponse.put("response_description", "Internal error");
            searchCompanyResponse.put("response_data", null);
        }

        return ResponseEntity
                .status(Integer.parseInt(String.valueOf(searchCompanyResponse.get("response_code"))))
                .body(searchCompanyResponse);
    }

    @RequestMapping(value ="/companies/list", method = RequestMethod.GET)
    public ResponseEntity<Object> listAllCompanies(@RequestParam(value = "page_number", required = false) String pageNumber,
                                                   @RequestParam(value = "status", required = false)  String status) {
        Map<String, Object> listAllCompaniesResponse = new HashMap<>();

        try {
            Map<String, Object> searchMap = new HashMap<>(){{
                put("page_number",pageNumber);
                put("status", status);
            }};

            searchMap = Utility.cleanMap(searchMap);

            listAllCompaniesResponse = companyService.listAllCompanies(searchMap);
        } catch (Exception e) {
            log.error(e.getMessage());

            listAllCompaniesResponse.put("response_code", "500");
            listAllCompaniesResponse.put("response_description", "Internal error");
            listAllCompaniesResponse.put("response_data", null);
        }

        return ResponseEntity
                .status(Integer.parseInt(String.valueOf(listAllCompaniesResponse.get("response_code"))))
                .body(listAllCompaniesResponse);
    }

    @RequestMapping(value ="/companies/{company_id}", method = RequestMethod.PUT)
    public ResponseEntity<Object> updateCompany(@PathVariable("company_id") String companyId,
                                                @RequestBody Map<String, Object> body) {
        Map<String, Object> updateCompanyResponse = new HashMap<>();

        try {
            if (!companyId.isEmpty()) {
                updateCompanyResponse = companyService.updateCompany(body, companyId);
            } else {
                updateCompanyResponse.put("response_code", "400");
                updateCompanyResponse.put("response_description", "Company id cannot be null");
                updateCompanyResponse.put("response_data", null);
            }
        } catch (Exception e) {
            log.error(e.getMessage());

            if (e.getCause() != null || e.getCause().getMessage().contains("duplicate key") || e.getCause().getMessage().contains("unique constraint")){
                updateCompanyResponse.put("response_code", "400");
                updateCompanyResponse.put("response_description", "Failed");
                updateCompanyResponse.put("errors", "Record already exists");
            }else {
                updateCompanyResponse.put("response_code", "500");
                updateCompanyResponse.put("response_description", "Failed");
                updateCompanyResponse.put("errors", "Internal error");
            }
        }

        return ResponseEntity
                .status(Integer.parseInt(String.valueOf(updateCompanyResponse.get("response_code"))))
                .body(updateCompanyResponse);
    }

    @RequestMapping(value ="/companies", method = RequestMethod.POST)
    public ResponseEntity<Object> createCompany(@RequestBody Map<String, Object> body) {
        Map<String, Object> createCompanyResponse = new HashMap<>();

        try {
            List<String> requiredFields = new ArrayList<>();

            requiredFields.add("company_address");
            requiredFields.add("company_description");
            requiredFields.add("company_email");
            requiredFields.add("company_name");

            Map<String, Object> dataValidationResult = dataValidation.areFieldsValid(body, requiredFields);

            if (Boolean.parseBoolean(dataValidationResult.get("valid").toString())) {
                createCompanyResponse = companyService.createCompany(body);
            } else {
                createCompanyResponse.put("response_code", "400");
                createCompanyResponse.put("response_description", "Failed");
                createCompanyResponse.put("errors", dataValidationResult.get("errors"));
            }
        } catch (Exception e) {
            log.error(e.getMessage());

            if (e.getCause() != null || e.getCause().getMessage().contains("duplicate key") || e.getCause().getMessage().contains("unique constraint")){
                createCompanyResponse.put("response_code", "400");
                createCompanyResponse.put("response_description", "Failed");
                createCompanyResponse.put("errors", "Property already exists");
            }else {
                createCompanyResponse.put("response_code", "500");
                createCompanyResponse.put("response_description", "Failed");
                createCompanyResponse.put("errors", "Internal error");
            }
        }

        return ResponseEntity
                .status(Integer.parseInt(String.valueOf(createCompanyResponse.get("response_code"))))
                .body(createCompanyResponse);
    }

    @RequestMapping(value ="/companies/{company_id}", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteCompany(@PathVariable("company_id") String companyId) {
        Map<String, Object> deleteCompanyResponse = new HashMap<>();

        try {
            if (!companyId.isEmpty()) {
                deleteCompanyResponse = companyService.deleteCompany(companyId.trim());
            } else {
                deleteCompanyResponse.put("response_code", "400");
                deleteCompanyResponse.put("response_description", "Company id cannot be null");
                deleteCompanyResponse.put("response_data", null);
            }
        } catch (Exception e) {
            log.error(e.getMessage());

            deleteCompanyResponse.put("response_code", "500");
            deleteCompanyResponse.put("response_description", "Internal error");
            deleteCompanyResponse.put("response_data", null);
        }


        return ResponseEntity
                .status(Integer.parseInt(String.valueOf(deleteCompanyResponse.get("response_code"))))
                .body(deleteCompanyResponse);
    }
}