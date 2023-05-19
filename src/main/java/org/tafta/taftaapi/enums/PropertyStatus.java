package org.tafta.taftaapi.enums;

/**
 * @author Gathariki Ngigi
 * Created on May 19, 2023.
 * Time 0840h
 */
public enum PropertyStatus {
    ACTIVE, INACTIVE, DELETED, SUSPENDED;

    public static String getPropertyStatusType(String type) throws Exception {
        String statusType;

        if(type.equalsIgnoreCase("active")){
            statusType = PropertyStatus.ACTIVE.name();
        }else if(type.equalsIgnoreCase("in-active") || type.equalsIgnoreCase("inactive")){
            statusType = PropertyStatus.INACTIVE.name();
        }else if(type.equalsIgnoreCase("deleted")){
            statusType = PropertyStatus.DELETED.name();
        }else if(type.equalsIgnoreCase("suspended")){
            statusType = PropertyStatus.SUSPENDED.name();
        }else{
            throw new Exception("Unrecognized status");
        }

        return statusType;
    }
}