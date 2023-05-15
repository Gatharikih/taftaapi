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
        List<String> unsetKeys = new ArrayList<>();

        if (collection.size() > 0) {
            StringBuilder keys = new StringBuilder();
            StringBuilder values = new StringBuilder();
            for (Map.Entry<String, Object> pair : collection.entrySet()) {

                keys.append(pair.getKey()).append(",");
                values.append(":").append(pair.getKey()).append(",");
            }

            keys = new StringBuilder(keys.substring(0, keys.length() - 1));
            values = new StringBuilder(values.substring(0, values.length() - 1));

            sql = String.format("insert into %s (%s) values(%s)", table, keys.toString(), values.toString());
        }

        for (String key : unsetKeys) {
            collection.remove(key);
        }

        return sql;
    }

    public static String UpdateString(String table, Map<String, Object> collection, Map<String, Object> wherecollection) {
        StringBuilder sql = new StringBuilder();
        List<String> unsetKeys = new ArrayList<>();

        if (collection.size() > 0) {
            String keys;
            String values;
            sql = new StringBuilder(String.format("update %s set ", table));

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

            sql.append(" where ");

            for (Map.Entry<String, Object> pair : wherecollection.entrySet()) {
                keys = pair.getKey().replaceAll("_1", "") + "=";
                values = ":" + pair.getKey() + " and ";

                sql.append(keys).append(values);
            }

            sql = new StringBuilder(sql.substring(0, sql.length() - 5));
        }

        for (String key : unsetKeys) {
            collection.remove(key);
        }

        return sql.toString();
    }

    public static String Likes(Map<String, Object> filtersCollection) {
        StringBuilder sql = new StringBuilder(" ");
        String keys;
        String values;

        for (Map.Entry<String, Object> pair : filtersCollection.entrySet()) {
            int point = pair.getKey().indexOf("_1");

            String key = point > 0 ? pair.getKey().substring(0, point) : pair.getKey();

            keys = key + " ilike ";
            values = ":" + key + " or ";

            sql.append(keys).append(values);
        }

        sql = new StringBuilder(sql.substring(0, sql.length() - 4));

        return sql.toString();
    }

    public static String Equals(Map<String, Object> filtersCollection) {
        return Equals(filtersCollection,"and","=");
    }

    public static String Equals(Map<String, Object> filtersCollection,String joiner) {
        return Equals(filtersCollection,joiner,"=");
    }

    public static String Equals(Map<String, Object> filtersCollection, String joiner, String comparator) {
        StringBuilder sql = new StringBuilder(" ");
        String keys;
        String values;

        if (joiner.isEmpty()) {
            joiner = "and";
        }

        if (comparator.isEmpty()) {
            comparator = "=";
        }

        joiner = " " + joiner + " ";

        String patternStr = "_[1-9]+";
        Pattern pattern = Pattern.compile(patternStr);

        for (Map.Entry<String, Object> pair : filtersCollection.entrySet()) {

            Matcher matcher = pattern.matcher(pair.getKey());

            int point = 0;

            if(matcher.find()){
                point = matcher.start();//this will give you index
            }

            //int point = pair.getKey().indexOf("_1");

            String key = point > 0 ? pair.getKey().substring(0, point) : pair.getKey();

            keys = key + " " + comparator;
            values = ":" + pair.getKey() + " " + joiner;

            sql.append(keys).append(values);
        }

        sql = new StringBuilder(sql.substring(0, sql.length() - (joiner.length() + 1)));

        return sql.toString();
    }
}