package org.tafta.taftaapi.repo;

import com.fasterxml.jackson.databind.ObjectMapper;
import jodd.db.DbOom;
import jodd.db.DbQuery;
import jodd.props.Props;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tafta.taftaapi.enums.UserStatus;

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

    //
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
                params.put("status", Optional.of(UserStatus.getUserStatusType(entryParams.get("status").toString())).orElse(UserStatus.getUserStatusType("active")));
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

        params.put("status", "deleted");
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
    // <editor-fold default-state="collapsed" desc="searchProperties(String searchTerm)">
    @Override
    public List<Map<String, Object>> searchProperties(String searchTerm) {
        LinkedHashMap<String, Object> param = new LinkedHashMap<>();
        String sql = "SELECT * FROM properties WHERE property_name=:property_name OR county=:county OR name=:name LIMIT 1 ORDER BY id, created_at ASC";

        param.put("property_name", searchTerm);
        param.put("county", searchTerm);
        param.put("name", searchTerm);

        List<Map<String, Object>> properties = NamedBaseExecute(sql, param, null, new MapResultHandler());

        return properties.size() > 0 ? properties : null;
    }
    // </editor-fold>
    //
    // <editor-fold default-state="collapsed" desc="deleteProperty(String id)">
    @Override
    public Map<String, Object> deleteProperty(String id) {
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();

        params.put("status", "deleted");
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
    // <editor-fold default-state="collapsed" desc="List<Map<String, Object>>listAllProperties(String pageNumber) ">
    @Override
    public List<Map<String, Object>> listAllProperties(Map<String, Object> queryParams) {
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();
        LinkedHashMap<String, Object> where_param = new LinkedHashMap<>();
        String sql = "SELECT * FROM properties WHERE status=:status ORDER BY id, created_at ASC LIMIT :limit OFFSET :offset";

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
}