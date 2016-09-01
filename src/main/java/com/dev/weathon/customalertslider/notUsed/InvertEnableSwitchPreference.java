package com.dev.weathon.customalertslider.notUsed;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.SwitchPreference;
import android.util.AttributeSet;

/**
 * Created by Joshua on 09.08.2016.
 */
public class InvertEnableSwitchPreference extends SwitchPreference {
    public InvertEnableSwitchPreference(Context context) {
        this(context, null);
    }

    public InvertEnableSwitchPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    public boolean isEnabled() {
        return !super.isEnabled();
    }
}
