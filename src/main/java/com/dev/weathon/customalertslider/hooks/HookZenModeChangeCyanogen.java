package com.dev.weathon.customalertslider.hooks;


import android.app.AndroidAppHelper;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import com.crossbowffs.remotepreferences.RemotePreferences;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/**
 * Created by Joshua on 05.08.2016.
 */
public class HookZenModeChangeCyanogen implements IXposedHookLoadPackage {

    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        if(!lpparam.packageName.equals("android"))
            return;

        findAndHookMethod("com.android.server.notification.ZenModeHelper", lpparam.classLoader, "setManualZenMode", int.class, Uri.class, String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                SharedPreferences settings = new RemotePreferences(AndroidAppHelper.currentApplication(),"com.dev.weathon.customalertslider", "com.dev.weathon.customalertslider_preferences");
                boolean hideToasts = settings.getBoolean("hideToast",false);

                //if (hideToasts) {
                //    Log.w("CustomAlertSlider", "hook that ZEN Toast! HOOK HOOK");
                //    param.setResult(param);
                //}
                XposedBridge.log("hideToasts: " + hideToasts + " params: " + param.args[0] + " " + param.args[1] + " " + param.args[2]);
                param.setResult(null);
            }

        });



    }
}
