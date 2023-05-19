package org.tafta.taftaapi.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tafta.taftaapi.services.CompanyService;
import org.tafta.taftaapi.services.DataValidation;
import org.tafta.taftaapi.services.PropertyService;

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
    private DataValidation dataValidation;

    @RequestMapping(value ="/api/v1/companies/company/{company_id}", method = RequestMethod.GET)
    public ResponseEntity<Object> getCompany(@PathVariable("company_id") String companyId) {
        try {
            if (!companyId.trim().isEmpty()) {
                Map<String, Object> searchCompanyResponse = companyService.searchCompanyById(companyId.trim());

                return ResponseEntity.status(Integer.parseInt(searchCompanyResponse.get("response_code").toString())).body(searchCompanyResponse);
            } else {
                return ResponseEntity.status(404).body(new HashMap<>() {{
                    put("response_code", "404");
                    put("description", "Success");
                    put("data", null);
                }});
            }
        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity.status(500).body(new HashMap<>() {{
                put("response_code", "500");
                put("description", "Internal error occurred");
                put("data", null);
            }});
        }
    }

    @RequestMapping(value ="/api/v1/properties", method = RequestMethod.GET)
    public ResponseEntity<Object> searchCompanies(@RequestParam("company_email") Optional<String> companyEmail, @RequestParam("company_name") Optional<String> companyName,
                                                  @RequestParam("contact_person") Optional<String> contactPerson) {
        try {
            Map<String, Object> searchMap = new HashMap<>();

            companyEmail.ifPresent(s -> searchMap.put("company_email", s));
            companyName.ifPresent(s -> searchMap.put("company_name", s));
            contactPerson.ifPresent(s -> searchMap.put("contact_person", s));

            Map<String, Object> searchCompaniesResponse = companyService.searchCompanies(searchMap);

            return ResponseEntity.status(Integer.parseInt(searchCompaniesResponse.get("response_code").toString())).body(searchCompaniesResponse);
        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity.status(500).body(new HashMap<>() {{
                put("response_code", "500");
                put("description", "Internal error occurred");
                put("data", null);
            }});
        }
    }

    @RequestMapping(value ="/api/v1/companies/list", method = RequestMethod.GET)
    public ResponseEntity<Object> listAllCompanies(@RequestParam("page_number") Optional<String> pageNumber, @RequestParam("status") Optional<String> status) {
        try {
            Map<String, Object> searchMap = new HashMap<>();

            pageNumber.ifPresent(s -> searchMap.put("page_number", s));
            status.ifPresent(s -> searchMap.put("status", s));

            Map<String, Object> listAllCompaniesResponse = companyService.listAllCompanies(searchMap);

            return ResponseEntity.status(Integer.parseInt(listAllCompaniesResponse.get("response_code").toString())).body(listAllCompaniesResponse);
        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity.status(500).body(new HashMap<>() {{
                put("response_code", "500");
                put("description", "Internal error occurred");
                put("data", null);
            }});
        }
    }

    @RequestMapping(value ="/api/v1/companies/{company_id}", method = RequestMethod.PUT)
    public ResponseEntity<Object> updateCompany(@PathVariable("company_id") String companyId, @RequestBody Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();

        try {
            Map<String, Object> updateCompanyResponse = companyService.updateCompany(body, companyId);

            return ResponseEntity.status(Integer.parseInt(updateCompanyResponse.get("response_code").toString())).body(updateCompanyResponse);
        } catch (Exception e) {
            e.printStackTrace();

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

    @RequestMapping(value ="/api/v1/companies", method = RequestMethod.POST)
    public ResponseEntity<Object> createCompany(@RequestBody Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<String> requiredFields = new ArrayList<>();

            requiredFields.add("company_address");
            requiredFields.add("company_description");
            requiredFields.add("company_email");
            requiredFields.add("company_name");

            Map<String, Object> dataValidationResult = dataValidation.areFieldsValid(body, requiredFields);

            if (Boolean.parseBoolean(dataValidationResult.get("valid").toString())) {
                Map<String, Object> createCompanyResponse = companyService.createCompany(body);

                return ResponseEntity.status(Integer.parseInt(createCompanyResponse.get("response_code").toString())).body(createCompanyResponse);
            } else {
                Map validationErrorMap = (Map) dataValidationResult.get("errors");

                response.put("response_code", "400");
                response.put("description", "Failed");
                response.put("errors", List.of(validationErrorMap.get("message")));

                return ResponseEntity.status(400).body(response);
            }
        } catch (Exception e) {
            e.printStackTrace();

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

    @RequestMapping(value ="/api/v1/companies/{company_id}", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteCompany(@PathVariable("company_id") String companyId) {
        try {
            if (!companyId.trim().equalsIgnoreCase("")) {
                Map<String, Object> deleteCompanyResponse = companyService.deleteCompany(companyId.trim());

                return ResponseEntity.status(Integer.parseInt(deleteCompanyResponse.get("response_code").toString())).body(deleteCompanyResponse);
            } else {
                return ResponseEntity.status(404).body(new HashMap<>() {{
                    put("response_code", "404");
                    put("description", "Success");
                    put("data", null);
                }});
            }
        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity.status(500).body(new HashMap<>() {{
                put("response_code", "500");
                put("description", "Internal error occurred");
                put("data", null);
            }});
        }
    }
}