package com.dev.weathon.customalertslider.tasker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.dev.weathon.customalertslider.HookUtils;
import com.dev.weathon.customalertslider.tasker.TaskerPlugin;
import com.dev.weathon.customalertslider.tasker.activity.EditActivity;
import com.dev.weathon.customalertslider.tasker.bundle.SliderState;


public final class QueryReceiver extends BroadcastReceiver {
    private static final String SLIDER_CHANGED_INTENT = "com.dev.weathon.customalertslider.SLIDER_CHANGED";
    protected static final Intent INTENT_REQUEST_REQUERY =
            new Intent(com.twofortyfouram.locale.Intent.ACTION_REQUEST_QUERY).putExtra(com.twofortyfouram.locale.Intent.EXTRA_ACTIVITY,
                    EditActivity.class.getName());

    private static SliderState currentState = SliderState.BOTTOM;
    private static boolean comingFromBoot = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("CustomAlertSlider", "received event query");


        if(SLIDER_CHANGED_INTENT.equals(intent.getAction())) {
            int stateValue = intent.getIntExtra("state", 0);

            Log.d("CustomAlertSlider", "received new slider state: " + stateValue);

            switch(stateValue) {
                case HookUtils.TotalSilenceZenValOxygen:
                    currentState = SliderState.TOP;
                    break;
                case HookUtils.PriorityZenValOxygen:
                    currentState = SliderState.MIDDLE;
                    break;
                case HookUtils.AllNotificationZenValOxygen:
                    currentState = SliderState.BOTTOM;
                    break;
            }
            comingFromBoot = intent.getBooleanExtra("comingFromBoot", false);

            TaskerPlugin.Event.addPassThroughMessageID(INTENT_REQUEST_REQUERY);
            context.sendBroadcast(INTENT_REQUEST_REQUERY);
        } else {

            if (!com.twofortyfouram.locale.Intent.ACTION_QUERY_CONDITION.equals(intent.getAction())) {
                Log.e("CustomAlertSlider", "invalid intent: " + intent.getAction() + " received");
                return;
            }

            final Bundle bundle = intent.getBundleExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE);

            if (TaskerPlugin.Condition.hostSupportsVariableReturn(intent.getExtras())) {
                Bundle varsBundle = new Bundle();

                varsBundle.putString("%pstate", currentState.name());
                varsBundle.putString("%frombootup", comingFromBoot ? "true" : "false");
                TaskerPlugin.addVariableBundle(getResultExtras(true), varsBundle);
            }

            Log.d("CustomAlertSlider", "send result: " + currentState + " coming from boot: " + comingFromBoot);
            setResultCode(com.twofortyfouram.locale.Intent.RESULT_CONDITION_SATISFIED);
        }
    }
}