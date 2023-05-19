package org.tafta.taftaapi.enums;

/**
 * @author Gathariki Ngigi
 * Created on May 19, 2023.
 * Time 1521h
 */
public enum CompanyStatus {
    ACTIVE, INACTIVE, DELETED, SUSPENDED;

    public static String getCompanyStatusType(String type) throws Exception {
        String statusType;

        if(type.equalsIgnoreCase("active")){
            statusType = CompanyStatus.ACTIVE.name();
        }else if(type.equalsIgnoreCase("in-active") || type.equalsIgnoreCase("inactive")){
            statusType = CompanyStatus.INACTIVE.name();
        }else if(type.equalsIgnoreCase("deleted")){
            statusType = CompanyStatus.DELETED.name();
        }else if(type.equalsIgnoreCase("suspended")){
            statusType = CompanyStatus.SUSPENDED.name();
        }else{
            throw new Exception("Unrecognized status");
        }

        return statusType;
    }
}