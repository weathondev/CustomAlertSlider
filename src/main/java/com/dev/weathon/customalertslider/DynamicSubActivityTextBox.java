package com.dev.weathon.customalertslider;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * Created by Joshua on 19.10.2016.
 */
public class DynamicSubActivityTextBox extends AppCompatActivity {

    private EditText editText;
    private String selectedKey;
    private String selectedValue;
    private String positionKey;
    private String actionKey;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_textbox);

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

        editText = (EditText) findViewById(R.id.editTextSimple);


        if (action != null){
            Set<Map.Entry<String, String>> set2 = action.getStringParameters().entrySet();
            for (Map.Entry<String, String> stringparam : set2) {
                if (stringparam.getKey().equalsIgnoreCase("text")){
                    editText.setText(stringparam.getValue());
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
        returnIntent.putExtra("text", editText.getText().toString());
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
