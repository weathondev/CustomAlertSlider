package com.dev.weathon.customalertslider.notUsed;

import android.app.AndroidAppHelper;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Joshua on 05.08.2016.
 */
public class MyReceiver extends BroadcastReceiver{

    public static final String PREFS_NAME = "CustomAlertSliderPrefs";

    @Override
    public void onReceive(Context context, Intent intent) {

//        Log.e("CustomAlertSlider", "Starting service");
        //context.startService(new Intent(context, MyService.class));
        /*
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        Log.w("CustomAlertSlider", "setPreferredNetworkType");

        if (intent.hasExtra("SliderMiddlePosition")){
            if (intent.getBooleanExtra("SliderMiddlePosition", false)){
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("previousNotificationVolume", mAudioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION));
                editor.putInt("previousMusicVolume", mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
                editor.commit();

                Log.w("CustomAlertSlider", "currentNotVolume is " + mAudioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION));
                Log.w("CustomAlertSlider", "previousMusicVolume is " + mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
            }
            mAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
        }
        else if (intent.hasExtra("SliderTopPosition")){
            mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, settings.getInt("previousNotificationVolume", 0),0);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, settings.getInt("previousMusicVolume", 0), 0);
        }
        else if (intent.hasExtra("SliderBottomPosition")){
            mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
        }
        */


    }
}
