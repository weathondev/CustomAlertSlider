package com.dev.weathon.customalertslider;

import android.app.AndroidAppHelper;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.Settings;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.weathon.customalertslider.tasker.TaskerPlugin;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

/**
 * Created by Joshua on 02.09.2016.
 */
public final class HookUtils { //final because the class should be handled like c#'s static class
    public static final String INTENT_SLIDER_CHANGED = "com.dev.weathon.customalertslider.SLIDER_CHANGED";


    private static int NETWORK_MODE_GSM_ONLY = 1; //2g
    private static int NETWORK_MODE_GSM_UMTS = 3; //3g
    private static int NETWORK_MODE_LTE_GSM_WCDMA = 9; //4g

    private HookUtils(){} //private constructor because the class should be handled like c#'s static class

/*
<item>ALL_NOTIFICATIONS</item>
        <item>PRIORITY</item>
        <item>ALARMS_ONLY</item>
        <item>TOTAL_SILENCE</item>
        <item>AIRPLANE</item>
        <item>BLUETOOTH</item>
        <item>FLASHLIGHT</item>
        <item>GPS</item>
        <item>MOBILE_DATA</item>
        <item>PREFER_NETWORK</item>
        <item>SCREEN_ORIENTATION</item>
        <item>SCREEN_BRIGHTNESS</item>
        <item>STARTAPP</item>
        <item>WIFI</item>
 */
    public enum MyEnum {
        ALL_NOTIFICATIONS(0),
        PRIORITY(1),
        ALARMS_ONLY(2),
        TOTAL_SILENCE(3),
        AIRPLANE(4),
        BLUETOOTH(5),
        FLASHLIGHT(6),
        GPS(7),
        MOBILE_DATA(8),
        PREFER_NETWORK(9),
        SCREEN_ORIENTATION(10),
        SCREEN_BRIGHTNESS(11),
        STARTAPP(12),
        WIFI(13),
        AUDIO_VOLUME(14),
        TOAST(15),
        BATTERY_SAVING(16),
        BATTERY_SAVING_AUTOMATIC(17),
        LOCK_SCREEN_NOTIFICATION(18),
        NOTIFICATION_LED(19),
        CHARGING_LED(20);

        private final int value;

