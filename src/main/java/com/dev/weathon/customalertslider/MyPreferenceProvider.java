package com.dev.weathon.customalertslider;

import com.crossbowffs.remotepreferences.RemotePreferenceProvider;

/**
 * Created by Joshua on 07.08.2016.
 */
public class MyPreferenceProvider extends RemotePreferenceProvider {
    public MyPreferenceProvider() {
        super("com.dev.weathon.customalertslider", new String[] {"com.dev.weathon.customalertslider_preferences"});
    }
}