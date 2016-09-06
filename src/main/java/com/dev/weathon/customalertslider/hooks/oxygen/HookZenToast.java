package com.dev.weathon.customalertslider.hooks.oxygen;


import android.app.AndroidAppHelper;
import android.content.SharedPreferences;
import android.util.Log;

import com.crossbowffs.remotepreferences.RemotePreferences;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/**
 * Created by Joshua on 05.08.2016.
 */
public class HookZenToast implements IXposedHookLoadPackage {

    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        if(!lpparam.packageName.equals("com.android.systemui"))
            return;

        XSharedPreferences settings = new XSharedPreferences("com.dev.weathon.customalertslider", "bootPreferences");
        settings.makeWorldReadable();
        String usedOS = settings.getString("usedOS", "oxygen");

        if (usedOS.equals("oxygen")) {
            findAndHookMethod("com.android.systemui.volume.ZenToast", lpparam.classLoader, "show", int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    SharedPreferences settings = new RemotePreferences(AndroidAppHelper.currentApplication(), "com.dev.weathon.customalertslider", "com.dev.weathon.customalertslider_preferences");
                    boolean hideToasts = settings.getBoolean("hideToast", false);

                    if (hideToasts) {
                        Log.w("CustomAlertSlider", "hook that ZEN Toast! HOOK HOOK");
                        param.setResult(param);
                    }
                }

            });
        }



    }
}
