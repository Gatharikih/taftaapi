package org.tafta.taftaapi.enums;

/**
 * @author Gathariki Ngigi
 * Created on May 15, 2023.
 * Time 1709h
 */
public enum UserStatus {
    ACTIVE, INACTIVE, DELETED, SUSPENDED;

    public static String getUserStatusType(String type) throws Exception {
        String statusType;

        if(type.equalsIgnoreCase("active")){
            statusType = UserStatus.ACTIVE.name();
        }else if(type.equalsIgnoreCase("in-active") || type.equalsIgnoreCase("inactive")){
            statusType = UserStatus.INACTIVE.name();
        }else if(type.equalsIgnoreCase("deleted")){
            statusType = UserStatus.DELETED.name();
        }else if(type.equalsIgnoreCase("suspended")){
            statusType = UserStatus.SUSPENDED.name();
        }else{
            throw new Exception("Unrecognized status");
        }

        return statusType;
    }
}