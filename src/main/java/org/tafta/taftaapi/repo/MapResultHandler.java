package org.tafta.taftaapi.repo;

import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.*;

/**
 * @author Gathariki Ngigi
 * Created on April 25, 2023.
 * Time 1518h
 */
@Slf4j
public class MapResultHandler implements DBFunctionImpl.ResultHandler<List<Map<String, Object>>> {
    @Override
    public List<Map<String, Object>> handle(ResultSet rs) {
        List<Map<String, Object>> requests = new ArrayList<>();

        try {
            Vector<String> columnNames = new Vector<String>();
            if (rs != null) {
                ResultSetMetaData columns = rs.getMetaData();
                int i = 0;

                while (i < columns.getColumnCount()) {
                    i++;
                    columnNames.add(columns.getColumnName(i));
                }

                while (rs.next()) {
                    Map<String,Object> request = new LinkedHashMap<>();

                    for (i = 0; i < columnNames.size(); i++) {
                        request.put(columnNames.get(i),rs.getObject(columnNames.get(i)));
                    }

                    requests.add(request);
                }
            }
        }catch (Exception e){
           log.error(e.getMessage());
        }

        return requests;
    }
}