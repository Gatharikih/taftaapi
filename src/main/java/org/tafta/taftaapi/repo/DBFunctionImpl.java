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
    // <editor-fold default-state="collapsed" desc="createUser(Map<String, Object> entryParams)">
    @Override
    public List<Map<String, Object>> createUser(Map<String, Object> entryParams) {
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();

        params.put("company_id", entryParams.get("company_id"));
        params.put("role_id", Optional.ofNullable(entryParams.get("role_id"))
                .orElse(getUserRoleId("user").size() > 0 ? Integer.parseInt(getUserRoleId("user").get(0).get("id").toString()) : 5));
        params.put("fullname", entryParams.get("fullname"));
        params.put("email", entryParams.get("email"));
        params.put("password", entryParams.get("password"));
        params.put("msisdn", entryParams.get("msisdn"));
        params.put("status", Optional.ofNullable(entryParams.get("status")).orElse("active"));
        params.put("reset_password", Optional.ofNullable(entryParams.get("reset_password")).orElse(true));

        params = cleanMap(params);

        LinkedHashMap<String, Object> where_params = new LinkedHashMap<>();

        String sql;
        String table = "users";

        sql = Models.InsertString(table, params);
        sql += " returning *";

        List<Map<String, Object>> results = NamedBaseExecute(sql, params, where_params, new MapResultHandler());

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

        params.put("company_id", entryParams.get("company_id"));
        params.put("role_id", Optional.ofNullable(entryParams.get("role_id"))
                .orElse(getUserRoleId("user").size() > 0 ? Integer.parseInt(getUserRoleId("user").get(0).get("id").toString()) : 5));
        params.put("fullname", entryParams.get("fullname"));
//        params.put("email", entryParams.get("email"));
        params.put("password", entryParams.get("password"));
//        params.put("msisdn", entryParams.get("msisdn"));
        params.put("status", Optional.ofNullable(entryParams.get("status")).orElse("active"));
        params.put("reset_password", Optional.ofNullable(entryParams.get("reset_password")).orElse(true));
        params.put("created_at", Timestamp.valueOf(LocalDateTime.now()));
        params.put("updated_at", Timestamp.valueOf(LocalDateTime.now()));
        params.put("deleted_at", null);

        params = cleanMap(params);

        LinkedHashMap<String, Object> where_params = new LinkedHashMap<>();

        String sql;
        String table = "users";

        where_params.put("email", params.get("email"));
        where_params.put("msisdn", params.get("msisdn"));

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
    public Map<String, Object> searchUserByEmailOrPhoneNumber(String searchTerm) {
        LinkedHashMap<String, Object> param = new LinkedHashMap<>();
        String sql = "SELECT * FROM users WHERE email=:email OR msisdn=:msisdn LIMIT 1";

        param.put("email", searchTerm);
        param.put("msisdn", searchTerm);

        List<Map<String, Object>> user = NamedBaseExecute(sql, param, null, new MapResultHandler());

        return user.size() > 0 ? user.get(0) : null;
    }
    // </editor-fold>
    //
    // <editor-fold default-state="collapsed" desc="searchUserById(String id)">
    @Override
    public Map<String, Object> searchUserById(String id) {
        LinkedHashMap<String, Object> param = new LinkedHashMap<>();
        String sql = "SELECT * FROM users WHERE id=:id LIMIT 1";

        param.put("id", Integer.parseInt(id));

        List<Map<String, Object>> user = NamedBaseExecute(sql, param, null, new MapResultHandler());

        return user.size() > 0 ? user.get(0) : null;
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
}