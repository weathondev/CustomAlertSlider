package com.dev.weathon.customalertslider.notUsed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.util.AttributeSet;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * Created by Joshua on 09.08.2016.
 */
public class InvertEnableMultiListPreference extends MultiSelectListPreference{
    public InvertEnableMultiListPreference(Context context) {
        this(context, null);
    }

    public InvertEnableMultiListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    public boolean isEnabled() {
        return !super.isEnabled();
    }

}
