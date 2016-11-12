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
import com.dev.weathon.customalertslider.SliderAction;
import com.dev.weathon.customalertslider.SliderPositionValue;
import com.google.gson.Gson;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
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

                    Gson gson = new Gson();
                    String json = settings.getString("botPosition", "");
                    SliderPositionValue obj = gson.fromJson(json, SliderPositionValue.class);
                    if (obj != null){
                        ArrayList<SliderAction> actions = obj.getActions();
                        int count = actions.size() - 1;

                        for (int i = 0; i < count; i++) {
                            XposedBridge.log("-------------------------------------------------------");
                            XposedBridge.log("Action: ");
                            XposedBridge.log(actions.get(i).getDisplayName());
                            int countBool = actions.get(i).getBooleanParameters().size() - 1;

                            Set<Map.Entry<String, Boolean>> set = actions.get(i).getBooleanParameters().entrySet();
                            for (Map.Entry<String, Boolean> boolparam : set) {
                                XposedBridge.log(boolparam.getKey() + ";" + boolparam.getValue());
                            }

                            Set<Map.Entry<String, String>> set2 = actions.get(i).getStringParameters().entrySet();
                            for (Map.Entry<String, String> stringparam : set2) {
                                XposedBridge.log(stringparam.getKey() + ";" + stringparam.getValue());
                            }

                            Set<Map.Entry<String, Integer>> set3 = actions.get(i).getIntParameters().entrySet();
                            for (Map.Entry<String, Integer> intparam : set3) {
                                XposedBridge.log(intparam.getKey() + ";" + intparam.getValue());
                            }
                        }
                    }
                    else
                        XposedBridge.log("obj is null");


                    newNotificationMode = (int)param.args[0];
                    settings.edit().putInt("currentPosition", newNotificationMode).apply();

                    Set<String> emptySet = Collections.emptySet();
                    setResultNull = false;
                    ArrayList<SliderAction> positionActions = null;

                    if(newNotificationMode == settings.getInt("SliderIsOnTop", 0)){
                        XposedBridge.log("newNotificationMode=TotalSilenceZenValOxygen" + newNotificationMode);
                        positionActions = getActionsForPosition("topPosition", settings);
                    }
                    else if(newNotificationMode == settings.getInt("SliderIsOnMid", 0)){
                        XposedBridge.log("newNotificationMode=PriorityZenValOxygen" + newNotificationMode);
                        positionActions = getActionsForPosition("midPosition", settings);
                    }
                    else if(newNotificationMode == settings.getInt("SliderIsOnBot", 0)){
                        XposedBridge.log("newNotificationMode=AllNotificationZenValOxygen" + newNotificationMode);
                        positionActions = getActionsForPosition("botPosition", settings);
                    }

                    boolean oneOfTheZenModeSwitches = false;

                    for(SliderAction s : positionActions){
                        if (s.getId().equalsIgnoreCase(HookUtils.MyEnum.ALL_NOTIFICATIONS.toString())){
                            oneOfTheZenModeSwitches = true;
                            param.args[0] = HookUtils.AllNotificationZenVal;
                        }
                        else if (s.getId().equalsIgnoreCase(HookUtils.MyEnum.PRIORITY.toString())){
                            oneOfTheZenModeSwitches = true;
                            param.args[0] = HookUtils.PriorityZenVal;
                        }
                        else if (s.getId().equalsIgnoreCase(HookUtils.MyEnum.ALARMS_ONLY.toString())){
                            oneOfTheZenModeSwitches = true;
                            param.args[0] = HookUtils.AlarmsOnlyZenVal;
                        }
                        else if (s.getId().equalsIgnoreCase(HookUtils.MyEnum.TOTAL_SILENCE.toString())){
                            oneOfTheZenModeSwitches = true;
                            param.args[0] = HookUtils.TotalSilenceZenVal;
                        }
                    }
                    if (!oneOfTheZenModeSwitches){
                        Vibrator v = (Vibrator) AndroidAppHelper.currentApplication().getSystemService(Context.VIBRATOR_SERVICE);
                        v.vibrate(50);
                        param.setResult(null);
                        setResultNull = true;
                    }
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    SharedPreferences settings = new RemotePreferences(AndroidAppHelper.currentApplication(), "com.dev.weathon.customalertslider", "com.dev.weathon.customalertslider_preferences");

                    if (!settings.getBoolean("comingFromBoot", true)){
                        ArrayList<SliderAction> TopPositionActions = getActionsForPosition("topPosition", settings);
                        ArrayList<SliderAction> MidPositionActions = getActionsForPosition("midPosition", settings);
                        ArrayList<SliderAction> BotPositionActions = getActionsForPosition("botPosition", settings);


                        if (newNotificationMode == settings.getInt("SliderIsOnTop", 0))
                            HookUtils.activateStates(AndroidAppHelper.currentApplication(), TopPositionActions);
                        else if (newNotificationMode == settings.getInt("SliderIsOnMid", 0))
                            HookUtils.activateStates(AndroidAppHelper.currentApplication(), MidPositionActions);
                        else if (newNotificationMode == settings.getInt("SliderIsOnBot", 0))
                            HookUtils.activateStates(AndroidAppHelper.currentApplication(), BotPositionActions);
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

    private ArrayList<SliderAction> getActionsForPosition(String pos, SharedPreferences settings){
        Gson gson = new Gson();
        String json = settings.getString(pos, "");
        SliderPositionValue obj = gson.fromJson(json, SliderPositionValue.class);
        if (obj != null) {
            ArrayList<SliderAction> actions = obj.getActions();
            return actions;
        }
        return new ArrayList<SliderAction>();
    }
}