        private MyEnum(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

    //Activate Custom States
    public static void activateStates(Context context, ArrayList<SliderAction> modeList){
        for(SliderAction s : modeList){
            Set<Map.Entry<String, String>> stringParams = s.getStringParameters().entrySet();
            Set<Map.Entry<String, Boolean>> boolParams = s.getBooleanParameters().entrySet();
            Set<Map.Entry<String, Integer>> intParams = s.getIntParameters().entrySet();
            Log.e("CustomAlertSlider", "activateStates");
            for (Map.Entry<String, String> param : stringParams) {
                Log.e("CustomAlertSlider", param.getKey());
            }
            for (Map.Entry<String, Integer> param : intParams) {
                Log.e("CustomAlertSlider", param.getKey());
            }

            if (s.getId().equalsIgnoreCase(MyEnum.TOAST.toString())){
                for (Map.Entry<String, String> param : stringParams) {
                    if (param.getKey().equalsIgnoreCase("text")){
                        showToast(context, param.getValue());
                    }
                }
            }
            if (s.getId().equalsIgnoreCase(MyEnum.AIRPLANE.toString())){
                for (Map.Entry<String, String> param : stringParams) {
                    if (param.getKey().equalsIgnoreCase("mode")){
                        if (param.getValue().equalsIgnoreCase("on"))
                            enableAirplane(true);
                        else if (param.getValue().equalsIgnoreCase("off"))
                            enableAirplane(false);
                    }
                }
            }
            if (s.getId().equalsIgnoreCase(MyEnum.BLUETOOTH.toString())){
                for (Map.Entry<String, String> param : stringParams) {
                    if (param.getKey().equalsIgnoreCase("mode")){
                        if (param.getValue().equalsIgnoreCase("on"))
                            enableBluetooth(true);
                        else if (param.getValue().equalsIgnoreCase("off"))
                            enableBluetooth(false);
                    }
                }
            }
            if (s.getId().equalsIgnoreCase(MyEnum.FLASHLIGHT.toString())){
                for (Map.Entry<String, String> param : stringParams) {
                    if (param.getKey().equalsIgnoreCase("mode")){
                        if (param.getValue().equalsIgnoreCase("on"))
                            enableFlashlight(context, true);
                        else if (param.getValue().equalsIgnoreCase("off"))
                            enableFlashlight(context, false);
                    }
                }
            }
            if (s.getId().equalsIgnoreCase(MyEnum.GPS.toString())){
                for (Map.Entry<String, String> param : stringParams) {
                    if (param.getKey().equalsIgnoreCase("mode")){
                        if (param.getValue().equalsIgnoreCase("HIGH_ACCURACY"))
                            setGPS(context, Settings.Secure.LOCATION_MODE_HIGH_ACCURACY);
                        else if (param.getValue().equalsIgnoreCase("DEVICE_ONLY"))
                            setGPS(context, Settings.Secure.LOCATION_MODE_SENSORS_ONLY);
                        else if (param.getValue().equalsIgnoreCase("BATTERY_SAVING"))
                            setGPS(context, Settings.Secure.LOCATION_MODE_BATTERY_SAVING);
                        else if (param.getValue().equalsIgnoreCase("GPS_OFF"))
                            setGPS(context, Settings.Secure.LOCATION_MODE_OFF);
                    }
                }
            }
            if (s.getId().equalsIgnoreCase(MyEnum.MOBILE_DATA.toString())){
                for (Map.Entry<String, String> param : stringParams) {
                    if (param.getKey().equalsIgnoreCase("mode")){
                        if (param.getValue().equalsIgnoreCase("on"))
                            enableMobileData(context, true);
                        else if (param.getValue().equalsIgnoreCase("off"))
                            enableMobileData(context, false);
                    }
                }
            }
            if (s.getId().equalsIgnoreCase(MyEnum.WIFI.toString())){
                for (Map.Entry<String, String> param : stringParams) {
                    if (param.getKey().equalsIgnoreCase("mode")){
                        if (param.getValue().equalsIgnoreCase("on"))
                            enableWifi(context, true);
                        else if (param.getValue().equalsIgnoreCase("off"))
                            enableWifi(context, false);
                    }
                }
            }
            if (s.getId().equalsIgnoreCase(MyEnum.PREFER_NETWORK.toString())){
                for (Map.Entry<String, String> param : stringParams) {
                    if (param.getKey().equalsIgnoreCase("mode")){
                        if (param.getValue().equalsIgnoreCase("2G"))
                            setPreferredNetworkType(context, NETWORK_MODE_GSM_ONLY);
                        else if (param.getValue().equalsIgnoreCase("3G"))
                            setPreferredNetworkType(context, NETWORK_MODE_GSM_UMTS);
                        else if (param.getValue().equalsIgnoreCase("4G"))
                            setPreferredNetworkType(context, NETWORK_MODE_LTE_GSM_WCDMA);
                    }
                }
            }
            if (s.getId().equalsIgnoreCase(MyEnum.SCREEN_ORIENTATION.toString())){
                for (Map.Entry<String, String> param : stringParams) {
                    if (param.getKey().equalsIgnoreCase("mode")){
                        if (param.getValue().equalsIgnoreCase("AUTO"))
                            enableAutoScreenRotation(context, true);
                        else if (param.getValue().equalsIgnoreCase("PORTRAIT"))
                            enableAutoScreenRotation(context, false);
                    }
                }
            }
            if (s.getId().equalsIgnoreCase(MyEnum.BATTERY_SAVING.toString())){
                for (Map.Entry<String, String> param : stringParams) {
                    if (param.getKey().equalsIgnoreCase("mode")){
                        if (param.getValue().equalsIgnoreCase("on"))
                            enableBatterySaving(true);
                        else if (param.getValue().equalsIgnoreCase("off"))
                            enableBatterySaving(false);
                    }
                }
            }
            if (s.getId().equalsIgnoreCase(MyEnum.NOTIFICATION_LED.toString())){
                for (Map.Entry<String, String> param : stringParams) {
                    if (param.getKey().equalsIgnoreCase("mode")){
                        if (param.getValue().equalsIgnoreCase("ALLOW_LED"))
                            allowNotificationLED(true);
                        else if (param.getValue().equalsIgnoreCase("DENY_LED"))
                            allowNotificationLED(false);
                    }
                }
            }
            if (s.getId().equalsIgnoreCase(MyEnum.BATTERY_SAVING_AUTOMATIC.toString())){
                for (Map.Entry<String, String> param : stringParams) {
                    if (param.getKey().equalsIgnoreCase("mode")){
                        if (param.getValue().equalsIgnoreCase("BATTERY_SAVING_NEVER"))
                            setBatterySavingAutomatic(0);
                        else if (param.getValue().equalsIgnoreCase("BATTERY_SAVING_5"))
                            setBatterySavingAutomatic(5);
                        else if (param.getValue().equalsIgnoreCase("BATTERY_SAVING_15"))
                            setBatterySavingAutomatic(15);
                    }
                }
            }
            if (s.getId().equalsIgnoreCase(MyEnum.LOCK_SCREEN_NOTIFICATION.toString())){
                for (Map.Entry<String, String> param : stringParams) {
                    if (param.getKey().equalsIgnoreCase("mode")){
                        if (param.getValue().equalsIgnoreCase("SHOW_ALL_NOTIFICATIONS"))
                            setLockScreenShowNotifications(0);
                        else if (param.getValue().equalsIgnoreCase("SHOW_ALL_BUT_PRIVATE_NOTIFICATIONS"))
                            setLockScreenShowNotifications(1);
                        else if (param.getValue().equalsIgnoreCase("DONT_SHOW_NOTIFICATIONS"))
                            setLockScreenShowNotifications(2);
                    }
                }
            }
            if (s.getId().equalsIgnoreCase(MyEnum.SCREEN_BRIGHTNESS.toString())){
                for (Map.Entry<String, String> param : stringParams) {
                    if (param.getKey().equalsIgnoreCase("mode")){
                        if (param.getValue().equalsIgnoreCase("AUTO"))
                            setDisplayBrightnessModeAuto(context);
                    }
                }
                for (Map.Entry<String, Integer> param : intParams) {
                    if (param.getKey().equalsIgnoreCase("brightness_level")){
                        setDisplayBrightness(context, param.getValue());
                    }
                }
            }
            if (s.getId().equalsIgnoreCase(MyEnum.STARTAPP.toString())){
                for (Map.Entry<String, String> param : stringParams) {
                    if (param.getKey().equalsIgnoreCase("apptostart")){
                        HookUtils.startApp(context, param.getValue());
                    }
                }
            }
            if (s.getId().equalsIgnoreCase(MyEnum.AUDIO_VOLUME.toString())){
                String volumeType = "";
                for (Map.Entry<String, String> param : stringParams) {
                    if (param.getKey().equalsIgnoreCase("mode")){
                        volumeType = param.getValue();
                    }
                }
                for (Map.Entry<String, Integer> param : intParams) {
                    if (param.getKey().equalsIgnoreCase("volume")){
                        setAudioVolume(context, volumeType, param.getValue());
                    }
                }
            }

            Log.e("CustomAlertSlider", "activateStates end");
        }
    }


    //Custom Action Methods
    public static void enableAirplane(boolean enable){
        Settings.Global.putInt(AndroidAppHelper.currentApplication().getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, enable ? 1 : 0);
        Intent intentAirplane = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        intentAirplane.putExtra("state", enable ? 1 : 0);
        AndroidAppHelper.currentApplication().sendBroadcast(intentAirplane);
    }
    @SuppressWarnings("MissingPermission")
    public static void enableBluetooth(boolean enable){
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if(enable)
            adapter.enable();
        else
            adapter.disable();
    }
    public static void enableFlashlight(Context context, boolean enable){
        try{
            CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            if (enable)
                manager.setTorchMode(manager.getCameraIdList()[0], true);
            else
                manager.setTorchMode(manager.getCameraIdList()[0], false);
        }
        catch (CameraAccessException cae){
            Log.e("CustomAlertSlider", cae.getMessage());
            cae.printStackTrace();
        }
    }
    public static void setGPS(Context context, int mode) {
        Settings.Secure.putInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE, mode);
    }
    public static void enableMobileData(Context context, boolean enable) {
        try
        {
            TelephonyManager telephonyService = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);

            Method setMobileDataEnabledMethod = telephonyService.getClass().getDeclaredMethod("setDataEnabled", boolean.class);

            if (null != setMobileDataEnabledMethod)
            {

                //setMobileDataEnabledMethod.invoke(telephonyService, !getMobileDataState(context));
                setMobileDataEnabledMethod.invoke(telephonyService, enable);
            }
        }
        catch (Exception ex)
        {
            Log.e("CustomAlertSlider", "Error setting mobile data state", ex);
        }
    }
    public static void setPreferredNetworkType(Context context, int prefNetworkType){
        try{
            TelephonyManager telephonyService = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
            Method getDefaultDataSubIdMethod = SubscriptionManager.class.getDeclaredMethod("getDefaultDataSubId");
            Method setPreferredNetworkTypeMethod = telephonyService.getClass().getDeclaredMethod("setPreferredNetworkType", int.class, int.class);

            if(null != getDefaultDataSubIdMethod && null != setPreferredNetworkTypeMethod){
                int defaultId = (int) getDefaultDataSubIdMethod.invoke(telephonyService);
                setPreferredNetworkTypeMethod.invoke(telephonyService, defaultId, prefNetworkType);
            }
        }
        catch (Exception ex)
        {
            Log.e("CustomAlertSlider", "Error setting to 2g", ex);
        }
    }
    public static void startApp(Context context, String appToStart){
        if (appToStart != null){
            Intent i = context.getPackageManager().getLaunchIntentForPackage(appToStart);
            context.startActivity(i);
        }
    }
    @SuppressWarnings("MissingPermission")
    public static void enableWifi(Context context, boolean enable){
        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        /*
        if (wifiManager.isWifiEnabled())
            wifiManager.setWifiEnabled(false);
        else
            wifiManager.setWifiEnabled(true);*/

        wifiManager.setWifiEnabled(enable);
    }
    public static void enableAutoScreenRotation(Context context, boolean enable){
        Settings.System.putInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, enable ? 1 : 0);
    }
    public static void enableBatterySaving(boolean enable){
        try {
            Runtime.getRuntime().exec("settings put global low_power " + (enable == true ? 1 : 0));
        } catch (IOException e) {
            Log.e("CustomAlertSlider", "enableBatterySavingException: " + e.getMessage());
        }
    }
    public static void allowNotificationLED(boolean enable){
        try {
            Runtime.getRuntime().exec("settings put system notification_light_pulse " + (enable == true ? 1 : 0));
        } catch (IOException e) {
            Log.e("CustomAlertSlider", "allowNotificationLED: " + e.getMessage());
        }
    }
    public static void setBatterySavingAutomatic(int mode){
        try {
            Runtime.getRuntime().exec("settings put global low_power_trigger_level " + mode);
        } catch (IOException e) {
            Log.e("CustomAlertSlider", "setBatterySavingAutomatic: " + "mode : " + e.getMessage());
        }
    }
    public static void setDisplayBrightnessModeAuto(Context context){
        Settings.System.putInt(context.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
    }
    public static void setDisplayBrightness(Context context, int brightness){
        Settings.System.putInt(context.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        Settings.System.putInt(context.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS, brightness);
    }
    public static void setAudioVolume(Context context, String volumeType, int volume){
        try {
            AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if (volumeType.equalsIgnoreCase("STREAM_MUSIC"))
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
            else if (volumeType.equalsIgnoreCase("STREAM_NOTIFICATION"))
                mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, volume, 0);
            else if (volumeType.equalsIgnoreCase("STREAM_RING"))
                mAudioManager.setStreamVolume(AudioManager.STREAM_RING, volume, 0);
            else if (volumeType.equalsIgnoreCase("STREAM_ALARM"))
                mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, volume, 0);
        }
        catch (Exception ex)
        {
            Log.e("CustomAlertSlider", "Error setting audio", ex);
        }
    }
    public static void setLockScreenShowNotifications(int mode){
        try {
            if (mode == 0){ //Show All
                Runtime.getRuntime().exec("settings put secure lock_screen_show_notifications 1");
                Runtime.getRuntime().exec("settings put secure lock_screen_allow_private_notifications 1");
            }
            else if (mode == 1){ //Show all but private
                Runtime.getRuntime().exec("settings put secure lock_screen_show_notifications 1");
                Runtime.getRuntime().exec("settings put secure lock_screen_allow_private_notifications 0");
            }
            else if (mode == 2){ //Show none
                Runtime.getRuntime().exec("settings put secure lock_screen_show_notifications 0");
            }
        } catch (Exception e) {
            Log.e("CustomAlertSlider", "setLockScreenShowNotifications: " + mode + " : " + e.getMessage());
        }
    }
    public static void showToast(Context context, String text) {
        LinearLayout linLayout = new LinearLayout(context);
        linLayout.setOrientation(LinearLayout.VERTICAL);
        linLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        linLayout.setPadding(20, 20, 20, 20);
        linLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        linLayout.setBackgroundColor(Color.parseColor("#212121"));
        final float scale = context.getResources().getDisplayMetrics().density;
        int height = (int) (100 * scale + 0.5f);
        int width = (int) (224 * scale + 0.5f);
        linLayout.setMinimumHeight(height);
        linLayout.setMinimumWidth(width);

        TextView tv = new TextView(context);
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        llp.setMargins(0, 16, 0, 0);
        tv.setLayoutParams(llp);
        tv.setGravity(Gravity.CENTER);
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tv.setTextSize(14);
        tv.setTextColor(Color.parseColor("#FFFFFF"));
        tv.setText(text);

        linLayout.addView(tv, 0);

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, -25);
        toast.setView(linLayout);
        toast.show();
    }

    public static final String AllNotificationHardwareVal = "603";
    public static final String PriorityHardwareVal = "602";
    public static final String TotalSilenceHardwareVal = "600";
    public static final String AlarmsOnlyHardwareVal = "601";

    public static final int TotalSilenceZenValOxygen = 1;
    public static final int PriorityZenValOxygen = 2;
    public static final int AllNotificationZenValOxygen = 3;


    public static final int AllNotificationZenVal = 0;
    public static final int PriorityZenVal = 1;
    public static final int TotalSilenceZenVal = 2;
    public static final int AlarmsOnlyZenVal = 3;

}

