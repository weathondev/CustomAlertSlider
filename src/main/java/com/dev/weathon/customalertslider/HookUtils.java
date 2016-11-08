package com.dev.weathon.customalertslider;

import android.app.AndroidAppHelper;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.net.wifi.WifiManager;
import android.os.Vibrator;
import android.provider.Settings;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.robv.android.xposed.XposedBridge;

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
        WIFI(13);

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
                            enableAutoScreenRotation(true);
                        else if (param.getValue().equalsIgnoreCase("PORTRAIT"))
                            enableAutoScreenRotation(false);
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
    public static void enableAutoScreenRotation(boolean enable){
        Settings.System.putInt(AndroidAppHelper.currentApplication().getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, enable ? 1 : 0);
    }

    public static void setDisplayBrightnessModeAuto(Context context){
        Settings.System.putInt(context.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
    }
    public static void setDisplayBrightness(Context context, int brightness){
        Settings.System.putInt(context.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        Settings.System.putInt(context.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS, brightness);
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

