package com.dev.weathon.customalertslider.notUsed;

import android.content.Context;
import android.preference.CheckBoxPreference;
import android.preference.SwitchPreference;
import android.util.AttributeSet;

/**
 * Created by Joshua on 09.08.2016.
 */
public class InvertEnableCheckBoxPreference extends CheckBoxPreference {
    public InvertEnableCheckBoxPreference(Context context) {
        this(context, null);
    }

    public InvertEnableCheckBoxPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    public boolean isEnabled() {
        return !super.isEnabled();
    }
}
