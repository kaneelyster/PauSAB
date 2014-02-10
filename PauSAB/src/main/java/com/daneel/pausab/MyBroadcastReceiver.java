package com.daneel.pausab;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by daneel on 2014/01/28.
 */
public class MyBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //String msg = "Broadcast Received, must start activity";
        String action = intent.getAction();

//        Toast.makeText(context, msg + " " + action, Toast.LENGTH_SHORT).show();

        Intent serviceIntent = new Intent(context, PersistentAgent.class);
        serviceIntent.putExtra("Pause", action);
        context.startService(serviceIntent);
    }
}