package com.dev.weathon.customalertslider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * Created by Joshua on 19.10.2016.
 */
public class DynamicSubActivityScreenBrightness extends AppCompatActivity {

    private Spinner spinner;
    private SeekBar seekBar;
    private String selectedKey;
    private String selectedValue;
    private String positionKey;
    private String actionKey;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_combo_slider);

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

        spinner = (Spinner) findViewById(R.id.spinnerSimpleCombo);
        seekBar = (SeekBar) findViewById(R.id.spinnerSimpleSeekbar);
        seekBar.setMax(255);
        seekBar.setVisibility(View.INVISIBLE);
        String[] values = null;
        String[] titles = null;

        if (getResources().getString(R.string.SCREEN_BRIGHTNESS).equals(selectedKey)){
            values = getResources().getStringArray(R.array.screen_brightness_values);
            titles = getResources().getStringArray(R.array.screen_brightness_titles);
        }

        ArrayList<SimpleKeyValue> simpleKeyValueList = new ArrayList<>();
        int i = 0;

        for (String s: values) {
            simpleKeyValueList.add(new SimpleKeyValue(values[i], titles[i]));
            i++;
        }
        ArrayAdapter<SimpleKeyValue> adapter = new ArrayAdapter<SimpleKeyValue>(this, R.layout.support_simple_spinner_dropdown_item, simpleKeyValueList);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (((SimpleKeyValue)spinner.getSelectedItem()).getId().equals(getResources().getString(R.string.screen_brightness_level))){
                    seekBar.setVisibility(View.VISIBLE);
                }
                else{
                    seekBar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (action != null){
            Set<Map.Entry<String, String>> set2 = action.getStringParameters().entrySet();
            String selectedKey = "";
            for (Map.Entry<String, String> stringparam : set2) {
                if (stringparam.getKey().equalsIgnoreCase("mode")){
                    selectedKey = stringparam.getValue();
                }
            }

            Set<Map.Entry<String, Integer>> set = action.getIntParameters().entrySet();
            for (Map.Entry<String, Integer> intParam : set) {
                if (intParam.getKey().equalsIgnoreCase("brightness_level")){
                    seekBar.setProgress(intParam.getValue());
                }
            }

            for (SimpleKeyValue keyVal : simpleKeyValueList) {
                if (keyVal.getId().equalsIgnoreCase(selectedKey)){
                    spinner.setSelection(simpleKeyValueList.indexOf(keyVal));
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
        returnIntent.putExtra("brightness_level", seekBar.getProgress());
        returnIntent.putExtra("actionKey", actionKey);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
