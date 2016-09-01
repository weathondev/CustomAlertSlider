package com.dev.weathon.customalertslider.notUsed;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.ListPreference;
import android.util.AttributeSet;

import com.dev.weathon.customalertslider.R;

/**
 * Created by Joshua on 09.08.2016.
 */
public class DependentListPreference extends ListPreference{
    private final String CLASS_NAME = this.getClass().getSimpleName();
    private String dependentValue = "a";

    public DependentListPreference(Context context) {
        this(context,null);
    }
    public DependentListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DependentListPreference);
            dependentValue = a.getString(R.styleable.DependentListPreference_dependentValue);
            a.recycle();
        }
    }

    @Override
    public void setValue(String value) {
        String mOldValue = getValue();
        super.setValue(value);
        if (!value.equals(mOldValue)) {
            notifyDependencyChange(shouldDisableDependents());
        }
    }

    @Override
    public boolean shouldDisableDependents() {
        boolean shouldDisableDependents = super.shouldDisableDependents();
        String value = getValue();
        String[] dependentValues = dependentValue.split(";");
        return shouldDisableDependents || value == null || !(value.equals(dependentValues[0]) || value.equals(dependentValues[1]));
    }
}
