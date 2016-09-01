package com.dev.weathon.customalertslider;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AndroidAppHelper;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.PopupWindow;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {


    public static Preference startAppPreference;
    private static Preference prefScreenNotificationSlider;
    private static Preference prefScreenCustomSlider;
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

                if (preference.getKey().equals("language")){
                    if (!value.toString().equals(preference.getContext().getSharedPreferences("com.dev.weathon.customalertslider_preferences", MODE_PRIVATE).getString("language", "system"))){
                        ((Activity)preference.getContext()).recreate();
                    }
                }
                /*
                else if (preference.getKey().equals("topPosition") || preference.getKey().equals("midPosition") || preference.getKey().equals("botPosition")){
                    if (value.equals("STARTAPP")){
                        preference.setSummary(preference.getSummary() + ": " + preference.getContext().getSharedPreferences("com.dev.weathon.customalertslider_preferences", MODE_PRIVATE).getString(preference.getKey() + "_app",""));

                        Intent applicationIntent = new Intent(preference.getContext(), AllAppsActivity.class);
                        applicationIntent.putExtra("preferenceKey", preference.getKey());
                        startAppPreference = preference;
                        Activity currentActivity = getActivity();
                        if (currentActivity != null){
                            currentActivity.startActivityForResult(applicationIntent, 1);
                        }
                    }
                }
                */

            }
            else if (preference instanceof MultiSelectListPreference){
                MultiSelectListPreference multiSelectListPreference = (MultiSelectListPreference) preference;
                if (multiSelectListPreference.getSummary() == null){
                    if(value.equals(""))
                        multiSelectListPreference.setSummary(multiSelectListPreference.getContext().getResources().getString(R.string.noAction));
                    else {
                        Set<String> values = (Set<String>) value;

                        if (!values.isEmpty()) {
                            String selectedValueList = getSelectedEntries(values, multiSelectListPreference).toString();
                            multiSelectListPreference.setSummary(selectedValueList.substring(1, selectedValueList.length() - 1));

                            if (values.contains("STARTAPP")) {
                                String startapp = preference.getContext().getResources().getString(R.string.startappstring);
                                preference.setSummary(preference.getSummary().toString().replace(startapp, startapp + ": " + preference.getContext().getSharedPreferences("com.dev.weathon.customalertslider_preferences", MODE_PRIVATE).getString(preference.getKey() + "_app", "")));


                                Intent applicationIntent = new Intent(preference.getContext(), AllAppsActivity.class);
                                applicationIntent.putExtra("preferenceKey", preference.getKey());
                                startAppPreference = preference;
                                Activity currentActivity = getActivity();
                                if (currentActivity != null) {
                                    currentActivity.startActivityForResult(applicationIntent, 1);
                                }
                            }
                        }
                        else
                            preference.setSummary(preference.getContext().getResources().getString(R.string.noAction));
                    }
                }
            }
            else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };


    private static Set<CharSequence> getSelectedEntries(Set<String> values, MultiSelectListPreference multilistPreference) {
        Set<CharSequence> labels = new HashSet<CharSequence>();
        for(String value: values) {
            int index = multilistPreference.findIndexOfValue(value);
            labels.add(multilistPreference.getEntries()[index]);
        }
        multilistPreference.setSummary(labels.toString());
        return labels;
    }

    private static Preference.OnPreferenceChangeListener onPreferenceChangeListenerCheckBox = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            if (preference.getKey().equals("reverseSlider") && (boolean) value){
                AlertDialog.Builder dialog = new AlertDialog.Builder(preference.getContext());
                dialog.setTitle(preference.getContext().getResources().getString(R.string.AlertDialogTitle));
                dialog.setMessage(preference.getContext().getResources().getString(R.string.AlertDialogMessage));
                dialog.setNeutralButton("OK", null);
                dialog.create().show();
            }
            else if (preference.getKey().equals("showIcon")){
                boolean b = (boolean) value;
                Log.v("CustomAlertSlider", "show icon is " + b);
                PackageManager pm = preference.getContext().getPackageManager();
                pm.setComponentEnabledSetting(
                        new ComponentName(preference.getContext(), "com.dev.weathon.customalertslider.show_ic"), b ? PackageManager.COMPONENT_ENABLED_STATE_DISABLED: PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                        PackageManager.DONT_KILL_APP);
            }
            else if (preference.getKey().equals("vibrateInsteadPriority") || preference.getKey().equals("extremeCustomization")){
                SharedPreferences prefs = preference.getContext().getSharedPreferences("bootPreferences", MODE_WORLD_READABLE);
                prefs.edit().putBoolean(preference.getKey(), (boolean)value).apply();

                if (preference.getKey().equals("extremeCustomization")){
                    if ((boolean)value){
                        prefScreenNotificationSlider.setEnabled(false);
                        prefScreenCustomSlider.setEnabled(true);
                    }
                    else{
                        prefScreenNotificationSlider.setEnabled(true);
                        prefScreenCustomSlider.setEnabled(false);
                    }
                }
                else if(preference.getKey().equals("vibrateInsteadPriority") && (boolean) value){
                    AlertDialog.Builder dialog = new AlertDialog.Builder(preference.getContext());
                    dialog.setTitle(preference.getContext().getResources().getString(R.string.AlertDialogTitle));
                    dialog.setMessage(preference.getContext().getResources().getString(R.string.AlertDialogMessage_VibrateMode));
                    dialog.setNeutralButton(preference.getContext().getResources().getString(R.string.Ok), null);
                    dialog.setPositiveButton(preference.getContext().getResources().getString(R.string.GoToPrioritySettings), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Activity currentActivity = getActivity();
                            if (currentActivity != null) {
                                currentActivity.startActivity(new Intent("android.settings.ZEN_MODE_PRIORITY_SETTINGS"));
                            }
                        }
                    });
                    dialog.create().show();
                }
            }
            else if (preference.getKey().equals("vibrateModeText")){
                SharedPreferences prefs = preference.getContext().getSharedPreferences("bootPreferences", MODE_WORLD_READABLE);
                prefs.edit().putString(preference.getKey(), value.toString()).apply();
                preference.setSummary(value.toString());
            }

            return true;
        }
    };

    private static Preference.OnPreferenceClickListener onPreferenceClickListener = new Preference.OnPreferenceClickListener(){

        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (preference.getKey().equals("donate")){
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=8EB9ZF3UC4LVL"));
                preference.getContext().startActivity(browserIntent);
                return true;
            }
            return false;
        }
    };


    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    private static boolean updateResources(Context context, String language) {
            Locale locale;

            if (language.equals("system"))
                locale = Resources.getSystem().getConfiguration().locale;
            else
                locale = new Locale(language);

            Locale.setDefault(locale);

            Resources resources = context.getResources();

            Configuration configuration = resources.getConfiguration();
            configuration.setLocale(locale);

            resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        return true;

    }

    public static Activity getActivity() {
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

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    private static void bindPreferenceSummaryToValueForMultiList(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.

        Set<String> emptySet = Collections.emptySet();

        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getStringSet(preference.getKey(), emptySet));
    }

    private static void bindPreferenceBooleanValueAlert(Preference preference) {
        preference.setOnPreferenceChangeListener(onPreferenceChangeListenerCheckBox);

        if (preference.getKey().equals("vibrateModeText")){
            onPreferenceChangeListenerCheckBox.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(), "Vibration"));
        }
    }

    private static void bindPreferenceOnClick(Preference preference) {
        preference.setOnPreferenceClickListener(onPreferenceClickListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences settings  = getSharedPreferences("com.dev.weathon.customalertslider_preferences", MODE_PRIVATE);
        SharedPreferences bootPreferences = getSharedPreferences("bootPreferences", MODE_WORLD_READABLE);
        if (settings.getBoolean("first_time_started_1.0.4", true)){
            settings.edit().clear().apply(); //put that away in the next version!!
            bootPreferences.edit().clear().apply();//put that away in the next version!!
            settings.edit().putBoolean("first_time_started_1.0.4", false).apply();
        }
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new GeneralPreferenceFragment())
                .commit();

        //updateResources(this, settings.getString("language", "system"));


    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }



    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        Context context = null;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.

            //bindPreferenceSummaryToValue(findPreference("language"));


            //bindsummary for multilistPreferences
            bindPreferenceSummaryToValueForMultiList(findPreference("topPosition"));
            bindPreferenceSummaryToValueForMultiList(findPreference("midPosition"));
            bindPreferenceSummaryToValueForMultiList(findPreference("botPosition"));



            //bindsummary for TextPreferences
            bindPreferenceSummaryToValue(findPreference("extendedVolumeControlAllNotText"));
            bindPreferenceSummaryToValue(findPreference("extendedVolumeControlPriorityText"));
            bindPreferenceSummaryToValue(findPreference("extendedVolumeControlSilentText"));

            //do stuff for SwitchPreferences-Value Change
            bindPreferenceBooleanValueAlert(findPreference("vibrateModeText"));
            bindPreferenceBooleanValueAlert(findPreference("reverseSlider"));
            bindPreferenceBooleanValueAlert(findPreference("showIcon"));
            bindPreferenceBooleanValueAlert(findPreference("vibrateInsteadPriority"));
            bindPreferenceBooleanValueAlert(findPreference("extremeCustomization"));

            //do stuff when preference get clicked
            bindPreferenceOnClick(findPreference("donate"));

            //save to static to enable/disable PreferenceScreens
            prefScreenNotificationSlider = findPreference("prefScreenNotificationSlider");
            prefScreenCustomSlider = findPreference("prefScreenCustomSlider");

            if (((SwitchPreference)findPreference("extremeCustomization")).isChecked()){
                prefScreenNotificationSlider.setEnabled(false);
                prefScreenCustomSlider.setEnabled(true);
            }
            else{
                prefScreenNotificationSlider.setEnabled(true);
                prefScreenCustomSlider.setEnabled(false);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1){
            if (resultCode == Activity.RESULT_OK){
                SharedPreferences settings = startAppPreference.getContext().getSharedPreferences("com.dev.weathon.customalertslider_preferences", MODE_PRIVATE);
                Set<String> emptySet = Collections.emptySet();
                Set<String> values = settings.getStringSet(startAppPreference.getKey(), emptySet);

                if (!values.isEmpty()) {
                    String startapp = getResources().getString(R.string.startappstring);
                    String selectedValueList = getSelectedEntries(values, (MultiSelectListPreference) startAppPreference).toString();
                    String summary = selectedValueList.substring(1, selectedValueList.length() - 1);
                    summary = summary.replace(startapp, startapp + ": " + data.getStringExtra("result"));
                    startAppPreference.setSummary(summary);
                }
                else
                    startAppPreference.setSummary(startAppPreference.getContext().getResources().getString(R.string.noAction));
            }
        }
    }
}
