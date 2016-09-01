package com.dev.weathon.customalertslider.hooks;


import android.app.AndroidAppHelper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.UserHandle;
import android.util.Log;

import com.crossbowffs.remotepreferences.RemotePreferences;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
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

    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        if(!lpparam.packageName.equals("android"))
            return;

        findAndHookMethod("com.android.server.OemExService", lpparam.classLoader, "sendBroadcastForZenModeChanged",int.class, boolean.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                SharedPreferences settings = new RemotePreferences(AndroidAppHelper.currentApplication(), "com.dev.weathon.customalertslider", "com.dev.weathon.customalertslider_preferences");

                if (settings.getBoolean("comingFromBoot", false)){
                    if (!settings.getBoolean("extremeCustomization", false)){
                        settings.edit().putBoolean("comingFromBoot", false).apply();
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
                        XposedBridge.log("set notification value: " + settings.getBoolean("extendedZenModeControl", true) + settings.getInt("extendedZenModeControlZenMode", 1));
                        Log.w("CustomAlertSlider", "CustomSlider: set notification always to ALL");

                        if(settings.getBoolean("extendedZenModeControl", true)) {
                            if ((int)param.args[0] == 3)
                                param.args[0] = 2;
                        }
                        else
                            param.args[0] = 3;

                    }
                }
            }
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                SharedPreferences settings = new RemotePreferences(AndroidAppHelper.currentApplication(), "com.dev.weathon.customalertslider", "com.dev.weathon.customalertslider_preferences");

                if (settings.getBoolean("comingFromBoot", false)) {
                    if (settings.getBoolean("extremeCustomization", false)) {
                        settings.edit().putBoolean("comingFromBoot", false).apply();

                        if(settings.getBoolean("extendedZenModeControl", true)){
                            Intent intentZenMode = new Intent(TRI_STATE_KEY_INTENT);
                            intentZenMode.putExtra(TRI_STATE_KEY_INTENT_EXTRA, settings.getInt("extendedZenModeControlZenMode", 3));
                            Constructor newUserHandle = XposedHelpers.findConstructorBestMatch(UserHandle.class, int.class);
                            try {
                                UserHandle userhandle = (UserHandle) newUserHandle.newInstance(-1);
                                AndroidAppHelper.currentApplication().sendBroadcastAsUser(intentZenMode, userhandle);
                            } catch (InstantiationException e) {
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });
    }
}
