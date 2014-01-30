package com.daneel.pausab;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by daneel on 2014/01/28.
 */
public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String msg = "Broadcast Received, must start activity";
 //       msg = intent.getStringExtra(Resources.getSystem().getString(R.string.EXTRA_SERVERIP));
        Bundle bundle = intent.getExtras();
        int pauseDuration = 1;

        if (bundle != null){
            pauseDuration = Integer.decode(bundle.getString(PersistentAgent.EXTRA_PAUSEDURATION));
        }
        Toast Toast = null;
        Toast.makeText(context, msg + " " + String.valueOf(pauseDuration), Toast.LENGTH_LONG).show();

        Intent serviceIntent = new Intent(context, PersistentAgent.class);
        serviceIntent.putExtra("Pause", String.valueOf(pauseDuration));
        context.startService(serviceIntent);
    }
}