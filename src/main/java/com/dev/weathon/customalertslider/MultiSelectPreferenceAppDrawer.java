package com.dev.weathon.customalertslider;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.MultiSelectListPreference;
import android.util.AttributeSet;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Joshua on 24.08.2016.
 */
public class MultiSelectPreferenceAppDrawer extends MultiSelectListPreference {
    public MultiSelectPreferenceAppDrawer(Context context) {
        this(context, null);
    }

    public MultiSelectPreferenceAppDrawer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        SharedPreferences settings = getContext().getSharedPreferences("com.dev.weathon.customalertslider_preferences", 0);
        Set<String> emptySet = Collections.emptySet();
        Set<String> values = settings.getStringSet(getKey(), emptySet);

        if (!values.isEmpty()) {
            String selectedValueList = getSelectedEntries(values, this).toString();
            setSummary(selectedValueList.substring(1, selectedValueList.length() - 1));

            if (values.contains("STARTAPP")) {
                String startapp = getContext().getResources().getString(R.string.startappstring);
                setSummary(getSummary().toString().replace(startapp, startapp + ": " + settings.getString(getKey() + "_app", "")));

                Intent applicationIntent = new Intent(getContext(), AllAppsActivity.class);
                applicationIntent.putExtra("preferenceKey", getKey());
                Activity currentActivity = getActivity();
                if (currentActivity != null) {
                    ((SettingsActivity)currentActivity).startAppPreference = this;
                    currentActivity.startActivityForResult(applicationIntent, 1);
                }
            }
        }
        else
            setSummary(getContext().getResources().getString(R.string.noAction));
    }

    private static Set<CharSequence> getSelectedEntries(Set<String> values, MultiSelectListPreference multilistPreference) {
        Set<CharSequence> labels = new HashSet<CharSequence>();
        for(String value: values) {
            int index = multilistPreference.findIndexOfValue(value);
            labels.add(multilistPreference.getEntries()[index]);
        }
        multilistPreference.setSummary(labels.toString());
        return labels;
    }
    private static Activity getActivity() {
        Class activityThreadClass = null;
        try {
            activityThreadClass = Class.forName("android.app.ActivityThread");
            Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
            Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
            activitiesField.setAccessible(true);

            Map<Object, Object> activities = (Map<Object, Object>) activitiesField.get(activityThread);
            if(activities == null)
                return null;

            for (Object activityRecord : activities.values()) {
                Class activityRecordClass = activityRecord.getClass();
                Field pausedField = activityRecordClass.getDeclaredField("paused");
                pausedField.setAccessible(true);
                if (!pausedField.getBoolean(activityRecord)) {
                    Field activityField = activityRecordClass.getDeclaredField("activity");
                    activityField.setAccessible(true);
                    Activity activity = (Activity) activityField.get(activityRecord);
                    return activity;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        return null;
    }
}
