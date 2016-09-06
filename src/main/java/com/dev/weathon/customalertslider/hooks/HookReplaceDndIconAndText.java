package com.dev.weathon.customalertslider.hooks;

import android.content.res.XResources;
import android.graphics.drawable.Drawable;

import com.dev.weathon.customalertslider.R;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;

/**
 * Replaces the dnd icon & text when in vibrate mode
 */
public class HookReplaceDndIconAndText implements IXposedHookInitPackageResources {

    @Override
    public void handleInitPackageResources(final XC_InitPackageResources.InitPackageResourcesParam resparam) throws Throwable {
        if (!resparam.packageName.equals("com.android.systemui"))
            return;

        XSharedPreferences settings = new XSharedPreferences("com.dev.weathon.customalertslider", "bootPreferences");
        settings.makeWorldReadable();
        String usedOS = settings.getString("usedOS", "oxygen");
        boolean vibrateInsteadPriority = settings.getBoolean("vibrateInsteadPriority", false);
        String replaceText = settings.getString("vibrateModeText", "Vibration");

        if (usedOS.equals("oxygen")) {
            if (vibrateInsteadPriority) {
                resparam.res.setReplacement("com.android.systemui", "string", "zen_important_interruptions", replaceText);
                resparam.res.setReplacement("com.android.systemui", "drawable", "stat_sys_dnd_24", new XResources.DrawableLoader() {
                    @Override
                    public Drawable newDrawable(XResources res, int id) throws Throwable {
                        int drawableId = resparam.res.getIdentifier("ic_volume_ringer_vibrate", "drawable", "com.android.systemui");
                        return resparam.res.getDrawable(drawableId, null);
                    }
                });
            }
        }
        else if (usedOS.equals("cyanogen")){
            if (vibrateInsteadPriority) {
                resparam.res.setReplacement("com.android.systemui", "string", "interruption_level_priority", replaceText);
                resparam.res.setReplacement("com.android.systemui", "string", "quick_settings_dnd_priority_label", replaceText);
                resparam.res.setReplacement("com.android.systemui", "drawable", "ic_dnd", new XResources.DrawableLoader() {
                    @Override
                    public Drawable newDrawable(XResources res, int id) throws Throwable {
                        int drawableId = resparam.res.getIdentifier("ic_volume_ringer_vibrate", "drawable", "com.android.systemui");
                        return resparam.res.getDrawable(drawableId, null);
                    }
                });
                resparam.res.setReplacement("com.android.systemui", "drawable", "ic_qs_dnd_on_priority", new XResources.DrawableLoader() {
                    @Override
                    public Drawable newDrawable(XResources res, int id) throws Throwable {
                        int drawableId = resparam.res.getIdentifier("ic_volume_ringer_vibrate", "drawable", "com.android.systemui");
                        return resparam.res.getDrawable(drawableId, null);
                    }
                });
            }
        }
    }

}
