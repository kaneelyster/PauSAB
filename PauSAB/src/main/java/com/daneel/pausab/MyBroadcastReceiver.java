package com.daneel.pausab;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.widget.Toast;

/**
 * Created by daneel on 2014/01/28.
 */
public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String msg = "Broadcast Received, must start activit;y";
 //       msg = intent.getStringExtra(Resources.getSystem().getString(R.string.EXTRA_SERVERIP));
        Toast Toast = null;
        Toast.makeText(context, msg,
                Toast.LENGTH_LONG).show();
    }


}