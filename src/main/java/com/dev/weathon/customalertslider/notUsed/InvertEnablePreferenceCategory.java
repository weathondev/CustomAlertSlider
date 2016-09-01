package com.dev.weathon.customalertslider.notUsed;

import android.content.Context;
import android.preference.PreferenceCategory;
import android.preference.SwitchPreference;
import android.util.AttributeSet;

/**
 * Created by Joshua on 09.08.2016.
 */
public class InvertEnablePreferenceCategory extends PreferenceCategory {
    public InvertEnablePreferenceCategory(Context context) {
        this(context, null);
    }

    public InvertEnablePreferenceCategory(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    public boolean isEnabled() {
        return !super.isEnabled();
    }
}
