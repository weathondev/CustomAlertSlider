package com.dev.weathon.customalertslider.notUsed;

import android.content.Context;
import android.preference.ListPreference;
import android.preference.SwitchPreference;
import android.util.AttributeSet;

/**
 * Created by Joshua on 09.08.2016.
 */
public class InvertEnableListPreference extends ListPreference {
    public InvertEnableListPreference(Context context) {
        this(context, null);
    }

    public InvertEnableListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    public boolean isEnabled() {
        return !super.isEnabled();
    }
}
