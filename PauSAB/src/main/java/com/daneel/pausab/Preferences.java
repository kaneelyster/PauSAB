package com.daneel.pausab;

/**
 * Created by daneel on 2014/01/30.
 */
public class Preferences {

    public Preferences() {
        this.SERVER_IP = "192.168.1.7";
        this.SERVER_PORT = "8080";
        this.API_KEY = "6e578e0f2667a0977a72d04c3a340950";
    }

    public String getSERVER_IP() {
        return SERVER_IP;
    }

    public String getSERVER_PORT() {
        return SERVER_PORT;
    }

    public String getAPI_KEY() {
        return API_KEY;
    }

    private String SERVER_IP;
    private String SERVER_PORT;
    private String API_KEY;
}
