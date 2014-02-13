package com.daneel.pausab;

/**
 * Created by daneel on 2014/01/30.
 */
public class PreferencesStore {

    public PreferencesStore() {
        this.SERVER_IP = "192.168.1.7";
        this.SERVER_PORT = "8080";
        this.API_KEY = "6e578e0f2667a0977a72d04c3a340950";
        this.DURATION1 = 5;
        this.DURATION2 = 15;
        this.DURATION3 = 30;
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

    public int getDuration1(){ return DURATION1; }
    public int getDuration2(){ return DURATION2; }
    public int getDuration3(){ return DURATION3; }

    private String SERVER_IP;
    private String SERVER_PORT;
    private String API_KEY;
    private int DURATION1;
    private int DURATION2;
    private int DURATION3;
}
