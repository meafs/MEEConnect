package com.fhb.meeconnect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;


public class AlarmReceiver extends BroadcastReceiver {

    private Context ctx;
    @Override
    public void onReceive(Context context, Intent intent) {

        ctx = context;

        Intent service = new Intent(context, BirthdayNotifyService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(service);
        } else {
            context.startService(service);
        }

    }


}
