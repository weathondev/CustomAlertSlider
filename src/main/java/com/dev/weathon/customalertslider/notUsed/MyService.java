package com.dev.weathon.customalertslider.notUsed;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

/**
 * Created by Joshua on 19.08.2016.
 */
public class MyService extends IntentService {
    private SettingsContentObserver mSettingsContentObserver;
    public MyService() {
        super("MyService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e("CustomAlertSlider", "service started");
        mSettingsContentObserver = new SettingsContentObserver(this,new Handler());
        getApplicationContext().getContentResolver().registerContentObserver(android.provider.Settings.System. CONTENT_URI, true, mSettingsContentObserver );


    }


}
