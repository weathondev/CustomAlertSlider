package com.dev.weathon.customalertslider.hooks;

import android.app.AndroidAppHelper;
import android.content.SharedPreferences;

import com.crossbowffs.remotepreferences.RemotePreferences;
import com.dev.weathon.customalertslider.HookUtils;
import com.dev.weathon.customalertslider.SliderAction;
import com.dev.weathon.customalertslider.SliderPositionValue;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class HookChargingLED implements IXposedHookLoadPackage {
    class HookChargingLEDSubclass extends XC_MethodHook {
        HookChargingLEDSubclass() {
        }

        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            try
            {
                XposedBridge.log("Hook Charging LED");
                SharedPreferences settings = new RemotePreferences(AndroidAppHelper.currentApplication(), "com.dev.weathon.customalertslider", "com.dev.weathon.customalertslider_preferences");
                int zenValue = settings.getInt("currentPosition", 1);
                ArrayList<SliderAction> positionActions = null;

                String usedOS = settings.getString("usedOS", "oxygen");

                if (usedOS.equals("oxygen")) {
                    if(zenValue == HookUtils.TotalSilenceZenValOxygen){
                        positionActions = getActionsForPosition("topPositionObject", settings);
                    }
                    else if(zenValue == HookUtils.PriorityZenValOxygen){
                        positionActions = getActionsForPosition("midPositionObject", settings);
                    }
                    else if(zenValue == HookUtils.AllNotificationZenValOxygen){
                        positionActions = getActionsForPosition("botPositionObject", settings);
                    }
                }
                else{
                    if(zenValue == settings.getInt("SliderIsOnTop", 0)){
                        positionActions = getActionsForPosition("topPositionObject", settings);
                    }
                    else if(zenValue == settings.getInt("SliderIsOnMid", 0)){
                        positionActions = getActionsForPosition("midPositionObject", settings);
                    }
                    else if(zenValue == settings.getInt("SliderIsOnBot", 0)){
                        positionActions = getActionsForPosition("botPositionObject", settings);
                    }
                }





                boolean blockTheLED = false;
                for(SliderAction s : positionActions){
                    if (s.getId().equalsIgnoreCase(HookUtils.MyEnum.CHARGING_LED.toString())){
                        for (Map.Entry<String, String> prm : s.getStringParameters().entrySet()) {
                            if (prm.getKey().equalsIgnoreCase("mode")){
                                if (prm.getValue().equalsIgnoreCase("ALLOW_LED"))
                                    blockTheLED = false;
                                else if (prm.getValue().equalsIgnoreCase("DENY_LED"))
                                    blockTheLED = true;
                            }
                        }
                    }
                }

                if (blockTheLED){
                    param.setResult(null);
                    Object mBatteryLight = XposedHelpers.getObjectField(param.thisObject, "mBatteryLight");
                    XposedHelpers.callMethod(mBatteryLight, "turnOff", new Object[0]);
                }
            } catch (Exception e) {
            }
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



    public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals("android")) {
            try {
                //XposedHelpers.findAndHookMethod("com.android.server.BatteryService$Led", lpparam.classLoader, "updateLightsLocked", new Object[]{new HookChargingLEDSubclass()});
            } catch (Exception e) {}
        }
    }
}
