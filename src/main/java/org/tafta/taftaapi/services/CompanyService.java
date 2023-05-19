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
    private DBFunctionImpl dbFunction;

    public Map<String, Object> createCompany(Map<String, Object> companyParams){
        List<Map<String, Object>> createCompanyResponse = dbFunction.createCompany(companyParams);

        if(createCompanyResponse != null && createCompanyResponse.size() > 0){
            return new HashMap<>() {{
                put("response_code", "201");
                put("description", "Success");
                put("data", createCompanyResponse);
            }};
        }else{
            return new HashMap<>() {{
                put("response_code", "200");
                put("description", "Record not updated");
                put("data", null);
            }};
        }
    }
    public Map<String, Object> updateCompany(Map<String, Object> companyParams, String companyId){
        Map<String, Object> companyResponse = dbFunction.searchCompanyById(companyId);

        if (companyResponse != null) {
            companyResponse.put("id", companyId);

            List<Map<String, Object>> updateCompanyResponse = dbFunction.updateCompany(companyParams);

            if(updateCompanyResponse != null){
                if(updateCompanyResponse.size() > 0){
                    return new HashMap<>() {{
                        put("response_code", "201");
                        put("description", "Success");
                        put("data", updateCompanyResponse);
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
                put("description", "Company not found");
                put("data", null);
            }};
        }
    }
    public Map<String, Object> searchCompanies(Map<String, Object> searchMap){
        List<Map<String, Object>> searchCompaniesResponse = dbFunction.searchCompanies(searchMap);

        if(searchCompaniesResponse != null){
            return new HashMap<>() {{
                put("response_code", "200");
                put("description", "Success");
                put("data", searchCompaniesResponse);
            }};
        }else{
            return new HashMap<>() {{
                put("response_code", "404");
                put("description", "No company found");
                put("data", null);
            }};
        }
    }
    public Map<String, Object> listAllCompanies(Map<String, Object> queryParams){
        List<Map<String, Object>> listAllCompaniesResponse = dbFunction.listAllCompanies(queryParams);

        if(listAllCompaniesResponse != null){
            return new HashMap<>() {{
                put("response_code", "200");
                put("description", "Success");
                put("data", listAllCompaniesResponse);
                put("page_size", listAllCompaniesResponse.size());
            }};
        }else{
            return new HashMap<>() {{
                put("response_code", "404");
                put("description", "No company found");
                put("data", null);
            }};
        }
    }
    public Map<String, Object> searchCompanyById(String id){
        Map<String, Object> searchCompanyResponse = dbFunction.searchCompanyById(id);

        if(searchCompanyResponse != null){
            return new HashMap<>() {{
                put("response_code", "200");
                put("description", "Success");
                put("data", searchCompanyResponse);
            }};
        }else{
            return new HashMap<>() {{
                put("response_code", "404");
                put("description", "No company found");
                put("data", null);
            }};
        }
    }
    public Map<String, Object> deleteCompany(String id){
        Map<String, Object> searchCompanyResponse = dbFunction.searchCompanyById(id);

        if(searchCompanyResponse != null){
            Map<String, Object> deleteCompanyResponse = dbFunction.deleteCompany(id);

            if(deleteCompanyResponse != null){
                return new HashMap<>() {{
                    put("response_code", "200");
                    put("description", "Success");
                    put("data", null);
                }};
            }else{
                return new HashMap<>() {{
                    put("response_code", "200");
                    put("description", "Company not deleted");
                    put("data", null);
                }};
            }
        }else{
            return new HashMap<>() {{
                put("response_code", "200");
                put("description", "Company not found");
                put("data", null);
            }};
        }
    }
}