package com.example.omarf.photogallery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by omarf on 1/12/2017.
 */

public class StartupReceiver extends BroadcastReceiver {
    private static final String TAG = "StartupReceiverTag";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG,"receive broadcast intent: "+intent.getAction());
        boolean isOn=QueryPreferences.isAlarmOn(context);
        PollService.setServiceAlarm(context,isOn);

    }
}
