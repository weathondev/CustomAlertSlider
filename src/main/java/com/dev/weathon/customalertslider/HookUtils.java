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

import com.crossbowffs.remotepreferences.RemotePreferences;

import java.lang.reflect.Method;

/**
 * Created by Joshua on 02.09.2016.
 */
public final class HookUtils { //final because the class should be handled like c#'s static class

    private HookUtils(){} //private constructor because the class should be handled like c#'s static class

    //Custom Action Methods
    public static void enableAirplane(int enable){
        //boolean enable = Settings.Global.getInt(AndroidAppHelper.currentApplication().getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) == 1;
        //Settings.Global.putInt(AndroidAppHelper.currentApplication().getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, enable ? 0 : 1);
        Settings.Global.putInt(AndroidAppHelper.currentApplication().getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, enable);
        Intent intentAirplane = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        //intentAirplane.putExtra("state", enable ? 0 : 1);
        intentAirplane.putExtra("state", enable);
        AndroidAppHelper.currentApplication().sendBroadcast(intentAirplane);
    }
    @SuppressWarnings("MissingPermission")
    public static void enableBluetooth(boolean enable){
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        /*
        if (adapter.isEnabled())
            adapter.disable();
        else
            adapter.enable();*/
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
        /*
        //Get GPS now state (open or closed)
        boolean gpsEnabled = Settings.Secure.isLocationProviderEnabled(context.getContentResolver(), LocationManager.GPS_PROVIDER);

        if (gpsEnabled) {
            Settings.Secure.putInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE, 0);
        } else {
            Settings.Secure.putInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE, 3);
        }*/

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
    public static void startApp(Context context, int position){
        String packagename = null;
        if (position == 1)
            packagename = new RemotePreferences(AndroidAppHelper.currentApplication(), "com.dev.weathon.customalertslider", "com.dev.weathon.customalertslider_preferences").getString("topPosition_app", null);
        else if (position == 2)
            packagename = new RemotePreferences(AndroidAppHelper.currentApplication(), "com.dev.weathon.customalertslider", "com.dev.weathon.customalertslider_preferences").getString("midPosition_app", null);
        else if (position == 3)
            packagename = new RemotePreferences(AndroidAppHelper.currentApplication(), "com.dev.weathon.customalertslider", "com.dev.weathon.customalertslider_preferences").getString("botPosition_app", null);

        if (packagename != null){
            Intent i = context.getPackageManager().getLaunchIntentForPackage(packagename);
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



    /*private static boolean getMobileDataState(Context context) {
        try
        {
            TelephonyManager telephonyService = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);

            Method getMobileDataEnabledMethod = telephonyService.getClass().getDeclaredMethod("getDataEnabled");

            if (null != getMobileDataEnabledMethod)
            {
                boolean mobileDataEnabled = (Boolean) getMobileDataEnabledMethod.invoke(telephonyService);

                return mobileDataEnabled;
            }
        }
        catch (Exception ex)
        {
            Log.e("CustomAlertSlider", "Error getting mobile data state", ex);
        }

        return false;
    }*/
}

