package com.scorpion.brightnessmanager.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;


import com.scorpion.brightnessmanager.service.MyService;

public class CallBroadCastReceiver extends BroadcastReceiver {

    SharedPreferences shref;
    SharedPreferences.Editor editor;

    public void onReceive(Context context, Intent intent) {

        shref = context.getSharedPreferences("MyPref", 0);
        editor = shref.edit();
        Log.e("CALL","CALL");
        if (shref.getInt("serviceONBrightness", 0) == 1 || shref.getInt("serviceONRotation", 0) == 1 || shref.getInt("serviceONVolume", 0) == 1){
            Log.e("CALL","CALL2");
            Intent intent2 = new Intent(context, MyService.class);
            if (Build.VERSION.SDK_INT >= 26) {
                context.startForegroundService(intent2);
            } else {
                context.startService(intent2);
            }
        }
    }
}
