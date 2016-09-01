package com.dev.weathon.customalertslider.notUsed;

import android.content.Context;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Handler;
import android.util.Log;

import java.util.logging.Logger;

/**
 * Created by Joshua on 19.08.2016.
 */
public class SettingsContentObserver extends ContentObserver {
    int previousVolume;
    Context context;

    public SettingsContentObserver(Context c, Handler handler) {
        super(handler);
        context=c;
        //Log.e("CustomAlertSlider", "registered receiver");

        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        previousVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
//        Log.e("CustomAlertSlider", "registered receiver: previousVolume: " + previousVolume);

    }

    @Override
    public boolean deliverSelfNotifications() {
        return super.deliverSelfNotifications();
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);

        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        Log.e("CustomAlertSlider", "currentVol= " + currentVolume + " prevVol" + previousVolume);
        int delta=previousVolume-currentVolume;

        if(delta>0)
        {
            Log.e("CustomAlertSlider", "Decreased");
            previousVolume=currentVolume;
        }
        else if(delta<0)
        {
            Log.e("CustomAlertSlider","Increased");
            previousVolume=currentVolume;
        }
    }
}