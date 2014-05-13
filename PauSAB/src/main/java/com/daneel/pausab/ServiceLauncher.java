package com.daneel.pausab;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by daneel on 2014/04/14.
 */
public class ServiceLauncher extends Activity {

    private static final int SHOW_PREFERENCES = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().hasExtra("Action")) {
            Bundle extras = getIntent().getExtras();

            if (extras != null && extras.getString("Action") != null && "Start".equals(extras.getString("Action"))) {
                Intent serviceIntent = new Intent(this, PersistentAgent.class);
                serviceIntent.putExtra("Action", "Start");
                startService(serviceIntent);
            } else if (extras != null && extras.getString("Action") != null && "Stop".equals(extras.getString("Action"))) {
                stopService(new Intent(this, PersistentAgent.class));
            } else if (extras != null && extras.getString("Action") != null &&  "Preferences".equals(extras.getString("Action"))) {
                Class c = Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB ? SettingsActivity.class : FragmentSettings.class;
                Intent i = new Intent(this, c);
                startActivityForResult(i, SHOW_PREFERENCES);
            }
        }
        else{
            try {
                ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
                Bundle bundle = ai.metaData;
                String myApiKey = bundle.getString("Action");
                if (myApiKey.equals("Preferences")) {
                    Class c = Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB ? SettingsActivity.class : FragmentSettings.class;
                    Intent i = new Intent(this, c);
                    startActivityForResult(i, SHOW_PREFERENCES);
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.e("Startup", "Failed to load meta-data, NameNotFound: " + e.getMessage());
            } catch (NullPointerException e) {
                Log.e("Startup", "Failed to load meta-data, NullPointer: " + e.getMessage());
            }
        }
        finish();
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        Class c = Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB ? SettingsActivity.class : FragmentSettings.class;
//        Intent i = new Intent(this, c);
//        startActivityForResult(i, SHOW_PREFERENCES);
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
