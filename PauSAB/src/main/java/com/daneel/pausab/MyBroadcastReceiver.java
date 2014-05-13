package com.daneel.pausab;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by daneel on 2014/01/28.
 */
public class MyBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        //A pause button was pressed
        if (action != null && action.contains("com.daneel.pausab.DURATION")){
            Intent serviceIntent = new Intent(context, PersistentAgent.class);
            serviceIntent.putExtra("Pause", action);
            context.startService(serviceIntent);
        }
        //Force status refresh. Used in ongoing timer-based status checks
        else if (action != null && action.contains("Action")){
            Intent serviceIntent = new Intent(context, PersistentAgent.class);
            serviceIntent.putExtra("Action", "Start");
            context.startService(serviceIntent);
        }
        //Network state changed, so start or end periodic status checks
        else if (action != null && action.contains("android.net.wifi.WIFI_STATE_CHANGED")
              || (action != null && action.contains("android.net.conn.CONNECTIVITY_CHANGE"))){
            Intent serviceIntent = new Intent(context, PersistentAgent.class);
            serviceIntent.putExtra("Action", "Start");
            context.startService(serviceIntent);
        }
    }
}