package com.daneel.pausab;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by daneel on 2014/01/30.
 */
public final class PreferencesStore {

    private static PreferencesStore mInstance = null;

    private String SERVER_IP;
    private String SERVER_PORT;
    private String API_KEY;
    private int DURATION1;
    private int DURATION2;
    private int DURATION3;
    private int UPDATECOUNT;

    private PreferencesStore(Context context){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);

        this.SERVER_IP = settings.getString(FragmentSettings.KEY_PREF_SERVER_IP, "");
        this.SERVER_PORT = settings.getString(FragmentSettings.KEY_PREF_SERVER_PORT, "8080");
        this.API_KEY = settings.getString(FragmentSettings.KEY_PREF_API_KEY, "");
        this.DURATION1 = Integer.decode(settings.getString(FragmentSettings.KEY_PREF_PAUSE_DURATION1, "5"));
        this.DURATION2 = Integer.decode(settings.getString(FragmentSettings.KEY_PREF_PAUSE_DURATION2, "15"));
        this.DURATION3 = Integer.decode(settings.getString(FragmentSettings.KEY_PREF_PAUSE_DURATION3, "30"));
        this.UPDATECOUNT = 0;
    }


    public static PreferencesStore getInstance(Context context) {
        if (mInstance == null){
            mInstance = new PreferencesStore(context);
        }
        return mInstance;
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

    public String getPauseURL(String duration){
        return "http://"+
                SERVER_IP+
                ":"+
                SERVER_PORT
                +"/api?mode=config&name=set_pause&value="+
                duration+
                "&apikey="+
                API_KEY;
    }

    public String getStatusURL(){
        // http://192.168.1.7:8080/api?mode=qstatus&amp;amp;output=xml&amp;amp;apikey=6e578e0f2667a0977a72d04c3a340950
        return "http://" +
                SERVER_IP +
                ":" +
                SERVER_PORT +
                "/api?mode=qstatus&output=xml&apikey=" +
                API_KEY;
    }

    public int getRefreshIntervalMinutes(){
        return 10;
    }

    public int getRefreshIntervalSeconds(){
        return 20;
    }

    public void incUpdateCount() {
        this.UPDATECOUNT++;
    }

    public int getUpdateCount() {
        return UPDATECOUNT;
    }

}
