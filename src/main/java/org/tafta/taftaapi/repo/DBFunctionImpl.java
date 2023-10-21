package org.tafta.taftaapi.repo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jodd.db.DbOom;
import jodd.db.DbQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tafta.taftaapi.enums.Status;
import org.tafta.taftaapi.enums.Status;
import org.tafta.taftaapi.utility.Utility;

import javax.crypto.Mac;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Gathariki Ngigi
 * Created on April 27, 2023.
 * Time 0959h
 */

@Slf4j
@Component
public class DBFunctionImpl {
    @Autowired
    DbConnectionsHandler connectionsHandler;
    static Mac mac = null;
    private static DbOom dbOom = null;
    int trackQuery;
    int numOfOperations;
    boolean addNewPermission = false, stripePermission = false;
    final Map<String, Object> exceptions = new HashMap<>();
    ObjectMapper mapper = new ObjectMapper();

    public DBFunctionImpl() {
        mac = HmacUtils.getInitializedMac(HmacAlgorithms.HMAC_SHA_512, "3pA4KkUTeGydlxLwht1kIOweCEGOztJAjNKvXa5QPlXlCLnd5hj8KC19fUg3".getBytes(StandardCharsets.UTF_8));
    }

    // <editor-fold default-state="collapsed" desc="NamedBaseExecute">
    public <T> T NamedBaseExecute(String sql, LinkedHashMap<String, Object> collection,
                                  LinkedHashMap<String, Object> whereCollection,
                                  ResultHandler<T> handler) {
        return NamedBaseExecute(sql, collection, whereCollection, handler, "");
    }

