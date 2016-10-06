package com.dev.weathon.customalertslider.hooks.cyanogen;


import android.app.AndroidAppHelper;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Vibrator;
import android.util.Log;

import com.crossbowffs.remotepreferences.RemotePreferences;
import com.dev.weathon.customalertslider.HookUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/**
 * Created by Joshua on 05.08.2016.
 */
public class HookZenModeChangeCyanogen implements IXposedHookLoadPackage {

    private int newNotificationMode = 2;
    private boolean setResultNull = false;


    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        if(!lpparam.packageName.equals("android"))
            return;

        XSharedPreferences settings = new XSharedPreferences("com.dev.weathon.customalertslider", "bootPreferences");
        settings.makeWorldReadable();
        String usedOS = settings.getString("usedOS", "oxygen");

        if (usedOS.equals("cyanogen")){
            findAndHookMethod("com.android.server.notification.ZenModeHelper", lpparam.classLoader, "setManualZenMode", int.class, Uri.class, String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    SharedPreferences settings = new RemotePreferences(AndroidAppHelper.currentApplication(),"com.dev.weathon.customalertslider", "com.dev.weathon.customalertslider_preferences");
                    AudioManager mAudioManager = (AudioManager) AndroidAppHelper.currentApplication().getSystemService(Context.AUDIO_SERVICE);
                    NotificationManager nm = (NotificationManager) AndroidAppHelper.currentApplication().getSystemService(AndroidAppHelper.currentApplication().NOTIFICATION_SERVICE);
                    Method getZenMode = nm.getClass().getDeclaredMethod("getZenMode");
                    int oldZenValue = (int) getZenMode.invoke(nm);
                    XposedBridge.log("CyanogenZenModeChange: old Value: " + oldZenValue);
                    XposedBridge.log("CyanogenZenModeChange: new Value: " + param.args[0]);


                    if (settings.getBoolean("vibrateInsteadPriority", false)){
                        if(oldZenValue == HookUtils.AllNotificationZenVal){
                            if (mAudioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) != 0)
                                settings.edit().putInt("previousNotificationVolume", mAudioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION)).apply();
                            if (mAudioManager.getStreamVolume(AudioManager.STREAM_RING) != 0)
                                settings.edit().putInt("previousRingerVolume", mAudioManager.getStreamVolume(AudioManager.STREAM_RING)).apply();
                        }
                        if(settings.getBoolean("silentMediaInVibrate", true)){
                            if (oldZenValue == HookUtils.AllNotificationZenVal || oldZenValue == HookUtils.AlarmsOnlyZenVal){
                                settings.edit().putInt("previousMusicVolume", mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC)).apply();
                            }
                        }
                    }


                    newNotificationMode = (int)param.args[0];

                    Set<String> emptySet = Collections.emptySet();
                    setResultNull = false;


                    if(newNotificationMode == settings.getInt("SliderIsOnTop", 0)){
                        sendSliderChangeIntent(AndroidAppHelper.currentApplication(), HookUtils.TotalSilenceZenValOxygen, settings.getBoolean("comingFromBoot", false));
                        ArrayList<HookUtils.MyEnum> TopPositionActions = new ArrayList<HookUtils.MyEnum>();
                        for(String s : settings.getStringSet("topPosition", emptySet))
                            TopPositionActions.add(HookUtils.MyEnum.valueOf(s));

                        if (TopPositionActions.contains(HookUtils.MyEnum.ALL_NOTIFICATIONS))
                            param.args[0] = HookUtils.AllNotificationZenVal;
                        else if (TopPositionActions.contains(HookUtils.MyEnum.PRIORITY))
                            param.args[0] = HookUtils.PriorityZenVal;
                        else if (TopPositionActions.contains(HookUtils.MyEnum.ALARMS_ONLY))
                            param.args[0] = HookUtils.AlarmsOnlyZenVal;
                        else if (TopPositionActions.contains(HookUtils.MyEnum.TOTAL_SILENCE))
                            param.args[0] = HookUtils.TotalSilenceZenVal;
                        else{
                            setResultNull = true;
                            param.setResult(null);
                        }
                    }
                    else if (newNotificationMode == settings.getInt("SliderIsOnMid", 0)) {
                        sendSliderChangeIntent(AndroidAppHelper.currentApplication(), HookUtils.PriorityZenValOxygen, settings.getBoolean("comingFromBoot", false));
                        ArrayList<HookUtils.MyEnum> MidPositionActions = new ArrayList<HookUtils.MyEnum>();
                        for (String s : settings.getStringSet("midPosition", emptySet))
                            MidPositionActions.add(HookUtils.MyEnum.valueOf(s));

                        if (MidPositionActions.contains(HookUtils.MyEnum.ALL_NOTIFICATIONS))
                            param.args[0] = HookUtils.AllNotificationZenVal;
                        else if (MidPositionActions.contains(HookUtils.MyEnum.PRIORITY))
                            param.args[0] = HookUtils.PriorityZenVal;
                        else if (MidPositionActions.contains(HookUtils.MyEnum.ALARMS_ONLY))
                            param.args[0] = HookUtils.AlarmsOnlyZenVal;
                        else if (MidPositionActions.contains(HookUtils.MyEnum.TOTAL_SILENCE))
                            param.args[0] = HookUtils.TotalSilenceZenVal;
                        else{
                            setResultNull = true;
                            param.setResult(null);
                        }
                    }
                    else if (newNotificationMode == settings.getInt("SliderIsOnBot", 0)){
                        sendSliderChangeIntent(AndroidAppHelper.currentApplication(), HookUtils.AllNotificationZenValOxygen, settings.getBoolean("comingFromBoot", false));
                        ArrayList<HookUtils.MyEnum> BotPositionActions = new ArrayList<HookUtils.MyEnum>();
                        for(String s : settings.getStringSet("botPosition", emptySet))
                            BotPositionActions.add(HookUtils.MyEnum.valueOf(s));

                        if (BotPositionActions.contains(HookUtils.MyEnum.ALL_NOTIFICATIONS))
                            param.args[0] = HookUtils.AllNotificationZenVal;
                        else if (BotPositionActions.contains(HookUtils.MyEnum.PRIORITY))
                            param.args[0] = HookUtils.PriorityZenVal;
                        else if (BotPositionActions.contains(HookUtils.MyEnum.ALARMS_ONLY))
                            param.args[0] = HookUtils.AlarmsOnlyZenVal;
                        else if (BotPositionActions.contains(HookUtils.MyEnum.TOTAL_SILENCE))
                            param.args[0] = HookUtils.TotalSilenceZenVal;
                        else{
                            setResultNull = true;
                            param.setResult(null);
                        }
                    }
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    SharedPreferences settings = new RemotePreferences(AndroidAppHelper.currentApplication(), "com.dev.weathon.customalertslider", "com.dev.weathon.customalertslider_preferences");

                    if (!settings.getBoolean("comingFromBoot", true)){
                        Set<String> emptySet = Collections.emptySet();

                        ArrayList<HookUtils.MyEnum> TopPositionActions = new ArrayList<HookUtils.MyEnum>();
                        for(String s : settings.getStringSet("topPosition", emptySet)){
                            TopPositionActions.add(HookUtils.MyEnum.valueOf(s));
                        }
                        ArrayList<HookUtils.MyEnum> MidPositionActions = new ArrayList<HookUtils.MyEnum>();
                        for(String s : settings.getStringSet("midPosition", emptySet)){
                            MidPositionActions.add(HookUtils.MyEnum.valueOf(s));
                        }
                        ArrayList<HookUtils.MyEnum> BotPositionActions = new ArrayList<HookUtils.MyEnum>();
                        for(String s : settings.getStringSet("botPosition", emptySet)){
                            BotPositionActions.add(HookUtils.MyEnum.valueOf(s));
                        }

                        if(newNotificationMode == settings.getInt("SliderIsOnTop", 0))
                            HookUtils.activateStates(AndroidAppHelper.currentApplication(), TopPositionActions, settings.getString("topPosition_app", null));
                        else if (newNotificationMode == settings.getInt("SliderIsOnMid", 0))
                            HookUtils.activateStates(AndroidAppHelper.currentApplication(), MidPositionActions, settings.getString("midPosition_app", null));
                        else if (newNotificationMode == settings.getInt("SliderIsOnBot", 0))
                            HookUtils.activateStates(AndroidAppHelper.currentApplication(), BotPositionActions, settings.getString("botPosition_app", null));
                    }
                    else
                        settings.edit().putBoolean("comingFromBoot", false).apply();

                    if (!setResultNull) {
                        if (settings.getBoolean("vibrateInsteadPriority", false)) {
                            AudioManager mAudioManager = (AudioManager) AndroidAppHelper.currentApplication().getSystemService(Context.AUDIO_SERVICE);
                            if ((int) param.args[0] == HookUtils.PriorityZenVal) {
                                mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0, 0);
                                mAudioManager.setStreamVolume(AudioManager.STREAM_RING, 0, 0);
                            } else if ((int) param.args[0] == HookUtils.AllNotificationZenVal) {
                                mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, settings.getInt("previousNotificationVolume", 4), 0);
                                mAudioManager.setStreamVolume(AudioManager.STREAM_RING, settings.getInt("previousRingerVolume", 4), 0);
                            }
                            if (settings.getBoolean("silentMediaInVibrate", true)) {
                                if ((int) param.args[0] == HookUtils.PriorityZenVal)
                                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                                else if ((int) param.args[0] == HookUtils.AllNotificationZenVal || (int) param.args[0] == HookUtils.AlarmsOnlyZenVal)
                                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, settings.getInt("previousMusicVolume", 2), 0);
                            }
                        }
                    }
                }
            });
        }




    }
    private void sendSliderChangeIntent(Context context, int newNotificationMode, boolean comingFromBoot) {
        XposedBridge.log("Slider change occured: " + newNotificationMode);

        Intent changeIntent = new Intent();

        changeIntent.setAction(HookUtils.INTENT_SLIDER_CHANGED);
        changeIntent.putExtra("state", newNotificationMode);
        changeIntent.putExtra("comingFromBoot", comingFromBoot);

        context.sendBroadcast(changeIntent);
    }
}
