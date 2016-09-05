package com.dev.weathon.customalertslider.hooks.oxygen;


import android.app.AndroidAppHelper;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.crossbowffs.remotepreferences.RemotePreferences;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LayoutInflated;

/**
 * Created by Joshua on 05.08.2016.
 */
public class HookExtendedVolumeControlLayout implements IXposedHookInitPackageResources{

    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) throws Throwable {
        if (!resparam.packageName.equals("com.android.systemui"))
            return;

        XSharedPreferences settings = new XSharedPreferences("com.dev.weathon.customalertslider", "bootPreferences");
        settings.makeWorldReadable();
        String usedOS = settings.getString("usedOS", "oxygen");

        if (usedOS.equals("oxygen")) {
            resparam.res.hookLayout("com.android.systemui", "layout", "volume_zen_footer", new XC_LayoutInflated() {
                @SuppressWarnings("ResourceType")
                @Override
                public void handleLayoutInflated(LayoutInflatedParam liparam) throws Throwable {
                    SharedPreferences settings = new RemotePreferences(AndroidAppHelper.currentApplication(), "com.dev.weathon.customalertslider", "com.dev.weathon.customalertslider_preferences");
                    boolean extendedZenModeControl = settings.getBoolean("extendedZenModeControl", true);

                    if (extendedZenModeControl) {

                        ImageView settingsIcon = (ImageView) liparam.view.findViewById(liparam.res.getIdentifier("volume_zen_settings_icon", "id", "com.android.systemui"));
                        LinearLayout linearLayoutZenFooter = (LinearLayout) settingsIcon.getParent().getParent();

                        LinearLayout linearLayoutModeChange = new LinearLayout(liparam.view.getContext());
                        LinearLayout.LayoutParams linearLayoutModeChangeLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        linearLayoutModeChangeLayoutParams.setMargins(15, 10, 15, 5);
                        linearLayoutModeChange.setLayoutParams(linearLayoutModeChangeLayoutParams);
                        linearLayoutModeChange.setOrientation(LinearLayout.HORIZONTAL);
                        linearLayoutModeChange.setGravity(Gravity.CENTER);

                        TextView textViewSilent = new TextView(liparam.view.getContext());
                        textViewSilent.setId(999);
                        textViewSilent.setLayoutParams(new TableLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
                        textViewSilent.setText(settings.getString("extendedVolumeControlSilentText", "SILENT"));
                        textViewSilent.setTextSize(16);
                        textViewSilent.setTypeface(null, Typeface.BOLD);
                        textViewSilent.setClickable(true);
                        textViewSilent.setTextColor(Color.WHITE);
                        textViewSilent.setGravity(Gravity.CENTER);

                        TextView textViewPriority = new TextView(liparam.view.getContext());
                        textViewPriority.setId(998);
                        textViewPriority.setLayoutParams(new TableLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
                        textViewPriority.setText(settings.getString("extendedVolumeControlPriorityText", "PRIORITY"));
                        textViewPriority.setTextSize(16);
                        textViewPriority.setTypeface(null, Typeface.BOLD);
                        textViewPriority.setClickable(true);
                        textViewPriority.setTextColor(Color.WHITE);
                        textViewPriority.setGravity(Gravity.CENTER);

                        TextView textViewAll = new TextView(liparam.view.getContext());
                        textViewAll.setId(997);
                        textViewAll.setLayoutParams(new TableLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
                        textViewAll.setText(settings.getString("extendedVolumeControlAllNotText", "ALL"));
                        textViewAll.setTextSize(16);
                        textViewAll.setTypeface(null, Typeface.BOLD);
                        textViewAll.setClickable(true);
                        textViewAll.setTextColor(Color.WHITE);
                        textViewAll.setGravity(Gravity.CENTER);


                        linearLayoutModeChange.addView(textViewSilent);
                        linearLayoutModeChange.addView(textViewPriority);
                        linearLayoutModeChange.addView(textViewAll);
                        linearLayoutZenFooter.addView(linearLayoutModeChange, 0);
                    }
                }
            });
        }
    }
}
