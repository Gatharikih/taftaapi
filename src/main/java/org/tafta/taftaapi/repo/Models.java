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
        List<String> unsetkeys = new ArrayList<>();

        if (collection.size() > 0) {
            String keys = "";
            String values = "";
            for (Map.Entry<String, Object> pair : collection.entrySet()) {

                keys += pair.getKey() + ",";
                values += ":" + pair.getKey() + ",";
            }

            keys = keys.substring(0, keys.length() - 1);
            values = values.substring(0, values.length() - 1);

            sql = String.format("insert into %s (%s) values(%s)", table, keys, values);
        }

        for (String key : unsetkeys) {
            collection.remove(key);
        }

        return sql;
    }

    public static String UpdateString(String table, Map<String, Object> collection, Map<String, Object> wherecollection) {
        String sql = "";
        List<String> unsetkeys = new ArrayList<>();

        if (collection.size() > 0) {
            String keys;
            String values;
            sql = String.format("update %s set ", table);
            for (Map.Entry<String, Object> pair : collection.entrySet()) {

                if (pair.getValue() == null) {
                    unsetkeys.add(pair.getKey());
                    continue;
                }

                keys = pair.getKey() + "=";
                values = ":" + pair.getKey() + ",";

                sql += keys + values;
            }

            sql = sql.substring(0, sql.length() - 1);

            sql += " where ";

            for (Map.Entry<String, Object> pair : wherecollection.entrySet()) {
                keys = pair.getKey().replaceAll("_1", "") + "=";
                values = ":" + pair.getKey() + " and ";

                sql += keys + values;
            }

            sql = sql.substring(0, sql.length() - 5);
        }

        for (String key : unsetkeys) {
            collection.remove(key);
        }

        return sql;
    }

    public static String Likes(Map<String, Object> filterscollection) {
        String sql = " ";
        String keys;
        String values;
        for (Map.Entry<String, Object> pair : filterscollection.entrySet()) {

            int point = pair.getKey().indexOf("_1");

            String key = point > 0 ? pair.getKey().substring(0, point) : pair.getKey();

            keys = key + " ilike ";
            values = ":" + key + " or ";

            sql += keys + values;
        }

        sql = sql.substring(0, sql.length() - 4);

        return sql;
    }

    public static String Equals(Map<String, Object> filterscollection) {
        return Equals(filterscollection,"and","=");
    }

    public static String Equals(Map<String, Object> filterscollection,String joiner) {
        return Equals(filterscollection,joiner,"=");
    }

    public static String Equals(Map<String, Object> filterscollection, String joiner, String comparator) {
        String sql = " ";
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

        for (Map.Entry<String, Object> pair : filterscollection.entrySet()) {

            Matcher matcher = pattern.matcher(pair.getKey());

            int point = 0;

            if(matcher.find()){
                point = matcher.start();//this will give you index
            }

            //int point = pair.getKey().indexOf("_1");

            String key = point > 0 ? pair.getKey().substring(0, point) : pair.getKey();

            keys = key + " " + comparator;
            values = ":" + pair.getKey() + " " + joiner;

            sql += keys + values;
        }

        //logger.trace(sql);

        sql = sql.substring(0, sql.length() - (joiner.length() + 1));

        return sql;
    }
}