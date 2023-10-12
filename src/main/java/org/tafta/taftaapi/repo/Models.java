package org.tafta.taftaapi.repo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Gathariki Ngigi
 * Created on April 27, 2023.
 * Time 0959h
 */
public class Models {
    public static String InsertString(String table, Map<String, Object> collection) {
        String sql = "";

        if (!collection.isEmpty()) {
            StringBuilder keys = new StringBuilder(), values = new StringBuilder();

            for (Map.Entry<String, Object> pair : collection.entrySet()) {

                keys.append(pair.getKey()).append(",");
                values.append(":").append(pair.getKey()).append(",");
            }

            keys = new StringBuilder(keys.substring(0, keys.length() - 1));
            values = new StringBuilder(values.substring(0, values.length() - 1));

            sql = String.format("INSERT into %s (%s) VALUES(%s)", table, keys, values);
        }

        return sql;
    }

    public static String UpdateString(String table, Map<String, Object> collection,
                                      Map<String, Object> whereCollection) {
        StringBuilder sql = new StringBuilder();
        List<String> unsetKeys = new ArrayList<>();

        if (!collection.isEmpty()) {
            String keys, values;
            sql = new StringBuilder(String.format("UPDATE %s SET ", table));

            for (Map.Entry<String, Object> pair : collection.entrySet()) {
                if (pair.getValue() == null) {
                    unsetKeys.add(pair.getKey());

                    continue;
                }

                keys = pair.getKey() + "=";
                values = ":" + pair.getKey() + ",";

                sql.append(keys).append(values);
            }

            sql = new StringBuilder(sql.substring(0, sql.length() - 1));

            sql.append(" WHERE ");

            for (Map.Entry<String, Object> pair : whereCollection.entrySet()) {
                keys = pair.getKey().replaceAll("_1", "") + "=";
                values = ":" + pair.getKey() + " AND ";

                sql.append(keys).append(values);
            }

            sql = new StringBuilder(sql.substring(0, sql.length() - 5));
        }

        for (String key : unsetKeys) {
            collection.remove(key);
        }

        return sql.toString();
    }
}