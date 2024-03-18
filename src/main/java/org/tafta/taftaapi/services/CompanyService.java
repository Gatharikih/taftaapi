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
 * Created on May 19, 2023.
 * Time 1446h
 */

@Slf4j
@Service
public class CompanyService {
    @Autowired
    DBFunctionImpl dbFunction;

    public Map<String, Object> createCompany(Map<String, Object> companyParams){
        Map<String, Object> response = new HashMap<>();

        try {
            Map<String, Object> createCompanyResponse = dbFunction.createCompany(companyParams);

            if(createCompanyResponse != null && !createCompanyResponse.isEmpty()){
                response.put("response_code", "201");
                response.put("response_description", "Success");
                response.put("response_data", createCompanyResponse);
            }else{
                response.put("response_code", "200");
                response.put("response_description", "Company not updated");
                response.put("response_data", null);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return response;
    }

    public Map<String, Object> updateCompany(Map<String, Object> companyParams, String companyId){
        Map<String, Object> response = new HashMap<>();

        try {
            Map<String, Object> companyResponse = dbFunction.searchCompanyById(companyId);

            if (companyResponse != null) {
                companyParams.put("company_id", companyId);

                Map<String, Object> updateCompanyResponse = dbFunction.updateCompany(companyParams);

                if(updateCompanyResponse != null){
                    response.put("response_code", "200");
                    response.put("response_description", "Success");
                    response.put("response_data", updateCompanyResponse);
                }else{
                    response.put("response_code", "200");
                    response.put("response_description", "Company not updated");
                    response.put("response_data", null);
                }
            } else {
                response.put("response_code", "404");
                response.put("response_description", "Company not found");
                response.put("response_data", null);
            }
        } catch (Exception e) {
            log.error(e.getMessage());

            response.put("response_code", "500");
            response.put("response_description", "Internal error");
            response.put("response_data", null);
        }

        return response;
    }

    public Map<String, Object> listAllCompanies(Map<String, Object> queryParams){
        Map<String, Object> response = new HashMap<>();

        try {
            List<Map<String, Object>> listAllCompaniesResponse = dbFunction.listAllCompanies(queryParams);

            if(listAllCompaniesResponse != null){
                response.put("response_code", "200");
                response.put("response_description", "Success");
                response.put("response_data", listAllCompaniesResponse);
                response.put("page_size", listAllCompaniesResponse.size());
            }else{
                response.put("response_code", "404");
                response.put("response_description", "No company found");
                response.put("response_data", null);
            }
        } catch (Exception e) {
            log.error(e.getMessage());

            response.put("response_code", "500");
            response.put("response_description", "Internal error");
            response.put("response_data", null);
        }

        return response;
    }

    public Map<String, Object> searchCompanyById(String id){
        Map<String, Object> response = new HashMap<>();

        try {
            Map<String, Object> searchCompanyResponse = dbFunction.searchCompanyById(id);

            if(searchCompanyResponse != null){
                searchCompanyResponse.remove("password");
                searchCompanyResponse.remove("api_password");
                searchCompanyResponse.remove("api_key");

                response.put("response_code", "200");
                response.put("response_description", "Success");
                response.put("response_data", searchCompanyResponse);
            }else{
                response.put("response_code", "404");
                response.put("response_description", "No company found");
                response.put("response_data", null);
            }
        } catch (Exception e) {
            log.error(e.getMessage());

            response.put("response_code", "500");
            response.put("response_description", "Internal error");
            response.put("response_data", null);
        }

        return response;
    }

    public Map<String, Object> searchCompany(String id){
        Map<String, Object> response = new HashMap<>();

        try {
            Map<String, Object> searchCompanyResponse = dbFunction.searchCompany(id);

            if(searchCompanyResponse != null){
                searchCompanyResponse.remove("password");
                searchCompanyResponse.remove("api_password");
                searchCompanyResponse.remove("api_key");

                response.put("response_code", "200");
                response.put("response_description", "Success");
                response.put("response_data", searchCompanyResponse);
            }else{
                response.put("response_code", "404");
                response.put("response_description", "No company found");
                response.put("response_data", null);
            }
        } catch (Exception e) {
            log.error(e.getMessage());

            response.put("response_code", "500");
            response.put("response_description", "Internal error");
            response.put("response_data", null);
        }

        return response;
    }

    public Map<String, Object> deleteCompany(String id){
        Map<String, Object> response = new HashMap<>();

        try {
            Map<String, Object> searchCompanyResponse = dbFunction.searchCompanyById(id);

            if(searchCompanyResponse != null){
                Map<String, Object> deleteCompanyResponse = dbFunction.deleteCompany(id);

                if(deleteCompanyResponse != null){
                    response.put("response_code", "200");
                    response.put("response_description", "Success");
                    response.put("response_data", deleteCompanyResponse.get("id"));
                }else{
                    response.put("response_code", "200");
                    response.put("response_description", "Company not deleted");
                    response.put("response_data", null);
                }
            }else{
                response.put("response_code", "404");
                response.put("response_description", "Company not found");
                response.put("response_data", null);
            }
        } catch (Exception e) {
            log.error(e.getMessage());

            response.put("response_code", "500");
            response.put("response_description", "Internal error");
            response.put("response_data", null);
        }

        return response;
    }
}