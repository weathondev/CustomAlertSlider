package com.dev.weathon.customalertslider;

import android.app.AndroidAppHelper;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Joshua on 02.09.2016.
 */
public final class HookUtils { //final because the class should be handled like c#'s static class
    public static final String INTENT_SLIDER_CHANGED = "com.dev.weathon.customalertslider.SLIDER_CHANGED";


    private static int NETWORK_MODE_GSM_ONLY = 1; //2g
    private static int NETWORK_MODE_GSM_UMTS = 3; //3g
    private static int NETWORK_MODE_LTE_GSM_WCDMA = 9; //4g

    private HookUtils(){} //private constructor because the class should be handled like c#'s static class


    public enum MyEnum {
        AIRPLANE_ON(1),
        AIRPLANE_OFF(2),
        BLUETOOTH_ON(3),
        BLUETOOTH_OFF(4),
        FLASHLIGHT_ON(5),
        FLASHLIGHT_OFF(6),
        GPS_HIGH_ACCURACY(7),
        GPS_DEVICE_ONLY(8),
        GPS_BATTERY_SAVING(9),
        GPS_OFF(10),
        MOBILE_DATA_ON(11),
        MOBILE_DATA_OFF(12),
        PREFER_NETWORK_2G(13),
        PREFER_NETWORK_3G(14),
        PREFER_NETWORK_4G(15),
        STARTAPP(16),
        WIFI_ON(17),
        WIFI_OFF(18),
        ALL_NOTIFICATIONS(19),
        PRIORITY(20),
        ALARMS_ONLY(21),
        TOTAL_SILENCE(22),
        SCREEN_ORIENTATION_AUTO(23),
        SCREEN_ORIENTATION_PORTRAIT(24);

        private final int value;

        private MyEnum(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

    //Activate Custom States
    public static void activateStates(Context context, ArrayList<MyEnum> modeList, String appToStart){
        if (modeList.contains(MyEnum.AIRPLANE_ON))
            HookUtils.enableAirplane(true);
        if (modeList.contains(MyEnum.AIRPLANE_OFF))
            HookUtils.enableAirplane(false);
        if (modeList.contains(MyEnum.BLUETOOTH_ON))
            HookUtils.enableBluetooth(true);
        if (modeList.contains(MyEnum.BLUETOOTH_OFF))
            HookUtils.enableBluetooth(false);
        if (modeList.contains(MyEnum.FLASHLIGHT_ON))
            HookUtils.enableFlashlight(context,true);
        if (modeList.contains(MyEnum.FLASHLIGHT_OFF))
            HookUtils.enableFlashlight(context, false);
        if (modeList.contains(MyEnum.GPS_HIGH_ACCURACY))
            HookUtils.setGPS(context, Settings.Secure.LOCATION_MODE_HIGH_ACCURACY);
        if (modeList.contains(MyEnum.GPS_DEVICE_ONLY))
            HookUtils.setGPS(context, Settings.Secure.LOCATION_MODE_SENSORS_ONLY);
        if (modeList.contains(MyEnum.GPS_BATTERY_SAVING))
            HookUtils.setGPS(context, Settings.Secure.LOCATION_MODE_BATTERY_SAVING);
        if (modeList.contains(MyEnum.GPS_OFF))
            HookUtils.setGPS(context, Settings.Secure.LOCATION_MODE_OFF);
        if (modeList.contains(MyEnum.MOBILE_DATA_ON))
            HookUtils.enableMobileData(context, true);
        if (modeList.contains(MyEnum.MOBILE_DATA_OFF))
            HookUtils.enableMobileData(context, false);
        if (modeList.contains(MyEnum.PREFER_NETWORK_2G))
            HookUtils.setPreferredNetworkType(context, NETWORK_MODE_GSM_ONLY);
        if (modeList.contains(MyEnum.PREFER_NETWORK_3G))
            HookUtils.setPreferredNetworkType(context, NETWORK_MODE_GSM_UMTS);
        if (modeList.contains(MyEnum.PREFER_NETWORK_4G))
            HookUtils.setPreferredNetworkType(context, NETWORK_MODE_LTE_GSM_WCDMA);
        if (modeList.contains(MyEnum.STARTAPP))
            HookUtils.startApp(context, appToStart);
        if (modeList.contains(MyEnum.WIFI_ON))
            HookUtils.enableWifi(context, true);
        if (modeList.contains(MyEnum.WIFI_OFF))
            HookUtils.enableWifi(context, false);
        if (modeList.contains(MyEnum.SCREEN_ORIENTATION_AUTO))
            HookUtils.enableAutoScreenRotation(true);
        if (modeList.contains(MyEnum.SCREEN_ORIENTATION_PORTRAIT))
            HookUtils.enableAutoScreenRotation(false);
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

