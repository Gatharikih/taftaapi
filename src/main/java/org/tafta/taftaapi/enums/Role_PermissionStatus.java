package org.tafta.taftaapi.enums;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Gathariki Ngigi
 * Created on May 20, 2023.
 * Time 1634h
 */

@Slf4j
public enum Role_PermissionStatus {
    ACTIVE, INACTIVE, DELETED, SUSPENDED;

    public static String getRole_PermissionStatusType(String type) throws Exception {
        String statusType;

        if(type.equalsIgnoreCase("active")){
            statusType = Role_PermissionStatus.ACTIVE.name();
        }else if(type.equalsIgnoreCase("in-active") || type.equalsIgnoreCase("inactive")){
            statusType = Role_PermissionStatus.INACTIVE.name();
        }else if(type.equalsIgnoreCase("deleted") || type.equalsIgnoreCase("delete")){
            statusType = Role_PermissionStatus.DELETED.name();
        }else{
            throw new Exception("Unrecognized status");
        }

        return statusType;
    }
}