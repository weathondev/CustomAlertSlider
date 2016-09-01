package com.dev.weathon.customalertslider.hooks;

import android.content.res.XResources;
import android.graphics.drawable.Drawable;

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
        boolean extremeCustomization = settings.getBoolean("extremeCustomization", false);
        boolean vibrateInsteadPriority = settings.getBoolean("vibrateInsteadPriority", false);
        String replaceText = settings.getString("vibrateModeText", "Vibration");


        if (!extremeCustomization && vibrateInsteadPriority){
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

}
