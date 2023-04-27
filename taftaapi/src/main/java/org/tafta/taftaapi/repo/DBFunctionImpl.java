package org.sla.pegpayapi.repo;

import com.fasterxml.jackson.databind.ObjectMapper;
import jodd.db.DbOom;
import jodd.db.DbQuery;
import jodd.props.Props;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tafta.taftaapi.repo.DBFunction;
import org.tafta.taftaapi.repo.DbConnectionsHandler;

import javax.crypto.Mac;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

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
    protected String Resource = "";
    protected String ID = "";
    protected Map<String, Class<Object>> Columns;
    private static Props props = null;

    public enum LogType {ERRORS, APIREQUESTS, RESPONSES, MONEYTRANS, COOPCALLBACK}

    private static final DateTimeFormatter defaultTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss", Locale.ENGLISH);
    private final Map<String, Object> exceptions = new HashMap<>();

    public DBFunctionImpl() {
        mac = HmacUtils
                .getInitializedMac(HmacAlgorithms.HMAC_SHA_512, "3pA4KkUTeGydlxLwht1kIOweCEGOztJAjNKvXa5QPlXlCLnd5hj8KC19fUg3".getBytes(StandardCharsets.UTF_8));
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" NamedBaseExecute ">
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
                log.error("SQLEXCEPTION1  " + e);

                exceptions.put("errors", e.getCause().getMessage());
            }

            query.close();

            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            log.error("SQLEXCEPTION  " + e);
            exceptions.put("errors", e.getCause().getMessage());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    log.error("SQLEXCEPTION3  " + e);

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
    // <editor-fold defaultstate="collapsed" desc=" cleanMap ">
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
}