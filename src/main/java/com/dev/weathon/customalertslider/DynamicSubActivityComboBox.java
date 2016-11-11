package com.dev.weathon.customalertslider;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * Created by Joshua on 19.10.2016.
 */
public class DynamicSubActivityComboBox extends AppCompatActivity {

    private Spinner spinner;
    private String selectedKey;
    private String selectedValue;
    private String positionKey;
    private String actionKey;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_combo);
        spinner = (Spinner) findViewById(R.id.spinnerSimpleCombo);

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

        String[] values = null;
        String[] titles = null;

        if (getResources().getString(R.string.SCREEN_ORIENTATION).equals(selectedKey)){
            values = getResources().getStringArray(R.array.orientation_values);
            titles = getResources().getStringArray(R.array.orientation_titles);
        }
        else if (getResources().getString(R.string.PREFER_NETWORK).equals(selectedKey)){
            values = getResources().getStringArray(R.array.network_values);
            titles = getResources().getStringArray(R.array.network_titles);
        }
        else if (getResources().getString(R.string.GPS).equals(selectedKey)){
            values = getResources().getStringArray(R.array.gps_values);
            titles = getResources().getStringArray(R.array.gps_titles);
        }
        else if (getResources().getString(R.string.LOCK_SCREEN_NOTIFICATION).equals(selectedKey)){
            values = getResources().getStringArray(R.array.lock_screen_notification_values);
            titles = getResources().getStringArray(R.array.lock_screen_notification_titles);
        }
        else if (getResources().getString(R.string.BATTERY_SAVING_AUTOMATIC).equals(selectedKey)){
            values = getResources().getStringArray(R.array.battery_saver_automatic_values);
            titles = getResources().getStringArray(R.array.battery_saver_automatic_titles);
        }
        else{
            values = getResources().getStringArray(R.array.onOff_values);
            titles = getResources().getStringArray(R.array.onOff_titles);
        }

        ArrayList<SimpleKeyValue> simpleKeyValueList = new ArrayList<>();
        int i = 0;

        for (String s: values) {
            simpleKeyValueList.add(new SimpleKeyValue(values[i], titles[i]));
            i++;
        }
        ArrayAdapter<SimpleKeyValue> adapter = new ArrayAdapter<SimpleKeyValue>(this, R.layout.support_simple_spinner_dropdown_item, simpleKeyValueList);
        spinner.setAdapter(adapter);


        if (action != null){ //no entry yet

            Set<Map.Entry<String, String>> set2 = action.getStringParameters().entrySet();
            String selectedKey = "";
            for (Map.Entry<String, String> stringparam : set2) {
                if (stringparam.getKey().equalsIgnoreCase("mode")){
                    selectedKey = stringparam.getValue();
                }
            }

            for (SimpleKeyValue keyVal : simpleKeyValueList) {
                if (keyVal.getId().equalsIgnoreCase(selectedKey)){
                    spinner.setSelection(simpleKeyValueList.indexOf(keyVal), false);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        onSavePositionClicked(null);
    }

    public void onSavePositionClicked(View v){
        Intent returnIntent = new Intent();
        returnIntent.putExtra("selectedKey", ((SimpleKeyValue)spinner.getSelectedItem()).getId());
        returnIntent.putExtra("selectedValue", ((SimpleKeyValue)spinner.getSelectedItem()).getName());
        returnIntent.putExtra("actionKey", actionKey);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
