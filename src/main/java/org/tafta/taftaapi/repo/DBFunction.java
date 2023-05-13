package org.tafta.taftaapi.repo;

import java.util.List;
import java.util.Map;

/**
 * @author Gathariki Ngigi
 * Created on April 27, 2023.
 * Time 0906h
 */

public interface DBFunction {
    List<Map<String, Object>> createOrUpdateUser(Map<String, Object> entryParams);
}
