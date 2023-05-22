package org.tafta.taftaapi.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * @author Joseph Kibe
 * Created on April 17, 2023.
 * Time 8:37 AM
 */

@Slf4j
public class ConvertTo {

    public static JsonNode jsonNode(Object objectToConvert){
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readTree(mapper.writeValueAsString(objectToConvert));

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public  static  JsonNode  jsonNodeFromStr(String data){
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readTree(data);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String jsonString(Object objectToConvert){
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(objectToConvert);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T objectOfType(Object objectToConvert){
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(objectToConvert);
            return (T) mapper.convertValue(json, objectToConvert.getClass());

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }

    }

    /** Get the elements that are not similar
     * @param greaterList
     * @param smallerList
     * @return ArrayList - Elements that are not contained in both lists
     * */
    public static List<Object> itemsNotPresentInList(List<Object> greaterList, List<Object> smallerList){
        List<Object> unlikeElements = new ArrayList<>();
        int count = 0;

        List<Object> greaterList_sorted  = greaterList.stream().sorted().toList();
        List<Object> smallerList_sorted = smallerList.stream().sorted().toList();

        while (count < greaterList_sorted.size()) {
            for (int i = 0; i < greaterList_sorted.size(); i++) {
                if ((i + 1) <= smallerList_sorted.size() ) {
                    if(!greaterList_sorted.get(i).equals(smallerList_sorted.get(i))){
                        unlikeElements.add(greaterList_sorted.get(i));
                    }
                } else {
                    unlikeElements.add(greaterList_sorted.get(i));
                }

                count = count + 1;
            }
        }

        return unlikeElements;
    }
}
