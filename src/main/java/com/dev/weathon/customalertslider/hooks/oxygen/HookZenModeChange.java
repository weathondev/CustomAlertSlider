package com.dev.weathon.customalertslider.hooks.oxygen;


import android.app.AndroidAppHelper;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.AudioManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.crossbowffs.remotepreferences.RemotePreferences;
import com.dev.weathon.customalertslider.HookUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findConstructorBestMatch;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

/**
 * Created by Joshua on 05.08.2016.
 */
public class HookZenModeChange implements IXposedHookLoadPackage {

    private int newNotificationMode = 2;
    private boolean setResultNull = false;

    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        //if (!lpparam.packageName.equals("com.android.systemui"))
        if(!lpparam.packageName.equals("android"))
            return;

        XSharedPreferences settings = new XSharedPreferences("com.dev.weathon.customalertslider", "bootPreferences");
        settings.makeWorldReadable();
        String usedOS = settings.getString("usedOS", "oxygen");

        if (usedOS.equals("oxygen")) {
            findAndHookMethod("com.android.server.OemExService", lpparam.classLoader, "handleZenModeChanged", int.class, int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    Log.w("CustomAlertSlider", "" + lpparam.packageName);
                    Log.w("CustomAlertSlider", "BeforeChangingParam: NewValue = " + param.args[0] + ", OldValue = " + param.args[1]);
                    SharedPreferences settings = new RemotePreferences(AndroidAppHelper.currentApplication(), "com.dev.weathon.customalertslider", "com.dev.weathon.customalertslider_preferences");
                    AudioManager mAudioManager = (AudioManager) AndroidAppHelper.currentApplication().getSystemService(Context.AUDIO_SERVICE);

                    NotificationManager nm = (NotificationManager) AndroidAppHelper.currentApplication().getSystemService(AndroidAppHelper.currentApplication().NOTIFICATION_SERVICE);
                    Method getZenMode = nm.getClass().getDeclaredMethod("getZenMode");
                    int oldZenValue = (int) getZenMode.invoke(nm);
                    XposedBridge.log("change: before doing: " + oldZenValue);

                    if (settings.getBoolean("vibrateInsteadPriority", false)){
                        if(oldZenValue == HookUtils.AllNotificationZenValOxygen){
                            if (mAudioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) != 0)
                                settings.edit().putInt("previousNotificationVolume", mAudioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION)).apply();
                        }
                        if(settings.getBoolean("silentMediaInVibrate", true)){
                            if (oldZenValue == HookUtils.AllNotificationZenValOxygen || oldZenValue == HookUtils.TotalSilenceZenValOxygen){
                                if (mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) != 0)
                                    settings.edit().putInt("previousMusicVolume", mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC)).apply();
                            }
                        }
                    }

                    newNotificationMode = (int) param.args[0];

                    Set<String> emptySet = Collections.emptySet();
                    setResultNull = false;

                    if(newNotificationMode == HookUtils.TotalSilenceZenValOxygen){
                        XposedBridge.log("newNotificationMode=TotalSilenceZenValOxygen" + newNotificationMode);
                        ArrayList<HookUtils.MyEnum> TopPositionActions = new ArrayList<HookUtils.MyEnum>();
                        for(String s : settings.getStringSet("topPosition", emptySet))
                            TopPositionActions.add(HookUtils.MyEnum.valueOf(s));

                        if (TopPositionActions.contains(HookUtils.MyEnum.ALL_NOTIFICATIONS))
                            param.args[0] = HookUtils.AllNotificationZenValOxygen;
                        else if (TopPositionActions.contains(HookUtils.MyEnum.PRIORITY))
                            param.args[0] = HookUtils.PriorityZenValOxygen;
                        else if (TopPositionActions.contains(HookUtils.MyEnum.ALARMS_ONLY) || TopPositionActions.contains(HookUtils.MyEnum.TOTAL_SILENCE))
                            param.args[0] = HookUtils.TotalSilenceZenValOxygen;
                        else{
                            Vibrator v = (Vibrator) AndroidAppHelper.currentApplication().getSystemService(Context.VIBRATOR_SERVICE);
                            v.vibrate(50);
                            param.setResult(null);
                            setResultNull = true;
                        }
                        XposedBridge.log("newNotificationMode=AllNotificationZenValOxygen new mode:" + param.args[0]);

                    }
                    else if(newNotificationMode == HookUtils.PriorityZenValOxygen){
                        XposedBridge.log("newNotificationMode=PriorityZenValOxygen" + newNotificationMode);
                        ArrayList<HookUtils.MyEnum> MidPositionActions = new ArrayList<HookUtils.MyEnum>();
                        for(String s : settings.getStringSet("midPosition", emptySet))
                            MidPositionActions.add(HookUtils.MyEnum.valueOf(s));

                        if (MidPositionActions.contains(HookUtils.MyEnum.ALL_NOTIFICATIONS))
                            param.args[0] = HookUtils.AllNotificationZenValOxygen;
                        else if (MidPositionActions.contains(HookUtils.MyEnum.PRIORITY))
                            param.args[0] = HookUtils.PriorityZenValOxygen;
                        else if (MidPositionActions.contains(HookUtils.MyEnum.ALARMS_ONLY) || MidPositionActions.contains(HookUtils.MyEnum.TOTAL_SILENCE))
                            param.args[0] = HookUtils.TotalSilenceZenValOxygen;
                        else{
                            Vibrator v = (Vibrator) AndroidAppHelper.currentApplication().getSystemService(Context.VIBRATOR_SERVICE);
                            v.vibrate(50);
                            param.setResult(null);
                            setResultNull = true;
                        }
                    }
                    else if(newNotificationMode == HookUtils.AllNotificationZenValOxygen){
                        XposedBridge.log("newNotificationMode=AllNotificationZenValOxygen" + newNotificationMode);
                        ArrayList<HookUtils.MyEnum> BotPositionActions = new ArrayList<HookUtils.MyEnum>();
                        for(String s : settings.getStringSet("botPosition", emptySet))
                            BotPositionActions.add(HookUtils.MyEnum.valueOf(s));

                        if (BotPositionActions.contains(HookUtils.MyEnum.ALL_NOTIFICATIONS))
                            param.args[0] = HookUtils.AllNotificationZenValOxygen;
                        else if (BotPositionActions.contains(HookUtils.MyEnum.PRIORITY))
                            param.args[0] = HookUtils.PriorityZenValOxygen;
                        else if (BotPositionActions.contains(HookUtils.MyEnum.ALARMS_ONLY) || BotPositionActions.contains(HookUtils.MyEnum.TOTAL_SILENCE))
                            param.args[0] = HookUtils.TotalSilenceZenValOxygen;
                        else{
                            Vibrator v = (Vibrator) AndroidAppHelper.currentApplication().getSystemService(Context.VIBRATOR_SERVICE);
                            v.vibrate(50);
                            param.setResult(null);
                            setResultNull = true;
                        }
                    }





                    /*
                    Vibrator v = (Vibrator) AndroidAppHelper.currentApplication().getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(50);

                    param.setResult(null);*/
                    //}
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    SharedPreferences settings = new RemotePreferences(AndroidAppHelper.currentApplication(), "com.dev.weathon.customalertslider", "com.dev.weathon.customalertslider_preferences");
                    Log.w("CustomAlertSlider", "AfterChangingParam: NewValue = " + param.args[0] + ", OldValue = " + param.args[1]);

                    Set<String> emptySet = Collections.emptySet();

                    ArrayList<HookUtils.MyEnum> TopPositionActions = new ArrayList<HookUtils.MyEnum>();
                    for (String s : settings.getStringSet("topPosition", emptySet)) {
                        TopPositionActions.add(HookUtils.MyEnum.valueOf(s));
                    }
                    ArrayList<HookUtils.MyEnum> MidPositionActions = new ArrayList<HookUtils.MyEnum>();
                    for (String s : settings.getStringSet("midPosition", emptySet)) {
                        MidPositionActions.add(HookUtils.MyEnum.valueOf(s));
                    }
                    ArrayList<HookUtils.MyEnum> BotPositionActions = new ArrayList<HookUtils.MyEnum>();
                    for (String s : settings.getStringSet("botPosition", emptySet)) {
                        BotPositionActions.add(HookUtils.MyEnum.valueOf(s));
                    }

                    if (newNotificationMode == HookUtils.TotalSilenceZenValOxygen)
                        HookUtils.activateStates(AndroidAppHelper.currentApplication(), TopPositionActions, settings.getString("topPosition_app", null));
                    else if (newNotificationMode == HookUtils.PriorityZenValOxygen)
                        HookUtils.activateStates(AndroidAppHelper.currentApplication(), MidPositionActions, settings.getString("midPosition_app", null));
                    else if (newNotificationMode == HookUtils.AllNotificationZenValOxygen)
                        HookUtils.activateStates(AndroidAppHelper.currentApplication(), BotPositionActions, settings.getString("botPosition_app", null));


                    if (!setResultNull){
                        if (settings.getBoolean("vibrateInsteadPriority", false)) {
                            final AudioManager mAudioManager = (AudioManager) AndroidAppHelper.currentApplication().getSystemService(Context.AUDIO_SERVICE);
                            if ((int)param.args[0] == HookUtils.PriorityZenValOxygen) {
                                XposedBridge.log("param.args[0]=PriorityZenValOxygen" + param.args[0]);
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    public void run() {
                                        mAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                                    }
                                }, 100);

                            } else if ((int)param.args[0] == HookUtils.AllNotificationZenValOxygen) {
                                XposedBridge.log("param.args[0]=AllNotificationZenValOxygen" + param.args[0]);
                                Handler handler = new Handler();
                                final int prevNotVol = settings.getInt("previousNotificationVolume", 4);
                                handler.postDelayed(new Runnable() {
                                    public void run() {
                                        mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                                        mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, prevNotVol, 0 );
                                    }
                                }, 100);
                            }
                            if(settings.getBoolean("silentMediaInVibrate", true)){
                                if ((int)param.args[0] == HookUtils.PriorityZenValOxygen)
                                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                                else if((int)param.args[0] == HookUtils.AllNotificationZenValOxygen || (int)param.args[0] == HookUtils.TotalSilenceZenValOxygen)
                                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, settings.getInt("previousMusicVolume", 2), 0);
                            }
                        }
                    }
                }
            });
        }
    }






}
