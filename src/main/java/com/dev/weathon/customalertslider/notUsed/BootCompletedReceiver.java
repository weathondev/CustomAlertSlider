package com.dev.weathon.customalertslider.notUsed;

import android.Manifest;
import android.app.AndroidAppHelper;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.crossbowffs.remotepreferences.RemotePreferences;

/**
 * Created by Joshua on 05.08.2016.
 */
public class BootCompletedReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("CustomAlertSlider", "Starting service");
        SharedPreferences settings = new RemotePreferences(context, "com.dev.weathon.customalertslider", "com.dev.weathon.customalertslider_preferences");
        if (settings.getBoolean("headPhoneMode", false)){
            //int permissionCheck = ContextCompat.checkSelfPermission(context,
            //        Manifest.permission.BLUETOOTH);
            //if (permissionCheck == PackageManager.PERMISSION_GRANTED)
                context.startService(new Intent(context, MyService.class));
        }
    }
}
