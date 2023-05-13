package org.tafta.taftaapi.utility;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.util.Date;

/**
 * @author Gathariki Ngigi
 * Created on March 07, 2023.
 * Time 10:42 AM
 */
@Slf4j
public class Dates {
    public Dates(){}

    public static Date getDateValue(Object dateVal) throws ParseException {
        try {
            String[] formats = {"ddMMyyyy", "dd-MM-yyyy", "MM/dd/yyyy", "yyMMdd", "yyyyMMdd"};

            if (dateVal instanceof String) {
                return DateUtils.parseDate(dateVal.toString(), formats);
            }

            if (dateVal instanceof Date) {
                return (Date) dateVal;
            }
        }catch (ParseException parseException){
            return null;
        }

        return null;
    }
}