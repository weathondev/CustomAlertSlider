package com.dev.weathon.customalertslider;


import android.Manifest;
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
import android.os.PersistableBundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

import com.crossbowffs.remotepreferences.RemotePreferences;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import de.robv.android.xposed.XposedBridge;

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
    private static Preference topPosition;
    private static Preference midPosition;
    private static Preference botPosition;

    private static SliderPositionValue topPositionSliderVal;
    private static SliderPositionValue midPositionSliderVal;
    private static SliderPositionValue botPositionSliderVal;

    private static Preference hideNotificationToasts;
    private static Preference prefScreenExtendedVolumeControl;

    private static int MY_PERMISSIONS_REQUEST_BLUETOOTH;


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
                else if (preference.getKey().equals("usedOS")){
                    SharedPreferences prefs = preference.getContext().getSharedPreferences("bootPreferences", MODE_WORLD_READABLE);
                    prefs.edit().putString(preference.getKey(), value.toString()).apply();

                    if (value.toString().equals("oxygen")){
                        prefScreenExtendedVolumeControl.setEnabled(true);
                        hideNotificationToasts.setEnabled(true);
                    }
                    else if (value.toString().equals("cyanogen")){
                        prefScreenExtendedVolumeControl.setEnabled(false);
                        hideNotificationToasts.setEnabled(false);
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
            /*if (preference.getKey().equals("reverseSlider") && (boolean) value){
                AlertDialog.Builder dialog = new AlertDialog.Builder(preference.getContext());
                dialog.setTitle(preference.getContext().getResources().getString(R.string.AlertDialogTitle));
                dialog.setMessage(preference.getContext().getResources().getString(R.string.AlertDialogMessage));
                dialog.setNeutralButton("OK", null);
                dialog.create().show();
            }*/
            if (preference.getKey().equals("showIcon")){
                boolean b = (boolean) value;
                Log.v("CustomAlertSlider", "show icon is " + b);
                PackageManager pm = preference.getContext().getPackageManager();
                pm.setComponentEnabledSetting(
                        new ComponentName(preference.getContext(), "com.dev.weathon.customalertslider.show_ic"), b ? PackageManager.COMPONENT_ENABLED_STATE_DISABLED: PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                        PackageManager.DONT_KILL_APP);
            }
            else if (preference.getKey().equals("vibrateInsteadPriority")){
                SharedPreferences prefs = preference.getContext().getSharedPreferences("bootPreferences", MODE_WORLD_READABLE);
                prefs.edit().putBoolean(preference.getKey(), (boolean)value).apply();

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
            else if (preference.getKey().equals("vibrateModeText")){
                SharedPreferences prefs = preference.getContext().getSharedPreferences("bootPreferences", MODE_WORLD_READABLE);
                prefs.edit().putString(preference.getKey(), value.toString()).apply();
                preference.setSummary(value.toString());
            }
            /*else if (preference.getKey().equals("headPhoneMode")){
                boolean b = (boolean) value;
                if (b){
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.BLUETOOTH},  MY_PERMISSIONS_REQUEST_BLUETOOTH);
                    }
                }
            }*/

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
            else if (preference.getKey().equals("topPosition")){
                Activity activity = getActivity();
                Intent intent = new Intent(activity, DynamicActivity.class);
                intent.putExtra("positionKey", preference.getKey());
                intent.putExtra("positionTitle", activity.getResources().getString(R.string.pref_title_topPosition));
                activity.startActivityForResult(intent, 1);
                return true;
            }
            else if (preference.getKey().equals("midPosition")){
                Activity activity = getActivity();
                Intent intent = new Intent(activity, DynamicActivity.class);
                intent.putExtra("positionKey", preference.getKey());
                intent.putExtra("positionTitle", activity.getResources().getString(R.string.pref_title_midPosition));
                activity.startActivityForResult(intent, 2);
                return true;
            }
            else if (preference.getKey().equals("botPosition")){
                Activity activity = getActivity();
                Intent intent = new Intent(activity, DynamicActivity.class);
                intent.putExtra("positionKey", preference.getKey());
                intent.putExtra("positionTitle", activity.getResources().getString(R.string.pref_title_botPosition));
                activity.startActivityForResult(intent, 3);
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

    private static void setPositionSummarys(Preference preference, String key, SliderPositionValue val){

        if (val != null){
            ArrayList<SliderAction> actions = val.getActions();
            int count = actions.size();
            String summary = "";
            Context context = preference.getContext();

            for (int i = 0; i < count; i++) {
                String actionName = actions.get(i).getDisplayName();
                String parameters = "";

                Set<Map.Entry<String, String>> set2 = actions.get(i).getStringParameters().entrySet();
                for (Map.Entry<String, String> stringparam : set2) {
                    parameters = parameters.equals("") ? keyToValConvert(context, stringparam.getValue()) : parameters + ", " + keyToValConvert(context, stringparam.getValue());
                }

                Set<Map.Entry<String, Integer>> set3 = actions.get(i).getIntParameters().entrySet();
                for (Map.Entry<String, Integer> intparam : set3) {
                    parameters = parameters.equals("") ? intparam.getValue() + "" : parameters + ", " + intparam.getValue();
                }

                Set<Map.Entry<String, Boolean>> set = actions.get(i).getBooleanParameters().entrySet();
                for (Map.Entry<String, Boolean> boolparam : set) {
                    parameters = parameters.equals("") ? boolparam.getValue() + "" : parameters + ", " + boolparam.getValue();
                }

                summary = summary.equals("") ? actionName : summary + "; " + actionName;

                if (!parameters.equals("")){
                    summary = summary + " [" + parameters + "]";
                }
            }


            preference.setSummary(summary);
        }
        else
            preference.setSummary("");
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
        }else if (settings.getBoolean("first_time_started_1.0.13", true)){
            settings.edit().remove("topPosition");
            settings.edit().remove("midPosition");
            settings.edit().remove("botPosition");
            settings.edit().putBoolean("first_time_started_1.0.4", false).apply();
            settings.edit().putBoolean("first_time_started_1.0.13", false).apply();
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(getResources().getString(R.string.AlertDialogTitle));
            dialog.setMessage(getResources().getString(R.string.AlertDialogMessageDataDeleted));
            dialog.setNeutralButton(getResources().getString(R.string.Ok), null);
            dialog.create().show();
        }
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new GeneralPreferenceFragment())
                .commit();

        Gson gson = new Gson();
        String json = settings.getString("topPosition", "");
        topPositionSliderVal = gson.fromJson(json, SliderPositionValue.class);
        gson = new Gson();
        json = settings.getString("midPosition", "");
        midPositionSliderVal = gson.fromJson(json, SliderPositionValue.class);
        gson = new Gson();
        json = settings.getString("botPosition", "");
        botPositionSliderVal = gson.fromJson(json, SliderPositionValue.class);

        //updateResources(this, settings.getString("language", "system"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences settings  = getSharedPreferences("com.dev.weathon.customalertslider_preferences", MODE_PRIVATE);
        if(settings.getString("usedOS", "oxygen").equals("cyanogen"))
            readSliderConfiguration(settings);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences settings  = getSharedPreferences("com.dev.weathon.customalertslider_preferences", MODE_PRIVATE);
        if(settings.getString("usedOS", "oxygen").equals("cyanogen"))
            readSliderConfiguration(settings);
    }

    private void readSliderConfiguration(SharedPreferences settings){
        final String NOTIF_SLIDER_TOP_NODE = "/proc/tri-state-key/keyCode_top";
        final String NOTIF_SLIDER_MIDDLE_NODE = "/proc/tri-state-key/keyCode_middle";
        final String NOTIF_SLIDER_BOTTOM_NODE = "/proc/tri-state-key/keyCode_bottom";

        Map<String, Integer> zenValueMapping = new HashMap<String, Integer>();
        zenValueMapping.put(HookUtils.AllNotificationHardwareVal, HookUtils.AllNotificationZenVal);
        zenValueMapping.put(HookUtils.PriorityHardwareVal, HookUtils.PriorityZenVal);
        zenValueMapping.put(HookUtils.TotalSilenceHardwareVal, HookUtils.TotalSilenceZenVal);
        zenValueMapping.put(HookUtils.AlarmsOnlyHardwareVal, HookUtils.AlarmsOnlyZenVal);

        readRootFile(NOTIF_SLIDER_TOP_NODE, "SliderIsOnTop", zenValueMapping, settings);
        readRootFile(NOTIF_SLIDER_MIDDLE_NODE, "SliderIsOnMid", zenValueMapping, settings);
        readRootFile(NOTIF_SLIDER_BOTTOM_NODE, "SliderIsOnBot", zenValueMapping, settings);
    }

    private void readRootFile(String filePath, String preference, Map<String, Integer> zenValueMapping, SharedPreferences settings) {
        String retVal = null;
        String content = "";
        try {
            Process process = Runtime.getRuntime().exec("su");
            InputStream in = process.getInputStream();
            OutputStream out = process.getOutputStream();
            String cmd = "cat " + filePath;
            out.write(cmd.getBytes());
            out.flush();
            out.close();

            Scanner s = new Scanner(in).useDelimiter("\\A");
            content = s.hasNext() ? s.next() : "";
            //Wait until reading finishes
            process.waitFor();
        } catch (IOException e) {
            Log.e("CustomAlertSlider", "IOException, " + e.getMessage());
        } catch (InterruptedException e) {
            Log.e("CustomAlertSlider", "InterruptedException, " + e.getMessage());
        }

        //int i = Integer.parseInt(content);


        Log.w("CustomAlertSlider", "content: " + content + " utilshardwareallnotific" + HookUtils.AllNotificationHardwareVal);


        int zenValue = 0;
        if (content.contains(HookUtils.AllNotificationHardwareVal))
            zenValue = HookUtils.AllNotificationZenVal;
        else if (content.contains(HookUtils.PriorityHardwareVal))
            zenValue = HookUtils.PriorityZenVal;
        else if (content.contains(HookUtils.AlarmsOnlyHardwareVal))
            zenValue = HookUtils.AlarmsOnlyZenVal;
        else if (content.contains(HookUtils.TotalSilenceHardwareVal))
            zenValue = HookUtils.TotalSilenceZenVal;

        Log.w("CustomAlertSlider", "save " + preference + " " + zenValue);
        settings.edit().putInt(preference, zenValue).apply();

        //Do your stuff here with "content" string
        //The "content" String has the content of readed file
        /*} catch (IOException e) {
            Log.e("CustomAlertSlider", "IOException, " + e.getMessage());
        } catch (InterruptedException e) {
            Log.e("CustomAlertSlider", "InterruptedException, " + e.getMessage());
        }*/
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


    private static String keyToValConvert(Context context, String key){
        String val = key;

        try{
            int keyPos = Arrays.asList(context.getResources().getStringArray(R.array.key_to_val_keys)).indexOf(key);
            val = context.getResources().getStringArray(R.array.key_to_val_vals)[keyPos];
        }
        catch (Exception e){
            return val;
        }
        return val;
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

            //save to static to enable/disable PreferenceScreens
            hideNotificationToasts = findPreference("hideToast");
            prefScreenExtendedVolumeControl = findPreference("prefScreenExtendedVolumeControl");

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.

            //bindPreferenceSummaryToValue(findPreference("language"));
            bindPreferenceSummaryToValue(findPreference("usedOS"));

            //bindsummary for multilistPreferences
            /*
            bindPreferenceSummaryToValueForMultiList(findPreference("topPosition"));
            bindPreferenceSummaryToValueForMultiList(findPreference("midPosition"));
            bindPreferenceSummaryToValueForMultiList(findPreference("botPosition"));
            */

            //bindsummary for TextPreferences
            bindPreferenceSummaryToValue(findPreference("extendedVolumeControlAllNotText"));
            bindPreferenceSummaryToValue(findPreference("extendedVolumeControlPriorityText"));
            bindPreferenceSummaryToValue(findPreference("extendedVolumeControlSilentText"));

            //do stuff for SwitchPreferences-Value Change
            bindPreferenceBooleanValueAlert(findPreference("vibrateModeText"));
            bindPreferenceBooleanValueAlert(findPreference("showIcon"));
            bindPreferenceBooleanValueAlert(findPreference("vibrateInsteadPriority"));
            //bindPreferenceBooleanValueAlert(findPreference("headPhoneMode"));

            //do stuff when preference get clicked

            topPosition = findPreference("topPosition");
            midPosition = findPreference("midPosition");
            botPosition = findPreference("botPosition");


            bindPreferenceOnClick(findPreference("donate"));
            bindPreferenceOnClick(topPosition);
            bindPreferenceOnClick(midPosition);
            bindPreferenceOnClick(botPosition);

            setPositionSummarys(topPosition, "topPosition", topPositionSliderVal);
            setPositionSummarys(midPosition, "midPosition", midPositionSliderVal);
            setPositionSummarys(botPosition, "botPosition", botPositionSliderVal);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1){ //top
            if (resultCode == Activity.RESULT_OK) {
                topPosition.setSummary(data.getStringExtra("summary"));
            }
        }
        else if (requestCode == 2){ //mid
            if (resultCode == Activity.RESULT_OK) {
                midPosition.setSummary(data.getStringExtra("summary"));
            }
        }
        else if (requestCode == 3){ //bot
            if (resultCode == Activity.RESULT_OK) {
                botPosition.setSummary(data.getStringExtra("summary"));
            }
        }


        /*if (requestCode == 1){
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
        }*/
    }

}
