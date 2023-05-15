package org.tafta.taftaapi.enums;

/**
 * @author Gathariki Ngigi
 * Created on May 15, 2023.
 * Time 1709h
 */
public enum AuthChannel {
    GOOGLE, TWITTER, USERNAME_PASSWORD, FACEBOOK;

    public static String getAuthChannel(String type) throws Exception {
        String authChannel;

        if(type.equalsIgnoreCase("google")){
            authChannel = AuthChannel.GOOGLE.name();
        }else if(type.equalsIgnoreCase("twitter")){
            authChannel = AuthChannel.TWITTER.name();
        }else if(type.equalsIgnoreCase("username_password")){
            authChannel = AuthChannel.USERNAME_PASSWORD.name();
        }else if(type.equalsIgnoreCase("facebook")){
            authChannel = AuthChannel.FACEBOOK.name();
        }else{
            throw new Exception("Unrecognized channel");
        }

        return authChannel;
    }
}
