package com.dev.weathon.customalertslider.notUsed;

import android.app.AndroidAppHelper;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.util.Log;

import com.crossbowffs.remotepreferences.RemotePreferences;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.robv.android.xposed.XposedBridge;

/**
 * Created by Joshua on 05.08.2016.
 */
public class MyReceiverVolume extends BroadcastReceiver{


    public static final String EXTRA_VOLUME_STREAM_TYPE = "android.media.EXTRA_VOLUME_STREAM_TYPE";
    public static final String EXTRA_VOLUME_STREAM_VALUE = "android.media.EXTRA_VOLUME_STREAM_VALUE";
    public static final String EXTRA_PREV_VOLUME_STREAM_VALUE = "android.media.EXTRA_PREV_VOLUME_STREAM_VALUE";

    public static final int STREAM_DEFAULT = -1;
    public static final int STREAM_VOICE_CALL = 0;
    public static final int STREAM_SYSTEM = 1;
    public static final int STREAM_RING = 2;
    public static final int STREAM_MUSIC = 3;
    public static final int STREAM_ALARM = 4;
    public static final int STREAM_NOTIFICATION = 5;
    public static final int STREAM_BLUETOOTH_SCO = 6;
    public static final int STREAM_SYSTEM_ENFORCED = 7;
    public static final int STREAM_DTMF = 8;
    public static final int STREAM_TTS = 9;

    @Override
    public void onReceive(Context context, Intent intent) {
/*

        final int stream = intent.getIntExtra(EXTRA_VOLUME_STREAM_TYPE, -1);
        final int level = intent.getIntExtra(EXTRA_VOLUME_STREAM_VALUE, -1);
        final int oldLevel = intent.getIntExtra(EXTRA_PREV_VOLUME_STREAM_VALUE, -1);

        if (stream == AudioManager.STREAM_NOTIFICATION){
            AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);


            Log.e("CustomAlertSlider","onReceive VOLUME_CHANGED_ACTION stream=" + stream
                    + " level=" + level + " oldLevel=" + oldLevel + " ringermode: " + mAudioManager.getRingerMode());
        }



        SharedPreferences settings = new RemotePreferences(context,"com.dev.weathon.customalertslider", "com.dev.weathon.customalertslider_preferences");
        if (!settings.getBoolean("extremeCustomization", false) && settings.getBoolean("vibrateInsteadPriority", false)) {
            if (stream == AudioManager.STREAM_NOTIFICATION){
                try {
                    NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    Method getZenMode = nm.getClass().getDeclaredMethod("getZenMode");
                    if ((int)getZenMode.invoke(nm) == 2 && level != 0){
                        Log.e("CustomAlertSlider", "setSoundTo0");
                        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                        mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0, 0);
                    }
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

        }
        */
    }

}
