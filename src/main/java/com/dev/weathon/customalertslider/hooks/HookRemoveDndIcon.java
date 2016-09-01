package com.dev.weathon.customalertslider.hooks;


import android.app.AndroidAppHelper;
import android.content.SharedPreferences;

import com.crossbowffs.remotepreferences.RemotePreferences;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import static de.robv.android.xposed.XposedHelpers.findAndHookConstructor;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/**
 * Removes the DnD Icon when in vibrate mode
 */
public class HookRemoveDndIcon implements IXposedHookLoadPackage {


    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {

        if(!lpparam.packageName.equals("com.android.systemui"))
            return;

        findAndHookMethod("com.android.systemui.statusbar.phone.PhoneStatusBarPolicy", lpparam.classLoader, "setZenMode", int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (((int)param.args[0]) == 2){
                    SharedPreferences settings = new RemotePreferences(AndroidAppHelper.currentApplication(), "com.dev.weathon.customalertslider", "com.dev.weathon.customalertslider_preferences");
                    if (!settings.getBoolean("extremeCustomization", false) && settings.getBoolean("vibrateInsteadPriority", false)){
                        param.args[0] = 10;
                    }
                }
            }
        });

    }



}
