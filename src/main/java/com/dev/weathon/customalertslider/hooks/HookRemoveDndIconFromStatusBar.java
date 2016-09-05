package com.dev.weathon.customalertslider.hooks;


import android.app.AndroidAppHelper;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;

import com.crossbowffs.remotepreferences.RemotePreferences;
import com.dev.weathon.customalertslider.HookUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import static de.robv.android.xposed.XposedHelpers.findAndHookConstructor;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/**
 * Removes the DnD Icon when in vibrate mode
 */
public class HookRemoveDndIconFromStatusBar implements IXposedHookLoadPackage {


    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        if(!lpparam.packageName.equals("com.android.systemui"))
            return;

        findAndHookMethod("com.android.systemui.statusbar.phone.PhoneStatusBarPolicy", lpparam.classLoader, "setZenMode", int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                SharedPreferences settings = new RemotePreferences(AndroidAppHelper.currentApplication(), "com.dev.weathon.customalertslider", "com.dev.weathon.customalertslider_preferences");
                String usedOS = settings.getString("usedOS", "oxygen");

                if (usedOS.equals("oxygen")) {
                    if (((int) param.args[0]) == 2) {
                        if (settings.getBoolean("vibrateInsteadPriority", false)) {
                            param.args[0] = 10;
                        }
                    }
                }
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                SharedPreferences settings = new RemotePreferences(AndroidAppHelper.currentApplication(), "com.dev.weathon.customalertslider", "com.dev.weathon.customalertslider_preferences");
                String usedOS = settings.getString("usedOS", "oxygen");

                if (usedOS.equals("cyanogen")){
                    if (((int) param.args[0]) == HookUtils.PriorityZenVal) {
                        if (settings.getBoolean("vibrateInsteadPriority", false)) {
                            Class statusbarManager = Class.forName("android.app.StatusBarManager");
                            final Method disable = statusbarManager.getDeclaredMethod("setIconVisibility", String.class, boolean.class);
                            final Object obj = param.thisObject;
                            disable.invoke(XposedHelpers.getObjectField(obj, "mService"), "zen", false);
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    try {
                                        disable.invoke(XposedHelpers.getObjectField(obj, "mService"), "zen", false);
                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                    } catch (InvocationTargetException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, 100);
                        }
                    }
                }
            }
        });
    }



}
