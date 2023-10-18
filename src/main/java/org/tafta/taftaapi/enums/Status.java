package org.tafta.taftaapi.enums;

/**
 * @author Gathariki Ngigi
 * Created on May 15, 2023.
 * Time 1709h
 */
public enum Status {
    ACTIVE, INACTIVE, DELETED, SUSPENDED;

    public static String getStatusType(String type) {
        String statusType;

        if(type.equalsIgnoreCase("active")){
            statusType = Status.ACTIVE.name();
        }else if(type.equalsIgnoreCase("in-active") || type.equalsIgnoreCase("inactive")){
            statusType = Status.INACTIVE.name();
        }else if(type.equalsIgnoreCase("deleted") || type.equalsIgnoreCase("delete")){
            statusType = Status.DELETED.name();
        }else if(type.equalsIgnoreCase("suspended") || type.equalsIgnoreCase("suspend")){
            statusType = Status.SUSPENDED.name();
        }else{
            statusType = null;
        }

        return statusType;
    }
}