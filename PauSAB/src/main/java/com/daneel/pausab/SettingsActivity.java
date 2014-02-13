package com.daneel.pausab;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import java.util.List;

public class SettingsActivity extends PreferenceActivity {

    public static final String KEY_PREF_SERVER_IP       = "PREF_SERVER_IP";
    public static final String KEY_PREF_SERVER_PORT     = "PREF_SERVER_PORT";
    public static final String KEY_PREF_API_KEY         = "PREF_API_KEY";
    public static final String KEY_PREF_PAUSE_DURATION1 = "PREF_PAUSE_DURATION1";
    public static final String KEY_PREF_PAUSE_DURATION2 = "PREF_PAUSE_DURATION2";
    public static final String KEY_PREF_PAUSE_DURATION3 = "PREF_PAUSE_DURATION3";



    @Override
    public void onBuildHeaders(List<Header> target){
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    public static class SettingsActivityFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference_screen);

            Preference serverIPPref = findPreference(KEY_PREF_SERVER_IP);
            serverIPPref.setSummary("ABC123");
        }
        @Override
        public void onResume(){
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public  void onPause(){
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(KEY_PREF_SERVER_IP)) {
                Preference serverIPPref = findPreference(key);
                serverIPPref.setSummary(sharedPreferences.getString(key, ""));
            }
            else if (key.equals(KEY_PREF_SERVER_PORT)) {
                Preference serverIPPref = findPreference(key);
                serverIPPref.setSummary(sharedPreferences.getString(key, ""));
            }
            else if (key.equals(KEY_PREF_API_KEY)) {
                Preference serverIPPref = findPreference(key);
                serverIPPref.setSummary(sharedPreferences.getString(key, ""));
            }
            else if (key.equals(KEY_PREF_PAUSE_DURATION1)) {
                Preference serverIPPref = findPreference(key);
                serverIPPref.setSummary(sharedPreferences.getString(key, ""));
            }
            else if (key.equals(KEY_PREF_PAUSE_DURATION2)) {
                Preference serverIPPref = findPreference(key);
                serverIPPref.setSummary(sharedPreferences.getString(key, ""));
            }
            else if (key.equals(KEY_PREF_PAUSE_DURATION3)) {
                Preference serverIPPref = findPreference(key);
                serverIPPref.setSummary(sharedPreferences.getString(key, ""));
            }
        }
    }
}
