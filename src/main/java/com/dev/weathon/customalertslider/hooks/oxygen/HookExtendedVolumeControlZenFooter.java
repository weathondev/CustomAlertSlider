package com.dev.weathon.customalertslider.hooks.oxygen;


import android.app.AndroidAppHelper;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.UserHandle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crossbowffs.remotepreferences.RemotePreferences;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findConstructorBestMatch;

/**
 * Created by Joshua on 05.08.2016.
 */
public class HookExtendedVolumeControlZenFooter implements IXposedHookLoadPackage {

    public static final String TRI_STATE_KEY_INTENT = "com.oem.intent.action.THREE_KEY_MODE";
    public static final String TRI_STATE_KEY_INTENT_EXTRA = "switch_state";

    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        if(!lpparam.packageName.equals("com.android.systemui"))
            return;

        XSharedPreferences settings = new XSharedPreferences("com.dev.weathon.customalertslider", "bootPreferences");
        settings.makeWorldReadable();
        String usedOS = settings.getString("usedOS", "oxygen");

        if (usedOS.equals("oxygen")) {
            findAndHookMethod("com.android.systemui.volume.ZenFooter", lpparam.classLoader, "init", "com.android.systemui.volume.VolumeDialog", "com.android.systemui.statusbar.policy.ZenModeController", "com.android.systemui.SystemUI", new XC_MethodHook() {
                @SuppressWarnings("ResourceType")
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    final SharedPreferences settings = new RemotePreferences(AndroidAppHelper.currentApplication(), "com.dev.weathon.customalertslider", "com.dev.weathon.customalertslider_preferences");
                    boolean extendedZenModeControl = settings.getBoolean("extendedZenModeControl", false);


                    if (extendedZenModeControl) {
                        TextView textViewSilent = (TextView) ((LinearLayout) param.thisObject).findViewById(999);
                        TextView textViewPriority = (TextView) ((LinearLayout) param.thisObject).findViewById(998);
                        TextView textViewAll = (TextView) ((LinearLayout) param.thisObject).findViewById(997);

                        final Context context = (Context) XposedHelpers.getObjectField(param.thisObject, "mContext");

                        textViewSilent.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intentZenMode = new Intent(TRI_STATE_KEY_INTENT);
                                intentZenMode.putExtra(TRI_STATE_KEY_INTENT_EXTRA, 1);
                                Constructor newUserHandle = findConstructorBestMatch(UserHandle.class, int.class);
                                try {
                                    UserHandle userhandle = (UserHandle) newUserHandle.newInstance(-1);
                                    settings.edit().putInt("extendedZenModeControlZenMode", 1).apply();
                                    context.sendBroadcastAsUser(intentZenMode, userhandle);
                                } catch (InstantiationException e) {
                                    e.printStackTrace();
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                } catch (InvocationTargetException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        textViewPriority.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intentZenMode = new Intent(TRI_STATE_KEY_INTENT);
                                intentZenMode.putExtra(TRI_STATE_KEY_INTENT_EXTRA, 2);
                                Constructor newUserHandle = findConstructorBestMatch(UserHandle.class, int.class);
                                try {
                                    UserHandle userhandle = (UserHandle) newUserHandle.newInstance(-1);
                                    settings.edit().putInt("extendedZenModeControlZenMode", 2).apply();
                                    context.sendBroadcastAsUser(intentZenMode, userhandle);
                                } catch (InstantiationException e) {
                                    e.printStackTrace();
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                } catch (InvocationTargetException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        textViewAll.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intentZenMode = new Intent(TRI_STATE_KEY_INTENT);
                                intentZenMode.putExtra(TRI_STATE_KEY_INTENT_EXTRA, 3);
                                Constructor newUserHandle = findConstructorBestMatch(UserHandle.class, int.class);
                                try {
                                    UserHandle userhandle = (UserHandle) newUserHandle.newInstance(-1);
                                    settings.edit().putInt("extendedZenModeControlZenMode", 3).apply();
                                    context.sendBroadcastAsUser(intentZenMode, userhandle);
                                } catch (InstantiationException e) {
                                    e.printStackTrace();
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                } catch (InvocationTargetException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }

            });
        }



    }
}