    public <T> T NamedBaseExecute(String sql, LinkedHashMap<String, Object> collection,
                                  LinkedHashMap<String, Object> whereCollection,
                                  ResultHandler<T> handler,
                                  String profile) {
        T output = null;
        Connection connection = null;
        ResultSet rs = null;

        try {
            connection = connectionsHandler.getConnection();
            if (dbOom == null) {
                dbOom = new DbOom.Builder().get();
            }

            // use connection
            DbQuery<?> query = new DbQuery<>(dbOom, connection, sql);

            int paramIndex = 1;

            if (collection != null) {
                for (Map.Entry<String, Object> param : collection.entrySet()) {
                    query.setObject(paramIndex++, param.getValue());
                }
            }

            if (whereCollection != null) {
                for (Map.Entry<String, Object> param : whereCollection.entrySet()) {
                    query.setObject(paramIndex++, param.getValue());
                }
            }

            if (handler == null) {
                query.executeUpdate();
            } else {
                rs = query.execute();
            }

            try {
                if (handler != null) {
                    output = handler.handle(rs);
                }
            } catch (Exception e) {
                log.error(e.getMessage());

                exceptions.put("errors", e.getCause().getMessage());
            }

            query.close();

            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            log.error(e.getMessage());

            exceptions.put("errors", e.getCause().getMessage());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    log.error(e.getMessage());

                    exceptions.put("errors", e.getCause().getMessage());
                }
            }
        }

        return output;
    }

    public interface ResultHandler<T> {
        T handle(ResultSet rs);
    }
    // </editor-fold>

    /**-------------------- USERS -------------------------*/

    // <editor-fold default-state="collapsed" desc="createUser(Map<String, Object> entryParams)">
    public List<Map<String, Object>> createUser(Map<String, Object> entryParams) {
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();

        if(entryParams.get("company_id") != null){
            params.put("company_id", entryParams.get("company_id"));
        }
        params.put("id", Optional.ofNullable(entryParams.get("id"))
                .orElse(!getUserRoleId("user").isEmpty() ?
                        Integer.parseInt(String.valueOf(getUserRoleId("user").get(0).get("id"))) : 5));

        if(entryParams.get("fullname") != null){
            params.put("fullname", entryParams.get("fullname"));
        }

        if(entryParams.get("email") != null){
            params.put("email", entryParams.get("email"));
        }

        if(entryParams.get("password") != null){
            params.put("password", entryParams.get("password"));
        }

        if(entryParams.get("msisdn") != null){
            params.put("msisdn", entryParams.get("msisdn"));
        }

        if(entryParams.get("reset_password") != null){
            params.put("reset_password", Optional.ofNullable(entryParams.get("reset_password")).orElse(true));
        }

        if(entryParams.get("status") != null){
            try {
                params.put("status", Optional.of(Status.getStatusType(String.valueOf(entryParams.get("status"))))
                        .orElse(Status.getStatusType("active")));
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }

        params = Utility.cleanMap(params);

        String sql;
        String table = "users";

        sql = Models.InsertString(table, params);
        sql += " returning *";

        List<Map<String, Object>> results = NamedBaseExecute(sql, params, null, new MapResultHandler());

        if(!results.isEmpty()){
            return results;
        }

        return null;
    }
    // </editor-fold>
    //
    // <editor-fold default-state="collapsed" desc="updateUser(Map<String, Object> entryParams)">
    public List<Map<String, Object>> updateUser(Map<String, Object> entryParams) {
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();

        if(entryParams.get("company_id") != null){
            params.put("company_id", entryParams.get("company_id"));
        }
        params.put("id", Optional.ofNullable(entryParams.get("id"))
                .orElse(!getUserRoleId("user").isEmpty() ?
                        Integer.parseInt(String.valueOf(getUserRoleId("user").get(0).get("id"))) : 5));

        if(entryParams.get("fullname") != null){
            params.put("fullname", entryParams.get("fullname"));
        }

        if(entryParams.get("email") != null){
            params.put("email", entryParams.get("email"));
        }

        if(entryParams.get("password") != null){
            params.put("password", entryParams.get("password"));
        }

        if(entryParams.get("msisdn") != null){
            params.put("msisdn", entryParams.get("msisdn"));
        }

        if(entryParams.get("reset_password") != null){
            params.put("reset_password", Optional.ofNullable(entryParams.get("reset_password")).orElse(true));
        }

        if(entryParams.get("status") != null){
            try {
                params.put("status", Status.getStatusType(String.valueOf(entryParams.get("status"))));
            } catch (Exception e) {
                log.error(e.getMessage());

                return new ArrayList<>();
            }
        }

        params.put("updated_at", Timestamp.valueOf(LocalDateTime.now()));
        params.put("deleted_at", null);

        params = Utility.cleanMap(params);

        LinkedHashMap<String, Object> where_params = new LinkedHashMap<>();

        String sql;
        String table = "users";

        where_params.put("id", Integer.parseInt(String.valueOf(entryParams.get("id"))));

        sql = Models.UpdateString(table, params, where_params);

        sql += " returning *";

        List<Map<String, Object>> results = NamedBaseExecute(sql, params, where_params, new MapResultHandler());

        if(!results.isEmpty()){
            return results;
        }

        return null;
    }
    // </editor-fold>
    //
    // <editor-fold default-state="collapsed" desc="getUserRoleId(String roleName)">
    public List<Map<String, Object>> getUserRoleId(String roleName) {
        LinkedHashMap<String, Object> param = new LinkedHashMap<>();
        String sql = "SELECT id FROM roles WHERE name=:name OR description =:name LIMIT 1";

        param.put("name", roleName);
        param.put("currency", roleName.toLowerCase());

        return NamedBaseExecute(sql, param, null, new MapResultHandler());
    }
    // </editor-fold>
    //
    // <editor-fold default-state="collapsed" desc="searchUser(String searchTerm)">
    public Map<String, Object> searchUser(String searchTerm) {
        LinkedHashMap<String, Object> param = new LinkedHashMap<>();
        String sql = "SELECT * FROM users WHERE email=:email OR msisdn=:msisdn OR fullname=:fullname LIMIT 1 " +
                "ORDER BY id, created_at ASC";

        param.put("email", searchTerm);
        param.put("msisdn", searchTerm);
        param.put("fullname", searchTerm);

        List<Map<String, Object>> user = NamedBaseExecute(sql, param, null, new MapResultHandler());

        return !user.isEmpty() ? user.get(0) : null;
    }
    // </editor-fold>
    //
    // <editor-fold default-state="collapsed" desc="findUserUsingEmailAndApiPassword(String email, String apiPassword)">
    public Map<String, Object> findActiveUserUsingEmail(String email) {
        LinkedHashMap<String, Object> param = new LinkedHashMap<>();

        String sql = "SELECT * FROM users WHERE email=:email AND UPPER(status)=:status ";

        param.put("email", email);
        param.put("status", "ACTIVE");

        List<Map<String, Object>> results = NamedBaseExecute(sql, param, null, new MapResultHandler());

        if (results != null && !results.isEmpty()) {
            return results.get(0);
        }

        return null;
    }
    // </editor-fold>
    //
    // <editor-fold default-state="collapsed" desc="List<Map<String, Object>>listAllUsers(String pageNumber) ">
    public List<Map<String, Object>> listAllUsers(Map<String, Object> queryParams) {
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();
        LinkedHashMap<String, Object> where_param = new LinkedHashMap<>();
        String sql = "SELECT * FROM users WHERE status=:status ORDER BY id, created_at ASC LIMIT :limit OFFSET :offset";

        params = Utility.cleanMap(params);

        int limit = 50;

        where_param.put("status", queryParams.getOrDefault("status", "ACTIVE"));
        where_param.put("limit", limit);
        where_param.put("offset", Integer.parseInt(String.valueOf(queryParams.getOrDefault("page_number", "0"))) * limit);

        log.error("where_param: " + where_param);

        List<Map<String, Object>> user = NamedBaseExecute(sql, params, where_param, new MapResultHandler());

        return !user.isEmpty() ? user : null;
    }
    // </editor-fold>
    //
    // <editor-fold default-state="collapsed" desc="searchUserById(String id)">
    public Map<String, Object> searchUserById(String id) {
        LinkedHashMap<String, Object> param = new LinkedHashMap<>();
        String sql = "SELECT * FROM users WHERE id=:id LIMIT 1";

        param.put("id", Integer.parseInt(id));

        List<Map<String, Object>> users = NamedBaseExecute(sql, param, null, new MapResultHandler());

        return !users.isEmpty() ? users.get(0) : null;
    }
    // </editor-fold>
    //
    // <editor-fold default-state="collapsed" desc="deleteUser(String id)">
    public Map<String, Object> deleteUser(String id) {
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();

        params.put("status", Status.DELETED);
        params.put("deleted_at", Timestamp.valueOf(LocalDateTime.now()));

        LinkedHashMap<String, Object> where_params = new LinkedHashMap<>();
        where_params.put("id", Integer.parseInt(id));

        String sql;
        String table = "users";

        where_params.put("id", Integer.parseInt(id));

        sql = Models.UpdateString(table, params, where_params);

        sql += " returning *";

        List<Map<String, Object>> results = NamedBaseExecute(sql, params, where_params, new MapResultHandler());

        if(!results.isEmpty()){
            return results.get(0);
        }

        return null;
    }
    // </editor-fold>

    /**-------------------- PROPERTIES -------------------------*/

    // <editor-fold default-state="collapsed" desc="searchPropertyById(String propertyId)">
    public Map<String, Object> searchPropertyById(String propertyId) {
        try {
            LinkedHashMap<String, Object> param = new LinkedHashMap<>();
            String sql = "SELECT * FROM properties WHERE property_id=:property_id LIMIT 1";

            param.put("property_id", propertyId.trim());

            List<Map<String, Object>> properties = NamedBaseExecute(sql, param, null, new MapResultHandler());

            return !properties.isEmpty() ? properties.get(0) : null;
        } catch (Exception e) {
            log.error(e.getMessage());

            return null;
        }
    }
    // </editor-fold>
    //
    // <editor-fold default-state="collapsed" desc="searchProperties(Map<String, Object> searchMap)">
    public List<Map<String, Object>> searchProperties(Map<String, Object> searchMap) {
        try {
            LinkedHashMap<String, Object> param = new LinkedHashMap<>();
            Map<String, Object> limitAndOffset = Utility.getLimitAndOffset(50, searchMap);

            int limit = Integer.parseInt(String.valueOf(limitAndOffset.get("limit")));
            int offset = Integer.parseInt(String.valueOf(limitAndOffset.get("offset")));

            LinkedHashMap<String, Object> where_param = new LinkedHashMap<>(){{
                put("status", searchMap.getOrDefault("status", "ACTIVE"));
                put("limit", limit);
                put("offset", offset);
            }};

            String searchTermsStr = "";
            trackQuery = 0;

            if(searchMap.get("county") != null){
                if(trackQuery >= 1) {
                    searchTermsStr += " AND LOWER(county) LIKE '%" + searchMap.get("county") + "%'";
                }else{
                    searchTermsStr += " LOWER(county) LIKE '%" + searchMap.get("county") + "%'";
                }

                trackQuery++;
            }

            if(searchMap.get("max_price") != null){
                if(trackQuery >= 1) {
                    searchTermsStr += " AND CAST (maximum_price AS INTEGER) <= " + Integer.parseInt(String.valueOf(searchMap.get("max_price")));
                }else{
                    searchTermsStr += " CAST (maximum_price AS INTEGER) <= " + Integer.parseInt(String.valueOf(searchMap.get("max_price")));
                }

                trackQuery++;
            }

            if(searchMap.get("min_price") != null){
                if(trackQuery >= 1) {
                    searchTermsStr += " AND CAST (minimum_price AS INTEGER) >= " + Integer.parseInt(String.valueOf(searchMap.get("min_price")));
                }else{
                    searchTermsStr += " CAST (minimum_price AS INTEGER) >= " + Integer.parseInt(String.valueOf(searchMap.get("min_price")));
                }

                trackQuery++;
            }

            if(searchMap.get("location") != null){
                if(trackQuery >= 1) {
                    searchTermsStr += " AND LOWER(location) LIKE LOWER('%" + searchMap.get("location") + "%')";
                }else{
                    searchTermsStr += " LOWER(location) LIKE LOWER('%" + searchMap.get("location") + "%')";
                }

                trackQuery++;
            }

            String sql = "SELECT * FROM properties WHERE " + searchTermsStr + " AND LOWER(status)=LOWER(:status) ORDER BY id, created_at ASC LIMIT :limit OFFSET :offset";

            log.error("sql: " + sql);

            List<Map<String, Object>> properties = NamedBaseExecute(sql, param, where_param, new MapResultHandler());

            return !properties.isEmpty() ? properties : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
    // </editor-fold>
    //
    // <editor-fold default-state="collapsed" desc="deleteProperty(String id)">
    public Map<String, Object> deleteProperty(String propertyId) {
        try {
            LocalDateTime now = LocalDateTime.now();
            String sql;
            String table = "properties";

            LinkedHashMap<String, Object> params = new LinkedHashMap<>(){{
                put("status", Status.DELETED.name());
                put("updated_at", now);
                put("deleted_at", now);
                put("updated_by", "admin");
            }};

            params = Utility.cleanMap(params);

            LinkedHashMap<String, Object> where_params = new LinkedHashMap<>(){{
                put("property_id", propertyId);
            }};

            params = Utility.cleanMap(params);

            sql = Models.UpdateString(table, params, where_params);
            sql += " returning *";

            List<Map<String, Object>> results = NamedBaseExecute(sql, params, where_params, new MapResultHandler());

            if(!results.isEmpty()){
                return results.get(0);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return null;
    }
    // </editor-fold>
    //
    // <editor-fold default-state="collapsed" desc="createProperty(Map<String, Object> entryParams)">
    public Map<String, Object> createProperty(Map<String, Object> entryParams) {
        try {
            LinkedHashMap<String, Object> params = new LinkedHashMap<>();

            // A date-time without a time-zone in the ISO-8601 calendar system, such as 2007-12-03T10:15:30.
            LocalDateTime now = LocalDateTime.now();

            params.put("company", entryParams.get("company"));
            params.put("county", entryParams.get("county"));
            params.put("created_at", now);
            params.put("created_by", entryParams.get("user")); // TODO: Get user from authentication context
            params.put("latitude", entryParams.get("latitude"));
            params.put("location", entryParams.get("location"));
            params.put("longitude", entryParams.get("longitude"));
            params.put("maximum_price", entryParams.get("maximum_price"));
            params.put("metadata", entryParams.get("metadata"));
            params.put("minimum_price", entryParams.get("minimum_price"));
            params.put("property_description", entryParams.get("property_description"));
            params.put("property_id", entryParams.get("property_id"));
            params.put("property_name", entryParams.get("property_name"));
            params.put("property_price", entryParams.get("property_price"));
            params.put("property_type", entryParams.get("property_type"));
            params.put("property_amenities", entryParams.get("property_amenities"));
            params.put("published_at", now);
            params.put("status", Status.getStatusType(String.valueOf(entryParams.getOrDefault("status", "active"))));
            params.put("updated_at", now);
            params.put("updated_by", entryParams.get("user"));

            params.put("verified", entryParams.get("verified") != null ?
                    Boolean.parseBoolean((String.valueOf(entryParams.getOrDefault("verified", false)))) : Boolean.FALSE);

            params = Utility.cleanMap(params);

            String sql;
            String table = "properties";

            sql = Models.InsertString(table, params);
            sql += " returning *";

            List<Map<String, Object>> results = NamedBaseExecute(sql, params, null, new MapResultHandler());

            if(results != null && !results.isEmpty()){
                return results.get(0);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return null;
    }
    // </editor-fold>
    //
    // <editor-fold default-state="collapsed" desc="updateProperty(Map<String, Object> entryParams)">
    public Map<String, Object> updateProperty(Map<String, Object> entryParams) {
        try {
            LocalDateTime now = LocalDateTime.now();
            String sql;
            String table = "properties";

            if(Status.valueOf(String.valueOf(entryParams.get("status"))).equals(Status.DELETED)){
                return null;
            }

            LinkedHashMap<String, Object> params = new LinkedHashMap<>(){{
                put("company", entryParams.get("company"));
                put("county", entryParams.get("county"));
                put("created_at", now);
                put("created_by", entryParams.get("user")); // TODO: Get user from authentication context
                put("latitude", entryParams.get("latitude"));
                put("location", entryParams.get("location"));
                put("longitude", entryParams.get("longitude"));
                put("maximum_price", entryParams.get("maximum_price"));
                put("metadata", entryParams.get("metadata"));
                put("minimum_price", entryParams.get("minimum_price"));
                put("property_description", entryParams.get("property_description"));
                put("property_name", entryParams.get("property_name"));
                put("property_price", entryParams.get("property_price"));
                put("property_type", entryParams.get("property_type"));
                put("property_amenities", entryParams.get("property_amenities"));
                put("published_at", now);
                put("status", entryParams.get("status") != null ? Optional.of(Status.getStatusType(String.valueOf(entryParams.get("status"))))
                        .orElse(Status.getStatusType("active")) : Status.getStatusType("active"));
                put("updated_at", now);
                put("updated_by", entryParams.get("user"));
                put("verified", entryParams.get("verified") != null ?
                        Boolean.parseBoolean((String.valueOf(entryParams.getOrDefault("verified", false)))) : Boolean.FALSE);
            }};

            params = Utility.cleanMap(params);

            LinkedHashMap<String, Object> where_params = new LinkedHashMap<>(){{
                put("property_id", String.valueOf(entryParams.get("property_id")));
            }};

            sql = Models.UpdateString(table, params, where_params);

            sql += " returning *";

            List<Map<String, Object>> results = NamedBaseExecute(sql, params, where_params, new MapResultHandler());

            if(!results.isEmpty()){
                return results.get(0);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return null;
    }
    // </editor-fold>
    //
    // <editor-fold default-state="collapsed" desc="List<Map<String, Object>>listAllProperties(String pageNumber) ">
    public List<Map<String, Object>> listAllProperties(Map<String, Object> queryParams) {
        try {
            String sql = "SELECT * FROM properties WHERE LOWER(status)=LOWER(:status) ORDER BY id, created_at ASC LIMIT :limit OFFSET :offset";

            Map<String, Object> limitAndOffset = Utility.getLimitAndOffset(50, queryParams);

            int limit = Integer.parseInt(String.valueOf(limitAndOffset.get("limit")));
            int offset = Integer.parseInt(String.valueOf(limitAndOffset.get("offset")));

            LinkedHashMap<String, Object> where_param = new LinkedHashMap<>(){{
                put("status", queryParams.getOrDefault("status", "ACTIVE"));
                put("limit", limit);
                put("offset", offset);
            }};

            List<Map<String, Object>> results = NamedBaseExecute(sql, null, where_param, new MapResultHandler());

            if(results != null && !results.isEmpty()){
                return results;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return null;
    }
    // </editor-fold>

    /**-------------------- COMPANIES -------------------------*/

    // <editor-fold default-state="collapsed" desc="searchCompanyById(String id)">
    public Map<String, Object> searchCompanyById(String id) {
        try {
            LinkedHashMap<String, Object> param = new LinkedHashMap<>(){{
                put("company_id", id);
            }};

            String sql = "SELECT * FROM companies WHERE company_id=:company_id LIMIT 1";

            List<Map<String, Object>> results = NamedBaseExecute(sql, param, null, new MapResultHandler());

            if (results != null && !results.isEmpty()){
                return results.get(0);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return null;
    }
    // </editor-fold>
    //
    // <editor-fold default-state="collapsed" desc="deleteCompany(String id)">
    public Map<String, Object> deleteCompany(String id) {
        try {
            LocalDateTime now = LocalDateTime.now();
            LinkedHashMap<String, Object> params = new LinkedHashMap<>(){{
                put("status", Status.DELETED.name());
                put("updated_at", now);
                put("deleted_at", now);
                put("updated_by", "admin");
            }};

            LinkedHashMap<String, Object> where_params = new LinkedHashMap<>(){{
                put("company_id", id);
            }};

            String sql;
            String table = "companies";

            sql = Models.UpdateString(table, params, where_params);

            sql += " returning *";

            List<Map<String, Object>> results = NamedBaseExecute(sql, params, where_params, new MapResultHandler());

            if(results != null && !results.isEmpty()){
                return results.get(0);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return null;
    }
    // </editor-fold>
    //
    // <editor-fold default-state="collapsed" desc="createCompany(Map<String, Object> entryParams)">
    public Map<String, Object> createCompany(Map<String, Object> entryParams) {
        try {
            String companyId = "CMP_" + Utility.generateRandomFromUUID();
            LocalDateTime now = LocalDateTime.now();
            LinkedHashMap<String, Object> params = new LinkedHashMap<>(){{
                put("company_address", entryParams.get("company_address"));
                put("company_description", entryParams.get("company_description"));
                put("company_email", entryParams.get("company_email"));
                put("company_name", entryParams.get("company_name"));
                put("password", entryParams.get("password"));
                put("contact_person", entryParams.get("contact_person"));
                put("created_at", now);
                put("updated_at", now);
                put("api_key", entryParams.get("api_key"));
                put("api_password", entryParams.get("api_password"));
                put("api_access", Boolean.parseBoolean(String.valueOf(entryParams.getOrDefault("api_access", "false"))));
                put("created_by", entryParams.getOrDefault("user", "admin"));
                put("updated_by", entryParams.getOrDefault("user", "admin"));
                put("company_id", companyId);
                put("status", Status.getStatusType(String.valueOf(entryParams.getOrDefault("status", "active"))));
            }};

            params = Utility.cleanMap(params);

            String sql;
            String table = "companies";

            sql = Models.InsertString(table, params);
            sql += " returning *";

            List<Map<String, Object>> results = NamedBaseExecute(sql, params, null, new MapResultHandler());

            if(results != null && !results.isEmpty()){
                return results.get(0);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return null;
    }
    // </editor-fold>
    //
    // <editor-fold default-state="collapsed" desc="updateCompany(Map<String, Object> entryParams)">
    public Map<String, Object> updateCompany(Map<String, Object> entryParams) {
        try {
            LinkedHashMap<String, Object> params = new LinkedHashMap<>(){{
                put("password", entryParams.get("password"));
                put("company_name", entryParams.get("company_name"));
                put("company_description", entryParams.get("company_description"));
                put("company_address", entryParams.get("company_address"));
                put("company_email", entryParams.get("company_email"));
                put("contact_person", entryParams.get("contact_person"));
                put("updated_at", Timestamp.valueOf(LocalDateTime.now()));
                put("api_password", entryParams.get("api_password"));
                put("api_key", entryParams.get("api_key"));
                put("updated_by", entryParams.getOrDefault("user", "admin"));
                put("status", Status.getStatusType(String.valueOf(entryParams.getOrDefault("status", "active"))));
            }};

            params = Utility.cleanMap(params);

            LinkedHashMap<String, Object> where_params = new LinkedHashMap<>(){{
                put("company_id", String.valueOf(entryParams.get("company_id")));
            }};

            String sql;
            String table = "companies";

            sql = Models.UpdateString(table, params, where_params);

            sql += " returning *";

            List<Map<String, Object>> results = NamedBaseExecute(sql, params, where_params, new MapResultHandler());

            if(results != null && !results.isEmpty()){
                return results.get(0);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return null;
    }
    // </editor-fold>
    //
    // <editor-fold default-state="collapsed" desc="listAllCompanies(Map<String, Object> queryParams) ">
    public List<Map<String, Object>> listAllCompanies(Map<String, Object> queryParams) {
        String sql = "SELECT * FROM companies WHERE LOWER(status)=LOWER(:status) ORDER BY id, created_at ASC LIMIT :limit OFFSET :offset";

        Map<String, Object> limitAndOffset = Utility.getLimitAndOffset(50, queryParams);

        int limit = Integer.parseInt(String.valueOf(limitAndOffset.get("limit")));
        int offset = Integer.parseInt(String.valueOf(limitAndOffset.get("offset")));

        LinkedHashMap<String, Object> where_param = new LinkedHashMap<>(){{
            put("status", queryParams.getOrDefault("status", "ACTIVE"));
            put("limit", limit);
            put("offset", offset);
        }};

        List<Map<String, Object>> results = NamedBaseExecute(sql, null, where_param, new MapResultHandler());

        if(results != null && !results.isEmpty()){
            return results;
        }

        return null;
    }
    // </editor-fold>

    /**-------------------- PERMISSIONS -------------------------*/

    // <editor-fold default-state="collapsed" desc="searchPermissionById(String id)">
    public Map<String, Object> searchPermissionById(String id) {
        try {
            LinkedHashMap<String, Object> param = new LinkedHashMap<>();
            String sql = "SELECT * FROM permissions WHERE id=:id LIMIT 1";

            param.put("id", Integer.parseInt(id));

            List<Map<String, Object>> properties = NamedBaseExecute(sql, param, null, new MapResultHandler());

            return !properties.isEmpty() ? properties.get(0) : null;
        } catch (Exception e) {
            return null;
        }
    }
    // </editor-fold>
    //
    // <editor-fold default-state="collapsed" desc="deletePermission(String id)">
    public Map<String, Object> deletePermission(String id) {
        try {
            LocalDateTime now = LocalDateTime.now();
            String sql;
            String table = "permissions";

            LinkedHashMap<String, Object> params = new LinkedHashMap<>(){{
                put("updated_at", now);
                put("deleted_at", now);
                put("updated_by", "admin");
                put("status", Status.DELETED.name());
            }};

            LinkedHashMap<String, Object> where_params = new LinkedHashMap<>(){{
                put("id", Integer.parseInt(id));
            }};

            params = Utility.cleanMap(params);

            sql = Models.UpdateString(table, params, where_params);
            sql += " returning *";

            List<Map<String, Object>> results = NamedBaseExecute(sql, params, where_params, new MapResultHandler());

            if(!results.isEmpty()){
                return results.get(0);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return null;
    }
    // </editor-fold>
    //
    // <editor-fold default-state="collapsed" desc="createPermission(Map<String, Object> entryParams)">
    public Map<String, Object> createPermission(Map<String, Object> entryParams) {
        try {
            LinkedHashMap<String, Object> params = new LinkedHashMap<>();
            LocalDateTime now = LocalDateTime.now();

            params.put("action", String.valueOf(entryParams.get("action")).toUpperCase());

            if (entryParams.get("description") != null || !String.valueOf(entryParams.get("description")).isEmpty()) {
                params.put("description", entryParams.get("description"));
            } else {
                params.put("description", String.valueOf(entryParams.get("action")).toLowerCase());
            }

            params.put("status", Status.getStatusType(String.valueOf(entryParams.getOrDefault("status", "active"))));
            params.put("created_at", now);
            params.put("updated_at", now);
            params.put("created_by", entryParams.getOrDefault("user", "admin"));
            params.put("updated_by", entryParams.getOrDefault("user", "admin"));

            params = Utility.cleanMap(params);

            String sql;
            String table = "permissions";

            sql = Models.InsertString(table, params);
            sql += " returning *";

            List<Map<String, Object>> results = NamedBaseExecute(sql, params, null, new MapResultHandler());

            if(!results.isEmpty()){
                return results.get(0);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return null;
    }
    // </editor-fold>
    //
    // <editor-fold default-state="collapsed" desc="updatePermission(Map<String, Object> entryParams)">
    public Map<String, Object> updatePermission(Map<String, Object> entryParams) {
        try {
            LocalDateTime now = LocalDateTime.now();
            String sql;
            String table = "permissions";

            if(Status.valueOf(String.valueOf(entryParams.get("status"))).equals(Status.DELETED)){
                return null;
            }

            LinkedHashMap<String, Object> params = new LinkedHashMap<>(){{
                put("description", entryParams.get("description"));
                put("status", Status.valueOf(String.valueOf(entryParams.get("status"))));
                put("updated_at", now);
                put("updated_by", entryParams.getOrDefault("user", "admin"));
            }};

            params = Utility.cleanMap(params);

            LinkedHashMap<String, Object> where_params = new LinkedHashMap<>(){{
                put("id", Integer.parseInt(String.valueOf(entryParams.get("id"))));
            }};

            sql = Models.UpdateString(table, params, where_params);

            sql += " returning *";

            List<Map<String, Object>> results = NamedBaseExecute(sql, params, where_params,
                    new MapResultHandler());

            if(!results.isEmpty()){
                return results.get(0);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return null;
    }
    // </editor-fold>
    //
    // <editor-fold default-state="collapsed" desc="listAllPermissions(Map<String, Object> queryParams) ">
    public List<Map<String, Object>> listAllPermissions(Map<String, Object> queryParams) {
        String sql = "SELECT * FROM permissions WHERE LOWER(status)=LOWER(:status) ORDER BY id, created_at ASC LIMIT :limit OFFSET :offset";

        try {
            Map<String, Object> limitAndOffset = Utility.getLimitAndOffset(50, queryParams);

            int limit = Integer.parseInt(String.valueOf(limitAndOffset.get("limit")));
            int offset = Integer.parseInt(String.valueOf(limitAndOffset.get("offset")));

            LinkedHashMap<String, Object> where_param = new LinkedHashMap<>(){{
                put("status", queryParams.getOrDefault("status", "ACTIVE"));
                put("limit", limit);
                put("offset", offset);
            }};

            List<Map<String, Object>> permissions = NamedBaseExecute(sql, null, where_param, new MapResultHandler());

            return !permissions.isEmpty() ? permissions : null;
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return null;
    }
    // </editor-fold>

    /**-------------------- ROLES -------------------------*/

    // <editor-fold default-state="collapsed" desc="searchRoleById(String id)">
    public Map<String, Object> searchRoleById(String id) {
        try {
            String sql = "SELECT * FROM roles WHERE id=:id LIMIT 1";

            LinkedHashMap<String, Object> param = new LinkedHashMap<>(){{
                put("id", Integer.parseInt(id));
            }};

            List<Map<String, Object>> properties = NamedBaseExecute(sql, param, null, new MapResultHandler());

            if(!properties.isEmpty()){
                return properties.get(0);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return null;
    }
    // </editor-fold>
    //
    // <editor-fold default-state="collapsed" desc="deleteRole(String id)">
    public Map<String, Object> deleteRole(String id) {
        try {
            LocalDateTime now = LocalDateTime.now();
            String sql;
            String table = "roles";

            LinkedHashMap<String, Object> params = new LinkedHashMap<>(){{
                put("updated_by", "admin");
                put("updated_at", now);
                put("deleted_at", now);
                put("status", Status.DELETED.name());
            }};

            LinkedHashMap<String, Object> where_params = new LinkedHashMap<>(){{
                put("id", Integer.parseInt(id));
            }};

            params = Utility.cleanMap(params);

            sql = Models.UpdateString(table, params, where_params);
            sql += " returning *";

            List<Map<String, Object>> results = NamedBaseExecute(sql, params, where_params, new MapResultHandler());

            if(!results.isEmpty()){
                return results.get(0);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return null;
    }
    // </editor-fold>
    //
    // <editor-fold default-state="collapsed" desc="createRole(Map<String, Object> entryParams)">
    public Map<String, Object> createRole(Map<String, Object> entryParams) {
        try {
            LinkedHashMap<String, Object> params = new LinkedHashMap<>();
            LocalDateTime now = LocalDateTime.now();

            params.put("name", String.valueOf(entryParams.get("name")).toUpperCase());

            if(entryParams.get("description") != null || !String.valueOf(entryParams.get("description")).isEmpty()){
                params.put("description", entryParams.get("description"));
            }else {
                params.put("description", String.valueOf(entryParams.get("name")).toLowerCase());
            }

            params.put("status", Status.getStatusType(String.valueOf(entryParams.getOrDefault("status", "active"))));
            params.put("created_at", now);
            params.put("updated_at", now);
            params.put("created_by", entryParams.getOrDefault("user", "admin"));
            params.put("updated_by", entryParams.getOrDefault("user", "admin"));

            if(entryParams.get("permissions") == null){
                return null;
            }

            List<String> permissionsProvidedStr = mapper.convertValue(entryParams.get("permissions"), new TypeReference<>() {});
            List<Integer> permissionsProvidedInt = new ArrayList<>(new HashSet<>(permissionsProvidedStr.stream().map(Integer::parseInt).toList()));
            List<Map<String, Object>> permissionsFound = searchPermissions(permissionsProvidedInt);

            if(!permissionsFound.isEmpty()){
                String permissionsString = permissionsFound.stream()
                        .map(stringObjectMap -> String.valueOf(stringObjectMap.get("id"))).collect(Collectors.joining(",", "", ""));

                params.put("permissions", permissionsString);

                params = Utility.cleanMap(params);

                String sql;
                String table = "roles";

                sql = Models.InsertString(table, params);
                sql += " returning *";

                List<Map<String, Object>> results = NamedBaseExecute(sql, params, null, new MapResultHandler());

                if(!results.isEmpty()){
                    return results.get(0);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return null;
    }
    // </editor-fold>
    //
    // <editor-fold default-state="collapsed" desc="searchPermissions(List<String> permissionsGiven))">
    public List<Map<String, Object>> searchPermissions(List<Integer> permissionsGivenMayHaveDuplicates){
        try {
            List<Integer> permissionsWithoutDuplicates = new ArrayList<>(new HashSet<>(permissionsGivenMayHaveDuplicates));
            String permissionsStringInBrackets = permissionsWithoutDuplicates.stream()
                    .map(String::valueOf).collect(Collectors.joining(",", "(", ")"));

            String sql = "SELECT * FROM permissions WHERE id IN " + permissionsStringInBrackets + " AND status='ACTIVE'";

            return NamedBaseExecute(sql, null, null, new MapResultHandler());
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return new ArrayList<>();
    }
    // </editor-fold>
    //
    // <editor-fold default-state="collapsed" desc="findAllPermissionsAssignedToRole(String roleId))">
    public List<Object> findAllPermissionsAssignedToRole(String roleId){
        try {
            LinkedHashMap<String, Object> param = new LinkedHashMap<>();
            String sql = "SELECT permission_id FROM permissions_role_links WHERE id=:id ORDER BY id";

            List<Map<String, Object>> permissions = NamedBaseExecute(sql, param, null, new MapResultHandler());
            List<String> permissionsArr = permissions.stream().map(s -> String.valueOf(s.get("permission_id"))).toList();

            return !permissions.isEmpty() ? mapper.convertValue(permissionsArr, new TypeReference<>() {}) : null;
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return null;
    }
    // </editor-fold>
    //
    // <editor-fold default-state="collapsed" desc="updateRole(Map<String, Object> entryParams)">
    public Map<String, Object> updateRole(Map<String, Object> entryParams){
        try {
            LocalDateTime now = LocalDateTime.now();
            String table = "roles";
            String sql;
            LinkedHashMap<String, Object> roleParams = new LinkedHashMap<>();

            if (entryParams.get("permissions") != null) {
                List<String> permissionsProvidedStr = mapper.convertValue(entryParams.get("permissions"), new TypeReference<>() {});
                List<Integer> permissionsProvidedInt = new ArrayList<>(new HashSet<>(permissionsProvidedStr.stream()
                        .map(Integer::parseInt).toList()));

                List<Map<String, Object>> permissionsFound = searchPermissions(permissionsProvidedInt);

                if(!permissionsFound.isEmpty()){
                    String permissionsString = permissionsFound.stream()
                            .map(stringObjectMap -> String.valueOf(stringObjectMap.get("id"))).collect(Collectors.joining(",", "", ""));

                    roleParams.put("permissions", permissionsString);
                }
            }

            if (entryParams.get("status") != null && !Status.valueOf(String.valueOf(entryParams.get("status"))).equals(Status.DELETED)) {
                roleParams.put("status", Status.valueOf(String.valueOf(entryParams.get("status"))).name());
            }

            roleParams.put("description", entryParams.get("description"));
            roleParams.put("updated_at", now);
            roleParams.put("updated_by", entryParams.getOrDefault("user", "admin"));

            roleParams = Utility.cleanMap(roleParams);

            LinkedHashMap<String, Object> where_params = new LinkedHashMap<>(){{
                put("id", Integer.parseInt(String.valueOf(entryParams.get("id"))));
            }};

            sql = Models.UpdateString(table, roleParams, where_params);
            sql += " returning *";

            List<Map<String, Object>> results = NamedBaseExecute(sql, roleParams, where_params,
                    new MapResultHandler());

            if(!results.isEmpty()){
                return results.get(0);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return null;
    }

    // </editor-fold>
    //
    // <editor-fold default-state="collapsed" desc="listAllRoles(Map<String, Object> queryParams) ">
    public List<Map<String, Object>> listAllRoles(Map<String, Object> queryParams) {
        String sql = "SELECT * FROM roles WHERE LOWER(status)=LOWER(:status) ORDER BY id ASC LIMIT :limit OFFSET :offset";

        try {
            Map<String, Object> limitAndOffset = Utility.getLimitAndOffset(50, queryParams);

            int limit = Integer.parseInt(String.valueOf(limitAndOffset.get("limit")));
            int offset = Integer.parseInt(String.valueOf(limitAndOffset.get("offset")));

            LinkedHashMap<String, Object> where_param = new LinkedHashMap<>(){{
                put("status", queryParams.getOrDefault("status", "ACTIVE"));
                put("limit", limit);
                put("offset", offset);
            }};

            List<Map<String, Object>> property = NamedBaseExecute(sql, null, where_param, new MapResultHandler());

            return !property.isEmpty() ? property : null;
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return null;
    }
    // </editor-fold>
}