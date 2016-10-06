package com.dev.weathon.customalertslider.hooks.oxygen;


import android.app.AndroidAppHelper;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.UserHandle;
import android.os.Vibrator;
import android.util.Log;

import com.crossbowffs.remotepreferences.RemotePreferences;
import com.dev.weathon.customalertslider.HookUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
 * Reverses the slider when booting up
 */
public class HookReverseSliderAtBoot implements IXposedHookLoadPackage {

    public static final String TRI_STATE_KEY_INTENT = "com.oem.intent.action.THREE_KEY_MODE";
    public static final String TRI_STATE_KEY_INTENT_EXTRA = "switch_state";
    private int notificationModeToChange = 2;

    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        if(!lpparam.packageName.equals("android"))
            return;

        XSharedPreferences settings = new XSharedPreferences("com.dev.weathon.customalertslider", "bootPreferences");
        settings.makeWorldReadable();
        String usedOS = settings.getString("usedOS", "oxygen");

        if (usedOS.equals("oxygen")) {
            findAndHookMethod("com.android.server.OemExService", lpparam.classLoader, "sendBroadcastForZenModeChanged", int.class, boolean.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    SharedPreferences settings = new RemotePreferences(AndroidAppHelper.currentApplication(), "com.dev.weathon.customalertslider", "com.dev.weathon.customalertslider_preferences");

                    if (settings.getBoolean("comingFromBoot", false)) {
                        int newNotificationMode = (int) param.args[0];
                        sendSliderChangeIntent(AndroidAppHelper.currentApplication(), newNotificationMode, true);

                        Set<String> emptySet = Collections.emptySet();

                        if (newNotificationMode == HookUtils.TotalSilenceZenValOxygen) {
                            XposedBridge.log("newNotificationMode=TotalSilenceZenValOxygen" + newNotificationMode);
                            ArrayList<HookUtils.MyEnum> TopPositionActions = new ArrayList<HookUtils.MyEnum>();
                            for (String s : settings.getStringSet("topPosition", emptySet))
                                TopPositionActions.add(HookUtils.MyEnum.valueOf(s));

                            if (TopPositionActions.contains(HookUtils.MyEnum.ALL_NOTIFICATIONS))
                                notificationModeToChange = HookUtils.AllNotificationZenValOxygen;
                            else if (TopPositionActions.contains(HookUtils.MyEnum.PRIORITY))
                                notificationModeToChange = HookUtils.PriorityZenValOxygen;
                            else if (TopPositionActions.contains(HookUtils.MyEnum.ALARMS_ONLY) || TopPositionActions.contains(HookUtils.MyEnum.TOTAL_SILENCE))
                                notificationModeToChange = HookUtils.TotalSilenceZenValOxygen;
                            else if (!settings.getBoolean("extendedZenModeControl", false))
                                param.setResult(null);

                        } else if (newNotificationMode == HookUtils.PriorityZenValOxygen) {
                            XposedBridge.log("newNotificationMode=PriorityZenValOxygen" + newNotificationMode);
                            ArrayList<HookUtils.MyEnum> MidPositionActions = new ArrayList<HookUtils.MyEnum>();
                            for (String s : settings.getStringSet("midPosition", emptySet))
                                MidPositionActions.add(HookUtils.MyEnum.valueOf(s));

                            if (MidPositionActions.contains(HookUtils.MyEnum.ALL_NOTIFICATIONS))
                                notificationModeToChange = HookUtils.AllNotificationZenValOxygen;
                            else if (MidPositionActions.contains(HookUtils.MyEnum.PRIORITY))
                                notificationModeToChange = HookUtils.PriorityZenValOxygen;
                            else if (MidPositionActions.contains(HookUtils.MyEnum.ALARMS_ONLY) || MidPositionActions.contains(HookUtils.MyEnum.TOTAL_SILENCE))
                                notificationModeToChange = HookUtils.TotalSilenceZenValOxygen;
                            else if (!settings.getBoolean("extendedZenModeControl", false))
                                param.setResult(null);

                        } else if (newNotificationMode == HookUtils.AllNotificationZenValOxygen) {
                            XposedBridge.log("newNotificationMode=AllNotificationZenValOxygen" + newNotificationMode);
                            ArrayList<HookUtils.MyEnum> BotPositionActions = new ArrayList<HookUtils.MyEnum>();
                            for (String s : settings.getStringSet("botPosition", emptySet))
                                BotPositionActions.add(HookUtils.MyEnum.valueOf(s));

                            if (BotPositionActions.contains(HookUtils.MyEnum.ALL_NOTIFICATIONS))
                                notificationModeToChange = HookUtils.AllNotificationZenValOxygen;
                            else if (BotPositionActions.contains(HookUtils.MyEnum.PRIORITY))
                                notificationModeToChange = HookUtils.PriorityZenValOxygen;
                            else if (BotPositionActions.contains(HookUtils.MyEnum.ALARMS_ONLY) || BotPositionActions.contains(HookUtils.MyEnum.TOTAL_SILENCE))
                                notificationModeToChange = HookUtils.TotalSilenceZenValOxygen;
                            else if (!settings.getBoolean("extendedZenModeControl", false))
                                    param.setResult(null);
                        }

                        if (settings.getBoolean("extendedZenModeControl", false)){
                            notificationModeToChange = settings.getInt("extendedZenModeControlZenMode", 3);
                        }
                    }
                }

                @Override
                protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                    SharedPreferences settings = new RemotePreferences(AndroidAppHelper.currentApplication(), "com.dev.weathon.customalertslider", "com.dev.weathon.customalertslider_preferences");

                    if (settings.getBoolean("comingFromBoot", false)) {
                        settings.edit().putBoolean("comingFromBoot", false).apply();

                        final Intent intentZenMode = new Intent(TRI_STATE_KEY_INTENT);
                        final int paramargs0 = notificationModeToChange;
                        intentZenMode.putExtra(TRI_STATE_KEY_INTENT_EXTRA, paramargs0);
                        Constructor newUserHandle = XposedHelpers.findConstructorBestMatch(UserHandle.class, int.class);
                        try {
                            final UserHandle userhandle = (UserHandle) newUserHandle.newInstance(-1);

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    XposedBridge.log("setZen to " + paramargs0);
                                    AndroidAppHelper.currentApplication().sendBroadcastAsUser(intentZenMode, userhandle);
                                }
                            }, 500);


                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
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
