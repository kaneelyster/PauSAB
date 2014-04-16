package com.daneel.pausab;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class FragmentSettings extends PreferenceActivity {

    public static final String KEY_PREF_SERVER_IP        = "PREF_SERVER_IP";
    public static final String KEY_PREF_SERVER_PORT      = "PREF_SERVER_PORT";
    public static final String KEY_PREF_SERVER_USER      = "PREF_SERVER_USER";
    public static final String KEY_PREF_SERVER_PASS      = "PREF_SERVER_PASS";
    public static final String KEY_PREF_API_KEY          = "PREF_API_KEY";
    public static final String KEY_PREF_PAUSE_DURATION1  = "PREF_PAUSE_DURATION1";
    public static final String KEY_PREF_PAUSE_DURATION2  = "PREF_PAUSE_DURATION2";
    public static final String KEY_PREF_PAUSE_DURATION3  = "PREF_PAUSE_DURATION3";
    public static final String KEY_PREF_REFRESH_INTERVAL = "PREF_REFRESH_INTERVAL";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsActivityFragment()).commit();
        PreferenceManager.setDefaultValues(this, R.xml.preference_screen, false);

    }

    public static class SettingsActivityFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference_screen);

            for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
                initSummary(getPreferenceScreen().getPreference(i));
            }
        }

        @Override
        public void onPause(){
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onResume(){
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(KEY_PREF_SERVER_IP)) {
                Preference PrefIP = findPreference(key);
                PrefIP.setSummary(sharedPreferences.getString(key, ""));

                Preference PrefGetAPIKey = findPreference(key);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(sharedPreferences.getString(KEY_PREF_SERVER_IP, "") + ":"+ sharedPreferences.getString(KEY_PREF_SERVER_PORT, "")+"/config/general/"));
                PrefGetAPIKey.setIntent(i);
            }
            else if (key.equals(KEY_PREF_SERVER_PORT)) {
                Preference Pref = findPreference(key);
                Pref.setSummary(sharedPreferences.getString(key, ""));
            }
            else if (key.equals(KEY_PREF_API_KEY)) {
                Preference Pref = findPreference(key);
                Pref.setSummary(sharedPreferences.getString(key, ""));
            }
            else if (key.equals(KEY_PREF_PAUSE_DURATION1)) {
                Preference Pref = findPreference(key);
                Pref.setSummary(sharedPreferences.getString(key, ""));
            }
            else if (key.equals(KEY_PREF_PAUSE_DURATION2)) {
                Preference Pref = findPreference(key);
                Pref.setSummary(sharedPreferences.getString(key, ""));
            }
            else if (key.equals(KEY_PREF_PAUSE_DURATION3)) {
                Preference Pref = findPreference(key);
                Pref.setSummary(sharedPreferences.getString(key, ""));
            }
            else if (key.equals(KEY_PREF_REFRESH_INTERVAL)) {
                Preference Pref = findPreference(key);
                Pref.setSummary(sharedPreferences.getString(key, ""));
            }
            try {
                PreferencesStore.getInstance().refreshSettings();
            }
            catch(Exception e){

            }
        }

        private void initSummary(Preference p) {
            if (p instanceof PreferenceCategory) {
                PreferenceCategory pCat = (PreferenceCategory) p;
                for (int i = 0; i < pCat.getPreferenceCount(); i++) {
                    if (!pCat.getPreference(i).getKey().equals(KEY_PREF_SERVER_PASS)){
                        initSummary(pCat.getPreference(i));
                    }
                }
            } else {
                updatePrefSummary(p);
            }
        }

        private void updatePrefSummary(Preference p) {
            if (p instanceof ListPreference) {
                ListPreference listPref = (ListPreference) p;
                p.setSummary(listPref.getEntry());
            }
            if (p instanceof EditTextPreference) {
                EditTextPreference editTextPref = (EditTextPreference) p;
                p.setSummary(editTextPref.getText());
            }
        }
    }
}
