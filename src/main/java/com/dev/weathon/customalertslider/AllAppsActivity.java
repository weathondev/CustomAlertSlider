package com.dev.weathon.customalertslider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

public class AllAppsActivity extends ListActivity {
    private PackageManager packageManager = null;
    private List<ApplicationInfo> applist = null;
    private ApplicationAdapter listadaptor = null;
    private String selectedKey;
    private String selectedValue;
    private String positionKey;
    private String actionKey;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appdrawerlist);
        packageManager = getPackageManager();


        Intent callingIntent = getIntent();
        selectedKey = callingIntent.getStringExtra("selectedKey");
        selectedValue = callingIntent.getStringExtra("selectedValue");
        positionKey = callingIntent.getStringExtra("positionKey");
        actionKey = callingIntent.getStringExtra("actionKey");
        setTitle(selectedValue);

        SharedPreferences settings  = getSharedPreferences("com.dev.weathon.customalertslider_preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = settings.getString(positionKey, "");
        SliderPositionValue obj = gson.fromJson(json, SliderPositionValue.class);
        SliderAction action = null;
        if (obj != null){
            ArrayList<SliderAction> actions = obj.getActions();
            for (SliderAction a: actions) {
                if(a.getId().equalsIgnoreCase(actionKey))
                    action = a;
            }
        }

        new LoadApplications().execute();


    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        ApplicationInfo app = applist.get(position);
        getSharedPreferences("com.dev.weathon.customalertslider_preferences", MODE_PRIVATE).edit().putString(getIntent().getStringExtra("preferenceKey") + "_app", app.packageName).apply();
        Intent returnIntent = new Intent();
        returnIntent.putExtra("apptostart", app.packageName);
        returnIntent.putExtra("selectedKey", selectedKey);
        returnIntent.putExtra("selectedValue", selectedValue);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    private List<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list) {
        ArrayList<ApplicationInfo> applist = new ArrayList<ApplicationInfo>();
        for (ApplicationInfo info : list) {
            try {
                if (null != packageManager.getLaunchIntentForPackage(info.packageName)) {
                    applist.add(info);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Collections.sort(applist, new ApplicationInfo.DisplayNameComparator(packageManager));

        return applist;
    }

    private class LoadApplications extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progress = null;

        @Override
        protected Void doInBackground(Void... params) {
            applist = checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));
            listadaptor = new ApplicationAdapter(AllAppsActivity.this,
                    R.layout.appdrawerlistview, applist);

            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Void result) {
            setListAdapter(listadaptor);
            progress.dismiss();
            String prevSelectedApp = getSharedPreferences("com.dev.weathon.customalertslider_preferences", MODE_PRIVATE).getString(getIntent().getStringExtra("preferenceKey") + "_app", "");

            getListView().setItemChecked(listadaptor.getPosition(prevSelectedApp), true);
            getListView().setSelection(listadaptor.getPosition(prevSelectedApp));


            /*
            LinearLayout linearLayout = (LinearLayout) getViewByPosition(listadaptor.getPosition(prevSelectedApp), getListView());
            linearLayout.setBackgroundColor(Color.BLUE);


            int count = linearLayout.getChildCount();
            View v = null;
            for(int i=0; i<count; i++) {
                v = linearLayout.getChildAt(i);
                v.setBackgroundColor(Color.BLUE);
                if (v instanceof LinearLayout){
                    LinearLayout l = (LinearLayout) v;
                    View v2 = null;
                    int count2 = l.getChildCount();
                    for(int i2=0; i2<count2; i2++){
                        v2 = l.getChildAt(i2);
                        v2.setBackgroundColor(Color.BLUE);
                    }
                }

            }
            */

            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(AllAppsActivity.this, null,
                    "Loading application info...");
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }
}