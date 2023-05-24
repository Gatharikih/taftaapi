package org.tafta.taftaapi.repo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jodd.db.DbOom;
import jodd.db.DbQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tafta.taftaapi.enums.CompanyStatus;
import org.tafta.taftaapi.enums.PropertyStatus;
import org.tafta.taftaapi.enums.Role_PermissionStatus;
import org.tafta.taftaapi.enums.UserStatus;
import org.tafta.taftaapi.utility.ConvertTo;

import javax.crypto.Mac;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

/**
 * @author Gathariki Ngigi
 * Created on April 27, 2023.
 * Time 0959h
 */

@Slf4j
@Component
public class DBFunctionImpl implements DBFunction {
    @Autowired
    private DbConnectionsHandler connectionsHandler;

    private static final SimpleDateFormat DATE_FORMAT_COMPRESSED = new SimpleDateFormat("yyMMdd");
    static Mac mac = null;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static DbOom dbOom = null;
    int trackQuery;
    int numOfOperations;

    boolean addNewPermission = false, stripePermission = false;

    public enum LogType {ERRORS, APIREQUESTS, RESPONSES, MONEYTRANS, COOPCALLBACK}

    private static final DateTimeFormatter defaultTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss", Locale.ENGLISH);
    private final Map<String, Object> exceptions = new HashMap<>();

    public DBFunctionImpl() {
        mac = HmacUtils.getInitializedMac(HmacAlgorithms.HMAC_SHA_512, "3pA4KkUTeGydlxLwht1kIOweCEGOztJAjNKvXa5QPlXlCLnd5hj8KC19fUg3".getBytes(StandardCharsets.UTF_8));
    }

    // <editor-fold default-state="collapsed" desc="NamedBaseExecute">
    public <T> T NamedBaseExecute(String sql, LinkedHashMap<String, Object> collection, LinkedHashMap<String, Object> whereCollection, ResultHandler<T> handler) {
        return NamedBaseExecute(sql, collection, whereCollection, handler, "");
    }

