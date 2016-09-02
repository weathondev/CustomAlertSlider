package com.dev.weathon.customalertslider.hooks;


import android.app.AndroidAppHelper;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Vibrator;
import android.provider.Settings;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.crossbowffs.remotepreferences.RemotePreferences;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findConstructorBestMatch;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

/**
 * Created by Joshua on 05.08.2016.
 */
public class HookZenModeChange implements IXposedHookLoadPackage {

    private int newNotificationMode = 2;


    private static int NETWORK_MODE_GSM_ONLY = 1; //2g
    private static int NETWORK_MODE_GSM_UMTS = 3; //3g
    private static int NETWORK_MODE_LTE_GSM_WCDMA = 9; //4g



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
        WIFI_OFF(18);




        private final int value;

        private MyEnum(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        //if (!lpparam.packageName.equals("com.android.systemui"))
        if(!lpparam.packageName.equals("android"))
            return;

        findAndHookMethod("com.android.server.OemExService", lpparam.classLoader, "handleZenModeChanged", int.class, int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Log.w("CustomAlertSlider", "" + lpparam.packageName);
                Log.w("CustomAlertSlider", "BeforeChangingParam: NewValue = " + param.args[0] + ", OldValue = " + param.args[1]);
                SharedPreferences settings = new RemotePreferences(AndroidAppHelper.currentApplication(), "com.dev.weathon.customalertslider", "com.dev.weathon.customalertslider_preferences");

                NotificationManager nm = (NotificationManager) AndroidAppHelper.currentApplication().getSystemService(AndroidAppHelper.currentApplication().NOTIFICATION_SERVICE);
                Method getZenMode = nm.getClass().getDeclaredMethod("getZenMode");
                XposedBridge.log("change: before doing: " + getZenMode.invoke(nm));

                if (!settings.getBoolean("extremeCustomization", false)){

                    if (!settings.getBoolean("reverseSlider", false) && !settings.getBoolean("vibrateInsteadPriority", false)){
                        Log.w("CustomAlertSlider", "SILENT_PRIORITY_RING: do nothing");
                    }
                    else if (!settings.getBoolean("reverseSlider", false) && settings.getBoolean("vibrateInsteadPriority", false)){
                        Log.w("CustomAlertSlider", "SILENT_VIBRATE_RING: do nothing");
                    }
                    else if (settings.getBoolean("reverseSlider", false) && !settings.getBoolean("vibrateInsteadPriority", false)){
                        Log.w("CustomAlertSlider", "RING_PRIORITY_SILENT: reverse slider");
                        if (((int)param.args[0]) == 1){
                            Log.w("CustomAlertSlider", "RING_PRIORITY_SILENT: change zen value from 1 to 3");
                            param.args[0] = 3;
                        }
                        else if(((int)param.args[0]) == 3){
                            Log.w("CustomAlertSlider", "RING_PRIORITY_SILENT: change zen value from 3 to 1");
                            param.args[0] = 1;
                        }
                    }
                    else if (settings.getBoolean("reverseSlider", false) && settings.getBoolean("vibrateInsteadPriority", false)){
                        Log.w("CustomAlertSlider", "RING_VIBRATE_SILENT: reverse slider & replace priority with vibrate");
                        if (((int)param.args[0]) == 1){
                            Log.w("CustomAlertSlider", "RING_VIBRATE_SILENT: change zen value from 1 to 3");
                            param.args[0] = 3;
                        }
                        else if(((int)param.args[0]) == 3){
                            Log.w("CustomAlertSlider", "RING_VIBRATE_SILENT: change zen value from 3 to 1");
                            param.args[0] = 1;
                        }
                    }
                }
                else{
                    Log.w("CustomAlertSlider", "CustomSlider: set notification always to ALL");

                    newNotificationMode = (int)param.args[0];
                    /*
                    param.args[0] = 3;

                    if(settings.getBoolean("extendedZenModeControl", true))
                        param.args[0] = settings.getInt("extendedZenModeControlZenMode", 3);*/

                    Vibrator v = (Vibrator) AndroidAppHelper.currentApplication().getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(50);

                    param.setResult(null);
                }
            }
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                SharedPreferences settings = new RemotePreferences(AndroidAppHelper.currentApplication(), "com.dev.weathon.customalertslider", "com.dev.weathon.customalertslider_preferences");
                Log.w("CustomAlertSlider", "AfterChangingParam: NewValue = " + param.args[0] + ", OldValue = " + param.args[1]);
                AudioManager mAudioManager = (AudioManager) AndroidAppHelper.currentApplication().getSystemService(Context.AUDIO_SERVICE);



                if (!settings.getBoolean("extremeCustomization", false)) {
                    if (!settings.getBoolean("reverseSlider", false) && !settings.getBoolean("vibrateInsteadPriority", false)){
                        Log.w("CustomAlertSlider", "SILENT_PRIORITY_RING: do nothing");
                    }
                    else if (!settings.getBoolean("reverseSlider", false) && settings.getBoolean("vibrateInsteadPriority", false)){
                        Log.w("CustomAlertSlider", "SILENT_VIBRATE_RING: replace priority with vibrate");

                        if ((int)param.args[1] == 3){
                            if (mAudioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) != 0)
                                settings.edit().putInt("previousNotificationVolume", mAudioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION)).apply();
                            settings.edit().putInt("previousMusicVolume", mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC)).apply();

                            if (mAudioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) != 0)
                                Log.w("CustomAlertSlider", "currentNotVolume is " + mAudioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION));
                            Log.w("CustomAlertSlider", "currentMusicVolume is " + mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
                        }
                        if (((int)param.args[0]) == 2) {
                            //mAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                            mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0,0);
                            if (settings.getBoolean("silentMediaInVibrate", true))
                                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                            else
                                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, settings.getInt("previousMusicVolume", 0), 0);
                        }
                        else if(((int)param.args[0]) == 3){
                            Log.w("CustomAlertSlider", "zen value is 3, therefore set ring volume to normal ");
                            mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                            mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, settings.getInt("previousNotificationVolume",0),0);
                            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, settings.getInt("previousMusicVolume",0), 0);
                        }
                    }
                    else if (settings.getBoolean("reverseSlider", false) && !settings.getBoolean("vibrateInsteadPriority", false)){
                        Log.w("CustomAlertSlider", "RING_PRIORITY_SILENT: do nothing");
                    }
                    else if (settings.getBoolean("reverseSlider", false) && settings.getBoolean("vibrateInsteadPriority", false)){
                        Log.w("CustomAlertSlider", "RING_VIBRATE_SILENT: replace priority with vibrate");

                        if ((int)param.args[1] == 1){
                            if (mAudioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) != 0)
                                settings.edit().putInt("previousNotificationVolume",mAudioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION)).apply();
                            settings.edit().putInt("previousMusicVolume",mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC)).apply();

                            if (mAudioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) != 0)
                                Log.w("CustomAlertSlider", "currentNotVolume is " + mAudioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION));
                            Log.w("CustomAlertSlider", "currentMusicVolume is " + mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
                        }

                        if (((int)param.args[0]) == 1){
                            Log.w("CustomAlertSlider", "zen value is 1, therefore set ring volume to silent");
                            mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                        }
                        else if (((int)param.args[0]) == 2) {
                            //mAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                            mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0,0);
                            if (settings.getBoolean("silentMediaInVibrate", true))
                                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                            else
                                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, settings.getInt("previousMusicVolume",0), 0);
                        }
                        else if(((int)param.args[0]) == 3){
                            Log.w("CustomAlertSlider", "zen value is 3, therefore set ring volume to normal ");
                            mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                            mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, settings.getInt("previousNotificationVolume",0),0);
                            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, settings.getInt("previousMusicVolume",0), 0);
                        }
                    }
                }
                else{
                    Set<String> emptySet = Collections.emptySet();

                    ArrayList<MyEnum> TopPositionActions = new ArrayList<MyEnum>();
                    for(String s : settings.getStringSet("topPosition", emptySet)){
                        TopPositionActions.add(MyEnum.valueOf(s));
                    }
                    ArrayList<MyEnum> MidPositionActions = new ArrayList<MyEnum>();
                    for(String s : settings.getStringSet("midPosition", emptySet)){
                        MidPositionActions.add(MyEnum.valueOf(s));
                    }
                    ArrayList<MyEnum> BotPositionActions = new ArrayList<MyEnum>();
                    for(String s : settings.getStringSet("botPosition", emptySet)){
                        BotPositionActions.add(MyEnum.valueOf(s));
                    }

                    if(newNotificationMode == 1)
                        activateStates(AndroidAppHelper.currentApplication(), TopPositionActions, 1);
                    else if (newNotificationMode == 2)
                        activateStates(AndroidAppHelper.currentApplication(), MidPositionActions, 2);
                    else if (newNotificationMode == 3)
                        activateStates(AndroidAppHelper.currentApplication(), BotPositionActions, 3);


                }
            }
        });
    }

    //Activate Custom States
    private static void activateStates(Context context, ArrayList<MyEnum> modeList, int position){
        if (modeList.contains(MyEnum.AIRPLANE_ON))
            enableAirplane(1);
        if (modeList.contains(MyEnum.AIRPLANE_OFF))
            enableAirplane(0);
        if (modeList.contains(MyEnum.BLUETOOTH_ON))
            enableBluetooth(true);
        if (modeList.contains(MyEnum.BLUETOOTH_OFF))
            enableBluetooth(false);
        if (modeList.contains(MyEnum.FLASHLIGHT_ON))
            enableFlashlight(context,true);
        if (modeList.contains(MyEnum.FLASHLIGHT_OFF))
            enableFlashlight(context, false);
        if (modeList.contains(MyEnum.GPS_HIGH_ACCURACY))
            setGPS(context, Settings.Secure.LOCATION_MODE_HIGH_ACCURACY);
        if (modeList.contains(MyEnum.GPS_DEVICE_ONLY))
            setGPS(context, Settings.Secure.LOCATION_MODE_SENSORS_ONLY);
        if (modeList.contains(MyEnum.GPS_BATTERY_SAVING))
            setGPS(context, Settings.Secure.LOCATION_MODE_BATTERY_SAVING);
        if (modeList.contains(MyEnum.GPS_OFF))
            setGPS(context, Settings.Secure.LOCATION_MODE_OFF);
        if (modeList.contains(MyEnum.MOBILE_DATA_ON))
            enableMobileData(context, true);
        if (modeList.contains(MyEnum.MOBILE_DATA_OFF))
            enableMobileData(context, false);
        if (modeList.contains(MyEnum.PREFER_NETWORK_2G))
            setPreferredNetworkType(context, NETWORK_MODE_GSM_ONLY);
        if (modeList.contains(MyEnum.PREFER_NETWORK_3G))
            setPreferredNetworkType(context, NETWORK_MODE_GSM_UMTS);
        if (modeList.contains(MyEnum.PREFER_NETWORK_4G))
            setPreferredNetworkType(context, NETWORK_MODE_LTE_GSM_WCDMA);
        if (modeList.contains(MyEnum.STARTAPP))
            startApp(context, position);
        if (modeList.contains(MyEnum.WIFI_ON))
            enableWifi(context, true);
        if (modeList.contains(MyEnum.WIFI_OFF))
            enableWifi(context, false);
    }


    //Custom Action Methods
    private static void enableAirplane(int enable){
        //boolean enable = Settings.Global.getInt(AndroidAppHelper.currentApplication().getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) == 1;
        //Settings.Global.putInt(AndroidAppHelper.currentApplication().getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, enable ? 0 : 1);
        Settings.Global.putInt(AndroidAppHelper.currentApplication().getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, enable);
        Intent intentAirplane = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        //intentAirplane.putExtra("state", enable ? 0 : 1);
        intentAirplane.putExtra("state", enable);
        AndroidAppHelper.currentApplication().sendBroadcast(intentAirplane);
    }
    @SuppressWarnings("MissingPermission")
    private static void enableBluetooth(boolean enable){
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
    private static void enableFlashlight(Context context, boolean enable){
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
    private static void setGPS(Context context, int mode) {
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
    private static void enableMobileData(Context context, boolean enable) {
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
    private static void setPreferredNetworkType(Context context, int prefNetworkType){
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
    private static void startApp(Context context, int position){
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
    private static void enableWifi(Context context, boolean enable){
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