    public <T> T NamedBaseExecute(String sql, LinkedHashMap<String, Object> collection, LinkedHashMap<String, Object> whereCollection, ResultHandler<T> handler, String profile) {
        T output = null;
        Connection connection = null;
        ResultSet rs = null;

        try {
            connection = connectionsHandler.getConnection();
            if (dbOom == null) {
                dbOom = new DbOom.Builder().get();
            }

            // use connection
            DbQuery query = new DbQuery(dbOom, connection, sql);

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
                e.printStackTrace();

                exceptions.put("errors", e.getCause().getMessage());
            }

            query.close();

            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();

            exceptions.put("errors", e.getCause().getMessage());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();

                    exceptions.put("errors", e.getCause().getMessage());

                    for (StackTraceElement ste : e.getStackTrace()) {
                        log.error(ste.toString());
                    }
                }
            }
        }

        return output;
    }

    public interface ResultHandler<T> {
        T handle(ResultSet rs);
    }
    // </editor-fold>
    //
    // <editor-fold default-state="collapsed" desc="cleanMap(LinkedHashMap<String, Object> map)">
    public static LinkedHashMap<String, Object> cleanMap(LinkedHashMap<String, Object> map) {
        if (map != null) {
            LinkedHashMap<String, Object> retMap = new LinkedHashMap<>();

            for (Map.Entry<String, Object> param : map.entrySet()) {
                if (param.getValue() != null) {
                    retMap.put(param.getKey(), param.getValue());
                }
            }

            return retMap;
        }

        return null;
    }
    // </editor-fold>
    //

    /*-------------------- USERS -------------------------*/

    // <editor-fold default-state="collapsed" desc="createUser(Map<String, Object> entryParams)">
    @Override
    public List<Map<String, Object>> createUser(Map<String, Object> entryParams) {
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();

        if(entryParams.get("company_id") != null){
            params.put("company_id", entryParams.get("company_id").toString());
        }
        params.put("role_id", Optional.ofNullable(entryParams.get("role_id"))
                .orElse(getUserRoleId("user").size() > 0 ? Integer.parseInt(getUserRoleId("user").get(0).get("id").toString()) : 5));

        if(entryParams.get("fullname") != null){
            params.put("fullname", entryParams.get("fullname").toString());
        }

        if(entryParams.get("email") != null){
            params.put("email", entryParams.get("email").toString());
        }

        if(entryParams.get("password") != null){
            params.put("password", entryParams.get("password").toString());
        }

        if(entryParams.get("msisdn") != null){
            params.put("msisdn", entryParams.get("msisdn").toString());
        }

        if(entryParams.get("reset_password") != null){
            params.put("reset_password", Optional.ofNullable(entryParams.get("reset_password")).orElse(true));
        }

        if(entryParams.get("status") != null){
            try {
                params.put("status", Optional.of(UserStatus.getUserStatusType(entryParams.get("status").toString())).orElse(UserStatus.getUserStatusType("active")));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        params = cleanMap(params);

        String sql;
        String table = "users";

        sql = Models.InsertString(table, params);
        sql += " returning *";

        List<Map<String, Object>> results = NamedBaseExecute(sql, params, null, new MapResultHandler());

        if(results.size() > 0){
            return results;
        }

        return null;
    }
    // </editor-fold>
    //
    // <editor-fold default-state="collapsed" desc="updateUser(Map<String, Object> entryParams)">
    @Override
    public List<Map<String, Object>> updateUser(Map<String, Object> entryParams) {
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();

        if(entryParams.get("company_id") != null){
            params.put("company_id", entryParams.get("company_id").toString());
        }
        params.put("role_id", Optional.ofNullable(entryParams.get("role_id"))
                .orElse(getUserRoleId("user").size() > 0 ? Integer.parseInt(getUserRoleId("user").get(0).get("id").toString()) : 5));

        if(entryParams.get("fullname") != null){
            params.put("fullname", entryParams.get("fullname").toString());
        }

        if(entryParams.get("email") != null){
            params.put("email", entryParams.get("email").toString());
        }

        if(entryParams.get("password") != null){
            params.put("password", entryParams.get("password").toString());
        }

        if(entryParams.get("msisdn") != null){
            params.put("msisdn", entryParams.get("msisdn").toString());
        }

        if(entryParams.get("reset_password") != null){
            params.put("reset_password", Optional.ofNullable(entryParams.get("reset_password")).orElse(true));
        }

        if(entryParams.get("status") != null){
            try {
                params.put("status", UserStatus.getUserStatusType(entryParams.get("status").toString()));
            } catch (Exception e) {
                e.printStackTrace();

//                throw new RuntimeException("Unrecognized status");
                return new ArrayList<>();
            }
        }

        params.put("updated_at", Timestamp.valueOf(LocalDateTime.now()));
        params.put("deleted_at", null);

        params = cleanMap(params);

        LinkedHashMap<String, Object> where_params = new LinkedHashMap<>();

        String sql;
        String table = "users";

        where_params.put("id", Integer.parseInt(entryParams.get("id").toString()));

        sql = Models.UpdateString(table, params, where_params);

        sql += " returning *";

        List<Map<String, Object>> results = NamedBaseExecute(sql, params, where_params, new MapResultHandler());

        if(results.size() > 0){
            return results;
        }

        return null;
    }
    // </editor-fold>
    //
    // <editor-fold default-state="collapsed" desc="getUserRoleId(String roleName)">
    @Override
    public List<Map<String, Object>> getUserRoleId(String roleName) {
        LinkedHashMap<String, Object> param = new LinkedHashMap<>();
        String sql = "SELECT id FROM roles WHERE name=:name OR description =:name LIMIT 1";

        param.put("name", roleName);
        param.put("currency", roleName.toLowerCase());

        return NamedBaseExecute(sql, param, null, new MapResultHandler());
    }
    // </editor-fold>
    //
    // <editor-fold default-state="collapsed" desc="searchUserByEmailOrPhoneNumber(String searchTerm)">
    @Override
    public List<Map<String, Object>> searchUserByEmailOrPhoneNumber(String searchTerm) {
        LinkedHashMap<String, Object> param = new LinkedHashMap<>();
        String sql = "SELECT * FROM users WHERE email=:email OR msisdn=:msisdn OR name=:name LIMIT 1 ORDER BY id, created_at ASC";

        param.put("email", searchTerm);
        param.put("msisdn", searchTerm);
        param.put("name", searchTerm);

        List<Map<String, Object>> user = NamedBaseExecute(sql, param, null, new MapResultHandler());

        return user.size() > 0 ? user : null;
    }
    // </editor-fold>
    //
    // <editor-fold default-state="collapsed" desc="List<Map<String, Object>>listAllUsers(String pageNumber) ">
    @Override
    public List<Map<String, Object>> listAllUsers(Map<String, Object> queryParams) {
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();
        LinkedHashMap<String, Object> where_param = new LinkedHashMap<>();
        String sql = "SELECT * FROM users WHERE status=:status ORDER BY id, created_at ASC LIMIT :limit OFFSET :offset";

        params = cleanMap(params);

        int limit = 50;

        where_param.put("status", queryParams.getOrDefault("status", "ACTIVE").toString());
        where_param.put("limit", limit);
        where_param.put("offset", Integer.parseInt(queryParams.getOrDefault("page_number", "0").toString()) * limit);

        log.error("where_param: " + where_param);

        List<Map<String, Object>> user = NamedBaseExecute(sql, params, where_param, new MapResultHandler());

        return user.size() > 0 ? user : null;
    }
    // </editor-fold>
    //
    // <editor-fold default-state="collapsed" desc="searchUserById(String id)">
    @Override
    public Map<String, Object> searchUserById(String id) {
        LinkedHashMap<String, Object> param = new LinkedHashMap<>();
        String sql = "SELECT * FROM users WHERE id=:id LIMIT 1";

        param.put("id", Integer.parseInt(id));

        List<Map<String, Object>> users = NamedBaseExecute(sql, param, null, new MapResultHandler());

        return users.size() > 0 ? users.get(0) : null;
    }
    // </editor-fold>
    //
    // <editor-fold default-state="collapsed" desc="deleteUser(String id)">
    @Override
    public Map<String, Object> deleteUser(String id) {
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();

        params.put("status", UserStatus.DELETED);
        params.put("deleted_at", Timestamp.valueOf(LocalDateTime.now()));

        LinkedHashMap<String, Object> where_params = new LinkedHashMap<>();
        where_params.put("id", Integer.parseInt(id));

        String sql;
        String table = "users";

        where_params.put("id", Integer.parseInt(id));

        sql = Models.UpdateString(table, params, where_params);

        sql += " returning *";

        List<Map<String, Object>> results = NamedBaseExecute(sql, params, where_params, new MapResultHandler());

        if(results.size() > 0){
            return results.get(0);
        }

        return null;
    }
    // </editor-fold>
    //

    /*-------------------- PROPERTIES -------------------------*/

    // <editor-fold default-state="collapsed" desc="searchPropertyById(String id)">
    @Override
    public Map<String, Object> searchPropertyById(String id) {
        LinkedHashMap<String, Object> param = new LinkedHashMap<>();
        String sql = "SELECT * FROM properties WHERE id=:id LIMIT 1";

        param.put("id", Integer.parseInt(id));

        List<Map<String, Object>> properties = NamedBaseExecute(sql, param, null, new MapResultHandler());

        return properties.size() > 0 ? properties.get(0) : null;
    }
    // </editor-fold>
    //
    // <editor-fold default-state="collapsed" desc="searchProperties(Map<String, Object> searchMap)">
    @Override
    public List<Map<String, Object>> searchProperties(Map<String, Object> searchMap) {
        LinkedHashMap<String, Object> param = new LinkedHashMap<>();
        String searchTermsStr = "";
        trackQuery = 0;

        if(searchMap.get("county") != null){
            if(trackQuery >= 1) {
                searchTermsStr += " AND LOWER(county)=LOWER(" + searchMap.get("county").toString() + ")";
            }else{
                searchTermsStr += " LOWER(county)=LOWER(" + searchMap.get("county").toString() + ")";
            }

            trackQuery = trackQuery + 1;
        }

        if(searchMap.get("property_name") != null){
            if(trackQuery >= 1){
                searchTermsStr += " AND LOWER(property_name)=LOWER(" + searchMap.get("property_name").toString() + ")";
            }else{
                searchTermsStr += " LOWER(property_name)=LOWER(" + searchMap.get("property_name").toString() + ")";
            }

            trackQuery = trackQuery + 1;
        }

        if(searchMap.get("min_price") != null){
            if(trackQuery >= 1) {
                searchTermsStr += " AND CAST (property_price AS INTEGER) >= " + Integer.parseInt(searchMap.get("min_price").toString());
            }else{
                searchTermsStr += " CAST (property_price AS INTEGER) >= " + Integer.parseInt(searchMap.get("min_price").toString());
            }

            trackQuery = trackQuery + 1;
        }

        if(searchMap.get("max_price") != null){
            if(trackQuery >= 1) {
                searchTermsStr += " AND CAST (property_price AS INTEGER) <= " + Integer.parseInt(searchMap.get("max_price").toString());
            }else{
                searchTermsStr += " CAST (property_price AS INTEGER) <= " + Integer.parseInt(searchMap.get("max_price").toString());
            }
        }

        if(searchMap.get("property_price") != null){
            if(trackQuery >= 1) {
                searchTermsStr += " and property_price=" + searchMap.get("property_price").toString();
            }else{
                searchTermsStr += " property_price=" + searchMap.get("property_price").toString();
            }
        }

        if(searchMap.get("description") != null){
            if(trackQuery >= 1) {
                searchTermsStr += " and LOWER(property_description) LIKE LOWER('%" + searchMap.get("description").toString() + "%')";
            }else{
                searchTermsStr += " LOWER(property_description) LIKE LOWER('%" + searchMap.get("description").toString() + "%')";
            }
        }

        if(searchMap.get("location") != null){
            if(trackQuery >= 1) {
                searchTermsStr += " and LOWER(location) LIKE LOWER('%" + searchMap.get("location").toString() + "%')";
            }else{
                searchTermsStr += " LOWER(location) LIKE LOWER('%" + searchMap.get("location").toString() + "%')";
            }
        }

        String sql = "SELECT * FROM properties WHERE " + searchTermsStr + " ORDER BY id, created_at ASC LIMIT 1";

        log.error("sql11: " + sql);

        List<Map<String, Object>> properties = NamedBaseExecute(sql, param, null, new MapResultHandler());

        return properties.size() > 0 ? properties : null;
    }
    // </editor-fold>
    //
    // <editor-fold default-state="collapsed" desc="deleteProperty(String id)">
    @Override
    public Map<String, Object> deleteProperty(String id) {
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();

        params.put("status", PropertyStatus.DELETED);
        params.put("deleted_at", Timestamp.valueOf(LocalDateTime.now()));

        LinkedHashMap<String, Object> where_params = new LinkedHashMap<>();
        where_params.put("id", Integer.parseInt(id));

        String sql;
        String table = "properties";

        where_params.put("id", Integer.parseInt(id));

        sql = Models.UpdateString(table, params, where_params);

        sql += " returning *";

        List<Map<String, Object>> results = NamedBaseExecute(sql, params, where_params, new MapResultHandler());

        if(results.size() > 0){
            return results.get(0);
        }

        return null;
    }
    // </editor-fold>
    //
    // <editor-fold default-state="collapsed" desc="createProperty(Map<String, Object> entryParams)">
    @Override
    public List<Map<String, Object>> createProperty(Map<String, Object> entryParams) {
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();

        params.put("county", entryParams.get("county").toString());
        params.put("latitude", entryParams.get("latitude").toString());
        params.put("longitude", entryParams.get("longitude").toString());
        params.put("location", entryParams.get("location").toString());
        params.put("property_description", entryParams.get("property_description").toString());
        params.put("property_name", entryParams.get("property_name").toString());
        params.put("property_price", entryParams.get("property_price").toString());
        params.put("manager", entryParams.get("role_id"));
        params.put("maximum_price", entryParams.get("maximum_price").toString());
        params.put("metadata", entryParams.get("metadata").toString());
        params.put("minimum_price", entryParams.get("minimum_price").toString());

        if(entryParams.get("property_id") != null){
            params.put("property_id", entryParams.get("property_id").toString());
        }else{
            String propertyId = UUID.randomUUID().toString();

            params.put("property_id", propertyId);
        }

        if(entryParams.get("status") != null){
            try {
                params.put("status", Optional.of(UserStatus.getUserStatusType(entryParams.get("status").toString())).orElse(UserStatus.getUserStatusType("active")));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        if(entryParams.get("verified") != null){
            params.put("verified", Boolean.parseBoolean(entryParams.getOrDefault("verified", false).toString()));
        }

        params = cleanMap(params);

        String sql;
        String table = "properties";

        sql = Models.InsertString(table, params);
        sql += " returning *";

        List<Map<String, Object>> results = NamedBaseExecute(sql, params, null, new MapResultHandler());

        if(results.size() > 0){
            return results;
        }

        return null;
    }
    // </editor-fold>
    //
    // <editor-fold default-state="collapsed" desc="updateProperty(Map<String, Object> entryParams)">
    @Override
    public List<Map<String, Object>> updateProperty(Map<String, Object> entryParams) {
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();

        if(entryParams.get("county") != null) {
            params.put("county", entryParams.get("county").toString());
        }

        if(entryParams.get("latitude") != null) {
            params.put("latitude", entryParams.get("latitude").toString());
        }

        if(entryParams.get("longitude") != null) {
            params.put("longitude", entryParams.get("longitude").toString());
        }

        if(entryParams.get("location") != null) {
            params.put("location", entryParams.get("location").toString());
        }

        if(entryParams.get("property_description") != null) {
            params.put("property_description", entryParams.get("property_description").toString());
        }

        if(entryParams.get("property_name") != null) {
            params.put("property_name", entryParams.get("property_name").toString());
        }

        if(entryParams.get("property_price") != null) {
            params.put("property_price", entryParams.get("property_price").toString());
        }

        if(entryParams.get("manager") != null) {
            params.put("manager", entryParams.get("role_id"));
        }

        if(entryParams.get("maximum_price") != null){
            params.put("maximum_price", entryParams.get("maximum_price").toString());
        }

        if(entryParams.get("metadata") != null){
            params.put("metadata", entryParams.get("metadata").toString());
        }

        if(entryParams.get("minimum_price") != null){
            params.put("minimum_price", entryParams.get("minimum_price").toString());
        }

        if(entryParams.get("status") != null){
            try {
                params.put("status", UserStatus.getUserStatusType(entryParams.get("status").toString()));

                if(UserStatus.getUserStatusType(entryParams.get("status").toString()).equalsIgnoreCase(PropertyStatus.DELETED.name())){
                    params.put("deleted_at", Timestamp.valueOf(LocalDateTime.now()));
                }
            } catch (Exception e) {
                e.printStackTrace();

                throw new RuntimeException(e);
            }
        }

        if(entryParams.get("verified") != null){
            params.put("verified", Boolean.parseBoolean(entryParams.getOrDefault("verified", false).toString()));
        }

        params.put("updated_at", Timestamp.valueOf(LocalDateTime.now()));

        params = cleanMap(params);

        LinkedHashMap<String, Object> where_params = new LinkedHashMap<>();

        String sql;
        String table = "properties";

        if(entryParams.get("id") != null) {
            where_params.put("id", Integer.parseInt(entryParams.get("id").toString()));
        }else{
            return null;
        }

        sql = Models.UpdateString(table, params, where_params);

        sql += " returning *";

        List<Map<String, Object>> results = NamedBaseExecute(sql, params, where_params, new MapResultHandler());

        if(results.size() > 0){
            return results;
        }

        return null;
    }
    // </editor-fold>
    //
    // <editor-fold default-state="collapsed" desc="List<Map<String, Object>>listAllProperties(String pageNumber) ">
    @Override
    public List<Map<String, Object>> listAllProperties(Map<String, Object> queryParams) {
        LinkedHashMap<String, Object> where_param = new LinkedHashMap<>();

        String sql = "SELECT * FROM properties WHERE LOWER(status)=LOWER(:status) ORDER BY id, created_at ASC LIMIT :limit OFFSET :offset";

        int limit = 50;

        where_param.put("status", UserStatus.valueOf(queryParams.getOrDefault("status", "ACTIVE").toString()));
        where_param.put("limit", limit);

        if(queryParams.get("page_number") == null){
            where_param.put("offset", 0);
        }else{
            where_param.put("offset", (Integer.parseInt(queryParams.getOrDefault("page_number", "0").toString()) - 1)* limit);
        }

        List<Map<String, Object>> property = NamedBaseExecute(sql, null, where_param, new MapResultHandler());

        return property.size() > 0 ? property : null;
    }
    // </editor-fold>
    //

    /*-------------------- COMPANIES -------------------------*/

    // <editor-fold default-state="collapsed" desc="searchCompanyById(String id)">
    @Override
    public Map<String, Object> searchCompanyById(String id) {
        LinkedHashMap<String, Object> param = new LinkedHashMap<>();
        String sql = "SELECT * FROM companies WHERE id=:id LIMIT 1";

        param.put("id", Integer.parseInt(id));

        List<Map<String, Object>> properties = NamedBaseExecute(sql, param, null, new MapResultHandler());

        return properties.size() > 0 ? properties.get(0) : null;
    }
    // </editor-fold>
    //
    // <editor-fold default-state="collapsed" desc="searchCompanies(Map<String, Object> searchMap)">
    @Override
    public List<Map<String, Object>> searchCompanies(Map<String, Object> searchMap) {
        LinkedHashMap<String, Object> param = new LinkedHashMap<>();
        String searchTermsStr = "";
        trackQuery = 0;

        if(searchMap.get("county") != null){
            if(trackQuery >= 1) {
                searchTermsStr += " AND LOWER(county)=LOWER(" + searchMap.get("county").toString() + ")";
            }else{
                searchTermsStr += " LOWER(county)=LOWER(" + searchMap.get("county").toString() + ")";
            }

            trackQuery = trackQuery + 1;
        }

        if(searchMap.get("property_name") != null){
            if(trackQuery >= 1){
                searchTermsStr += " AND LOWER(property_name)=LOWER(" + searchMap.get("property_name").toString() + ")";
            }else{
                searchTermsStr += " LOWER(property_name)=LOWER(" + searchMap.get("property_name").toString() + ")";
            }

            trackQuery = trackQuery + 1;
        }

        if(searchMap.get("min_price") != null){
            if(trackQuery >= 1) {
                searchTermsStr += " AND CAST (property_price AS INTEGER) >= " + Integer.parseInt(searchMap.get("min_price").toString());
            }else{
                searchTermsStr += " CAST (property_price AS INTEGER) >= " + Integer.parseInt(searchMap.get("min_price").toString());
            }

            trackQuery = trackQuery + 1;
        }

        if(searchMap.get("max_price") != null){
            if(trackQuery >= 1) {
                searchTermsStr += " AND CAST (property_price AS INTEGER) <= " + Integer.parseInt(searchMap.get("max_price").toString());
            }else{
                searchTermsStr += " CAST (property_price AS INTEGER) <= " + Integer.parseInt(searchMap.get("max_price").toString());
            }
        }

        if(searchMap.get("property_price") != null){
            if(trackQuery >= 1) {
                searchTermsStr += " and property_price=" + searchMap.get("property_price").toString();
            }else{
                searchTermsStr += " property_price=" + searchMap.get("property_price").toString();
            }
        }

        if(searchMap.get("description") != null){
            if(trackQuery >= 1) {
                searchTermsStr += " and LOWER(property_description) LIKE LOWER('%" + searchMap.get("description").toString() + "%')";
            }else{
                searchTermsStr += " LOWER(property_description) LIKE LOWER('%" + searchMap.get("description").toString() + "%')";
            }
        }

        if(searchMap.get("location") != null){
            if(trackQuery >= 1) {
                searchTermsStr += " and LOWER(location) LIKE LOWER('%" + searchMap.get("location").toString() + "%')";
            }else{
                searchTermsStr += " LOWER(location) LIKE LOWER('%" + searchMap.get("location").toString() + "%')";
            }
        }

        String sql = "SELECT * FROM properties WHERE " + searchTermsStr + " ORDER BY id, created_at ASC LIMIT 1";

        log.error("sql11: " + sql);

        List<Map<String, Object>> properties = NamedBaseExecute(sql, param, null, new MapResultHandler());

        return properties.size() > 0 ? properties : null;
    }
    // </editor-fold>
    //
    // <editor-fold default-state="collapsed" desc="deleteCompany(String id)">
    @Override
    public Map<String, Object> deleteCompany(String id) {
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();

        params.put("status", CompanyStatus.DELETED);
        params.put("deleted_at", Timestamp.valueOf(LocalDateTime.now()));

        LinkedHashMap<String, Object> where_params = new LinkedHashMap<>();
        where_params.put("id", Integer.parseInt(id));

        String sql;
        String table = "companies";

        where_params.put("id", Integer.parseInt(id));

        sql = Models.UpdateString(table, params, where_params);

        sql += " returning *";

        List<Map<String, Object>> results = NamedBaseExecute(sql, params, where_params, new MapResultHandler());

        if(results.size() > 0){
            return results.get(0);
        }

        return null;
    }
    // </editor-fold>
    //
    // <editor-fold default-state="collapsed" desc="createCompany(Map<String, Object> entryParams)">
    @Override
    public List<Map<String, Object>> createCompany(Map<String, Object> entryParams) {
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();

        params.put("company_address", entryParams.get("company_address").toString());
        params.put("company_description", entryParams.get("company_description").toString());
        params.put("company_email", entryParams.get("company_email").toString());
        params.put("company_name", entryParams.get("company_name").toString());

        params.put("password", entryParams.get("password").toString());
        params.put("contact_person", entryParams.get("contact_person").toString());
        params.put("created_at", Timestamp.valueOf(LocalDateTime.now()));
        params.put("updated_at", Timestamp.valueOf(LocalDateTime.now()));
        params.put("published_at", Timestamp.valueOf(LocalDateTime.now()));
        params.put("api_key", entryParams.get("api_key").toString());
        params.put("api_password", entryParams.get("api_password").toString());
//        params.put("api_access", entryParams.get("api_access").toString());
        params.put("created_by", entryParams.getOrDefault("created_by", "0").toString());
        params.put("updated_by", entryParams.getOrDefault("updated_by", "0").toString());

        if(entryParams.get("company_id") != null){
            params.put("company_id", entryParams.get("company_id").toString());
        }else{
            String propertyId = UUID.randomUUID().toString();

            params.put("company_id", propertyId);
        }

        if(entryParams.get("status") != null){
            try {
                params.put("status", Optional.of(CompanyStatus.getCompanyStatusType(entryParams.get("status").toString())).orElse(UserStatus.getUserStatusType("active")));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        params = cleanMap(params);

        String sql;
        String table = "properties";

        sql = Models.InsertString(table, params);
        sql += " returning *";

        List<Map<String, Object>> results = NamedBaseExecute(sql, params, null, new MapResultHandler());

        if(results.size() > 0){
            return results;
        }

        return null;
    }
    // </editor-fold>
    //
    // <editor-fold default-state="collapsed" desc="updateCompany(Map<String, Object> entryParams)">
    @Override
    public List<Map<String, Object>> updateCompany(Map<String, Object> entryParams) {
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();

        if(entryParams.get("password") != null) {
            params.put("password", entryParams.get("password").toString());
        }

        if(entryParams.get("company_name") != null) {
            params.put("company_name", entryParams.get("company_name").toString());
        }

        if(entryParams.get("company_description") != null) {
            params.put("company_description", entryParams.get("company_description").toString());
        }

        if(entryParams.get("company_address") != null) {
            params.put("company_address", entryParams.get("company_address").toString());
        }

        if(entryParams.get("company_email") != null) {
            params.put("company_email", entryParams.get("company_email").toString());
        }

        if(entryParams.get("contact_person") != null) {
            params.put("contact_person", entryParams.get("contact_person").toString());
        }

        params.put("updated_at", Timestamp.valueOf(LocalDateTime.now()));

        if(entryParams.get("published_at") != null) {
            params.put("published_at", Timestamp.valueOf(LocalDateTime.now()));
        }

        if(entryParams.get("api_password") != null){
            params.put("api_password", entryParams.get("api_password").toString());
        }

        if(entryParams.get("api_key") != null){
            params.put("api_key", entryParams.get("api_key").toString());
        }

        params.put("updated_by", Integer.parseInt(entryParams.getOrDefault("updated_by", "0").toString()));

        if(entryParams.get("status") != null){
            try {
                params.put("status", CompanyStatus.getCompanyStatusType(entryParams.get("status").toString()));
            } catch (Exception e) {
                e.printStackTrace();

                throw new RuntimeException(e);
            }
        }

        if(entryParams.get("verified") != null){
            params.put("verified", Boolean.parseBoolean(entryParams.getOrDefault("verified", false).toString()));
        }

        params.put("updated_at", Timestamp.valueOf(LocalDateTime.now()));

        params = cleanMap(params);

        LinkedHashMap<String, Object> where_params = new LinkedHashMap<>();

        String sql;
        String table = "properties";

        if(entryParams.get("id") != null) {
            where_params.put("id", Integer.parseInt(entryParams.get("id").toString()));
        }else{
            return null;
        }

        sql = Models.UpdateString(table, params, where_params);

        sql += " returning *";

        List<Map<String, Object>> results = NamedBaseExecute(sql, params, where_params, new MapResultHandler());

        if(results.size() > 0){
            return results;
        }

        return null;
    }
    // </editor-fold>
    //
    // <editor-fold default-state="collapsed" desc="listAllCompanies(Map<String, Object> queryParams) ">
    @Override
    public List<Map<String, Object>> listAllCompanies(Map<String, Object> queryParams) {
        LinkedHashMap<String, Object> where_param = new LinkedHashMap<>();

        String sql = "SELECT * FROM companies WHERE LOWER(status)=LOWER(:status) ORDER BY id, created_at ASC LIMIT :limit OFFSET :offset";

        int limit = 50;

        where_param.put("status", UserStatus.valueOf(queryParams.getOrDefault("status", "ACTIVE").toString()));
        where_param.put("limit", limit);

        if(queryParams.get("page_number") == null){
            where_param.put("offset", 0);
        }else{
            where_param.put("offset", (Integer.parseInt(queryParams.getOrDefault("page_number", "0").toString()) - 1)* limit);
        }

        List<Map<String, Object>> property = NamedBaseExecute(sql, null, where_param, new MapResultHandler());

        return property.size() > 0 ? property : null;
    }
    // </editor-fold>
    //

    /*-------------------- PERMISSIONS -------------------------*/

    // <editor-fold default-state="collapsed" desc="searchPermissionById(String id)">
    @Override
    public Map<String, Object> searchPermissionById(String id) {
        LinkedHashMap<String, Object> param = new LinkedHashMap<>();
        String sql = "SELECT * FROM permissions WHERE id=:id LIMIT 1";

        param.put("id", Integer.parseInt(id));

        List<Map<String, Object>> properties = NamedBaseExecute(sql, param, null, new MapResultHandler());

        return properties.size() > 0 ? properties.get(0) : null;
    }
    // </editor-fold>
    //
    // <editor-fold default-state="collapsed" desc="deletePermission(String id)">
    @Override
    public Map<String, Object> deletePermission(String id) {
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();

        params.put("updated_at", Timestamp.valueOf(LocalDateTime.now()));
        params.put("deleted_at", Timestamp.valueOf(LocalDateTime.now()));

        try {
            if(params.get("status") != null){
                params.put("status", Role_PermissionStatus.getRole_PermissionStatusType(params.get("status").toString()));
            }else{
                params.put("status", Role_PermissionStatus.getRole_PermissionStatusType("delete"));
            }
        } catch (Exception e) {
            e.printStackTrace();

            throw new RuntimeException(e);
        }

        LinkedHashMap<String, Object> where_params = new LinkedHashMap<>();
        where_params.put("id", Integer.parseInt(id));

        String sql;
        String table = "permissions";

        where_params.put("id", Integer.parseInt(id));

        sql = Models.UpdateString(table, params, where_params);

        sql += " returning *";

        List<Map<String, Object>> results = NamedBaseExecute(sql, params, where_params, new MapResultHandler());

        if(results.size() > 0){
            return results.get(0);
        }

        return null;
    }
    // </editor-fold>
    //
    // <editor-fold default-state="collapsed" desc="createPermission(Map<String, Object> entryParams)">
    @Override
    public List<Map<String, Object>> createPermission(Map<String, Object> entryParams) {
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();

        params.put("action", entryParams.get("action").toString().toUpperCase());
        if (entryParams.get("description") != null) {
            params.put("description", entryParams.get("description").toString());
        } else {
            params.put("description", entryParams.get("action").toString().toLowerCase());
        }
        params.put("created_at", Timestamp.valueOf(LocalDateTime.now()));
        params.put("updated_at", Timestamp.valueOf(LocalDateTime.now()));
        params.put("created_by", entryParams.getOrDefault("created_by", "0").toString());
        params.put("updated_by", entryParams.getOrDefault("updated_by", "0").toString());

        params = cleanMap(params);

        String sql;
        String table = "permissions";

        sql = Models.InsertString(table, params);
        sql += " returning *";

        List<Map<String, Object>> results = NamedBaseExecute(sql, params, null, new MapResultHandler());

        if(results.size() > 0){
            return results;
        }

        return null;
    }
    // </editor-fold>
    //
    // <editor-fold default-state="collapsed" desc="updatePermission(Map<String, Object> entryParams)">
    @Override
    public List<Map<String, Object>> updatePermission(Map<String, Object> entryParams) {
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();

        if(entryParams.get("description") != null) {
            params.put("description", entryParams.get("description").toString());
        }

        params.put("updated_at", Timestamp.valueOf(LocalDateTime.now()));
        params.put("updated_by", entryParams.getOrDefault("updated_by", "0").toString());

        params = cleanMap(params);

        LinkedHashMap<String, Object> where_params = new LinkedHashMap<>();

        String sql;
        String table = "permissions";

        if(entryParams.get("id") != null) {
            where_params.put("id", Integer.parseInt(entryParams.get("id").toString()));
        }else{
            return null;
        }

        sql = Models.UpdateString(table, params, where_params);

        sql += " returning *";

        List<Map<String, Object>> results = NamedBaseExecute(sql, params, where_params, new MapResultHandler());

        if(results.size() > 0){
            return results;
        }

        return null;
    }
    // </editor-fold>
    //
    // <editor-fold default-state="collapsed" desc="listAllPermissions(Map<String, Object> queryParams) ">
    @Override
    public List<Map<String, Object>> listAllPermissions(Map<String, Object> queryParams) {
        LinkedHashMap<String, Object> param = new LinkedHashMap<>();

        String limit = "50";
        String sql = "SELECT * FROM permissions WHERE LOWER(status)=LOWER(:status) ORDER BY id, created_at ASC LIMIT " + limit + " OFFSET :offset";

        try {
//            param.put("limit", limit);
            param.put("status", Role_PermissionStatus.getRole_PermissionStatusType(queryParams.getOrDefault("status", Role_PermissionStatus.ACTIVE.name()).toString()));

            if(queryParams.get("page_number") == null){
                param.put("offset", 0);
            }else{
                param.put("offset", (Integer.parseInt(queryParams.getOrDefault("page_number", "0").toString()) - 1) * Integer.parseInt(limit));
            }

            log.error("param: " + param);

            List<Map<String, Object>> property = NamedBaseExecute(sql, null, param, new MapResultHandler());

            return property.size() > 0 ? property : null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    // </editor-fold>
    //

    /*-------------------- ROLES -------------------------*/

    // <editor-fold default-state="collapsed" desc="searchRoleById(String id)">
    @Override
    public Map<String, Object> searchRoleById(String id) {
        LinkedHashMap<String, Object> param = new LinkedHashMap<>();
        String sql = "SELECT * FROM roles WHERE id=:id LIMIT 1";

        param.put("id", Integer.parseInt(id));

        List<Map<String, Object>> properties = NamedBaseExecute(sql, param, null, new MapResultHandler());

        return properties.size() > 0 ? properties.get(0) : null;
    }
    // </editor-fold>
    //
    // <editor-fold default-state="collapsed" desc="deleteRole(String id)">
    @Override
    public Map<String, Object> deleteRole(String id) {
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();

        params.put("updated_at", Timestamp.valueOf(LocalDateTime.now()));
        params.put("deleted_at", Timestamp.valueOf(LocalDateTime.now()));

        try {
            if(params.get("status") != null){
                params.put("status", Role_PermissionStatus.getRole_PermissionStatusType(params.get("status").toString()));
            }else{
                params.put("status", Role_PermissionStatus.getRole_PermissionStatusType("delete"));
            }
        } catch (Exception e) {
            e.printStackTrace();

            throw new RuntimeException(e);
        }

        LinkedHashMap<String, Object> where_params = new LinkedHashMap<>();
        where_params.put("id", Integer.parseInt(id));

        String sql;
        String table = "roles";

        where_params.put("id", Integer.parseInt(id));

        sql = Models.UpdateString(table, params, where_params);

        sql += " returning *";

        List<Map<String, Object>> results = NamedBaseExecute(sql, params, where_params, new MapResultHandler());

        if(results.size() > 0){
            return results.get(0);
        }

        return null;
    }
    // </editor-fold>
    //
    // <editor-fold default-state="collapsed" desc="createRole(Map<String, Object> entryParams)">
    @Override
    public Map<String, Object> createRole(Map<String, Object> entryParams) {
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();
        String roleIdNum;

        params.put("name", entryParams.get("name").toString().toUpperCase());

        if(entryParams.get("description") != null){
            params.put("description", entryParams.get("description").toString());
        }else {
            params.put("description", entryParams.get("name").toString().toLowerCase());
        }

        if(entryParams.get("role_id") != null){
            roleIdNum = entryParams.get("role_id").toString();

            params.put("role_id", roleIdNum);
        }else {
            roleIdNum = UUID.randomUUID().toString();

            params.put("role_id", roleIdNum);
        }

        if(entryParams.get("type") != null){
            params.put("type", entryParams.get("type").toString());
        }else {
            params.put("type", null);
        }

        params.put("created_at", Timestamp.valueOf(LocalDateTime.now()));
        params.put("updated_at", Timestamp.valueOf(LocalDateTime.now()));
        params.put("created_by", entryParams.getOrDefault("created_by", "0").toString());
        params.put("updated_by", entryParams.getOrDefault("updated_by", "0").toString());

        List<Object> permissionList = (List) entryParams.get("permissions");

        try {
            numOfOperations = 0;

            params = cleanMap(params);

            String sql;
            String table = "roles";

            sql = Models.InsertString(table, params);
            sql += " returning *";

            List<Map<String, Object>> results = NamedBaseExecute(sql, params, null, new MapResultHandler());

            if(results.size() > 0 && permissionList.size() > 0){
                while(numOfOperations < permissionList.size()) {
                    permissionList.forEach(permission -> {
                        String sql2;
                        String table2 = "permissions_role_links";

                        LinkedHashMap<String, Object> params2 = new LinkedHashMap<>();

                        params2.put("permission_id", Integer.parseInt(String.valueOf(permission)));
                        params2.put("role_id", roleIdNum);

                        sql2 = Models.InsertString(table2, params2);
                        sql2 += " returning *";

                        List<Map<String, Object>> results2 = NamedBaseExecute(sql2, params2, null, new MapResultHandler());

                        if (results2.size() > 0) {
                            numOfOperations++;
                        }
                    });
                }

                if (numOfOperations == permissionList.size()) {
                    return results.get(0);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return null;
    }
    // </editor-fold>
    //
    // <editor-fold default-state="collapsed" desc="findAllPermissionsAssignedToRole(String roleId))">
    public List<Object> findAllPermissionsAssignedToRole(String roleId){
        LinkedHashMap<String, Object> param = new LinkedHashMap<>();
        String sql = "SELECT permission_id FROM permissions_role_links WHERE role_id=:role_id ORDER BY id";

        param.put("role_id", roleId);

        List<Map<String, Object>> permissions = NamedBaseExecute(sql, param, null, new MapResultHandler());
        List<String> permissionsArr = permissions.stream().map(s -> s.get("permission_id").toString()).toList();

        return permissions.size() > 0 ? (List) permissionsArr : null;
    }
    // </editor-fold>
    //
    // <editor-fold default-state="collapsed" desc="updateRole(Map<String, Object> entryParams)">
    public int updatePermissions(Map<String, Object> entryParams) {
        numOfOperations = 0;
        List<Object> permissionList = (List) entryParams.get("permissions");
        List<Object> allAssignedPermissions = findAllPermissionsAssignedToRole(entryParams.get("role_id").toString());

        List<Object> orderedPermissionList =  permissionList.stream().sorted().toList();
        List<Object> allAssignedPermissionsOrdered = allAssignedPermissions.stream().sorted().toList(); // Arrays.asList("1", "2", "3", "4");
        List<Object> unlikeElements = null;

        try {
            if(!orderedPermissionList.equals(allAssignedPermissionsOrdered)){
                if(orderedPermissionList.size() > allAssignedPermissionsOrdered.size()){
                    // new permissions to add to role
                    addNewPermission = true;

                    unlikeElements = ConvertTo.itemsNotPresentInList(orderedPermissionList, allAssignedPermissionsOrdered);
                }else{
                    // permission to be removed from the role
                    stripePermission = true;

                    unlikeElements = ConvertTo.itemsNotPresentInList(allAssignedPermissionsOrdered, orderedPermissionList);
                }
            }

            // TODO: Bug resulting from Jodd using executeQuery which sometimes does not return results as expected - can be replaced with executeUpdate in place

            if (unlikeElements != null && unlikeElements.size() > 0) {
                // Update Role-Permission tbl
                while (numOfOperations < unlikeElements.size()) {
                    unlikeElements.forEach(permission -> {
                        String rolePermissionLink_sql = "";
                        String table2 = "permissions_role_links";

                        LinkedHashMap<String, Object> rolePermissionLink_params = new LinkedHashMap<>();

                        if (addNewPermission && !stripePermission) {
                            rolePermissionLink_params.put("permission_id", Integer.parseInt(String.valueOf(permission)));
                            rolePermissionLink_params.put("role_id", entryParams.get("role_id").toString());

                            rolePermissionLink_sql = Models.InsertString(table2, rolePermissionLink_params);
                            rolePermissionLink_sql += " returning *";
//                            rolePermissionLink_sql = "INSERT INTO permissions_role_links(permission_id, role_id) VALUES (" + Integer.parseInt(permission.toString()) +
//                                    ", '" + entryParams.get("role_id").toString() + "') returning *;";
                        }

                        if(stripePermission && !addNewPermission){
                            rolePermissionLink_params.put("permission_id", Integer.parseInt(permission.toString()));
                            rolePermissionLink_params.put("role_id", entryParams.get("role_id").toString());

                            rolePermissionLink_sql = "DELETE FROM permissions_role_links WHERE permission_id=:permission_id AND role_id=:role_id returning *";
//                            rolePermissionLink_sql = "DELETE FROM permissions_role_links WHERE permission_id=" + Integer.parseInt(permission.toString()) +
//                                    " AND role_id='" + entryParams.get("role_id").toString() + "' returning *;";
                        }

                        List<Map<String, Object>> rolePermissionLinkResult = NamedBaseExecute(rolePermissionLink_sql, null, rolePermissionLink_params, new MapResultHandler());

                        if (rolePermissionLinkResult != null && rolePermissionLinkResult.size() > 0) {
                            numOfOperations++;
                        }
                    });
                }

                if((numOfOperations + 1) > unlikeElements.size()){
                    return numOfOperations;
                }
            }else{
                return 1;
            }
        } catch (Exception e) {
            e.printStackTrace();

            return -1;
        }

        return numOfOperations;
    }

    @Override
    public Map<String, Object> updateRole(Map<String, Object> entryParams){
        int updatePermissionsStatus = updatePermissions(entryParams);

        log.error("updatePermissionsStatus: " + updatePermissionsStatus);

        if (updatePermissionsStatus > 0) {
            // Update Role tbl
            String rolePermissionLinkUpdate_sql;
            String table = "roles";
            LinkedHashMap<String, Object> roleParams = new LinkedHashMap<>();

            if(entryParams.get("description") != null) {
                roleParams.put("description", entryParams.get("description").toString());
            }

            if(entryParams.get("type") != null) {
                roleParams.put("type", entryParams.get("type").toString());
            }

            roleParams.put("updated_at", Timestamp.valueOf(LocalDateTime.now()));
            roleParams.put("updated_by", entryParams.getOrDefault("updated_by", "0").toString());

            roleParams = cleanMap(roleParams);

            LinkedHashMap<String, Object> where_params = new LinkedHashMap<>();

            if(entryParams.get("id") != null) {
                where_params.put("id", Integer.parseInt(entryParams.get("id").toString()));
            }else{
                return null;
            }

            rolePermissionLinkUpdate_sql = Models.UpdateString(table, roleParams, where_params);
            rolePermissionLinkUpdate_sql += " returning *";

            List<Map<String, Object>> roleUpdateResult = NamedBaseExecute(rolePermissionLinkUpdate_sql, roleParams, where_params, new MapResultHandler());

            return roleUpdateResult.size() > 0 ? roleUpdateResult.get(0) : null;
        } else {
            return null;
        }
    }

    // </editor-fold>
    //
    // <editor-fold default-state="collapsed" desc="listAllRoles(Map<String, Object> queryParams) ">
    @Override
    public List<Map<String, Object>> listAllRoles(Map<String, Object> queryParams) {
        LinkedHashMap<String, Object> param = new LinkedHashMap<>();

        String limit = "50";

        String sql = "SELECT * FROM roles WHERE LOWER(status)=LOWER(:status) ORDER BY id ASC LIMIT " + limit + " OFFSET :offset";

        try {
//            param.put("limit", limit);
            param.put("status", Role_PermissionStatus.getRole_PermissionStatusType(queryParams.getOrDefault("status", Role_PermissionStatus.ACTIVE.name()).toString()));

            if(queryParams.get("page_number") == null){
                param.put("offset", 0);
            }else{
                param.put("offset", (Integer.parseInt(queryParams.getOrDefault("page_number", "0").toString()) - 1) * Integer.parseInt(limit));
            }

            log.error("param: " + param);

            List<Map<String, Object>> property = NamedBaseExecute(sql, null, param, new MapResultHandler());

            return property.size() > 0 ? property : null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    // </editor-fold>
    //
}