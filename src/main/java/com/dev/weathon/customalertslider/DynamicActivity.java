package com.dev.weathon.customalertslider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Joshua on 19.10.2016.
 */
public class DynamicActivity extends AppCompatActivity {
    // Parent view for all rows and the add button.
    private LinearLayout mContainerView;
    private TextView lastConfigureOpenedTextViewSpinner;
    private Spinner lastConfigureOpenedSpinner;

    private String positionKey;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.row_container);
        mContainerView = (LinearLayout) findViewById(R.id.parentView);

        Intent callingIntent = getIntent();
        String title = callingIntent.getStringExtra("positionTitle");
        positionKey = callingIntent.getStringExtra("positionKey");
        setTitle(title);

        SharedPreferences settings  = getSharedPreferences("com.dev.weathon.customalertslider_preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = settings.getString(positionKey, "");
        SliderPositionValue obj = gson.fromJson(json, SliderPositionValue.class);
        if (obj != null){
            ArrayList<SliderAction> actions = obj.getActions();
            int count = actions.size();

            for (int i = 0; i < count; i++) {
                inflateEditRow(actions.get(i));
            }
        }
    }

    // onClick handler for the "Add new" button;
    public void onAddNewClicked(View v) {
        // Inflate a new row and hide the button self.
        inflateEditRow(null);
    }

    @Override
    public void onBackPressed() {
        onSavePositionClicked(null);
    }
    public void onSavePositionClicked(View v){
        String summary ="";

        SharedPreferences settings  = getSharedPreferences("com.dev.weathon.customalertslider_preferences", MODE_PRIVATE);

        int countActions =  mContainerView.getChildCount() - 2;


        SliderPositionValue val = new SliderPositionValue(positionKey, null);


        for (int i = 0; i < countActions; i++) {
            LinearLayout row = (LinearLayout) mContainerView.getChildAt(i);
            LinearLayout spinnerParent = (LinearLayout) row.getChildAt(0);
            Spinner spinner = (Spinner) spinnerParent.getChildAt(0);
            TextView tv = (TextView) spinnerParent.getChildAt(1);
            String tvText = (String) tv.getText();
            String spinnerSelection = spinner.getSelectedItem().toString();
            String selectedKey = getResources().getStringArray(R.array.pref_sliderposition_list_values)[spinner.getSelectedItemPosition()];

            if (tvText.equals("")){
                if (summary.equals("")){
                    summary = spinnerSelection;
                }
                else{
                    summary = summary + "; " + spinnerSelection;
                }
            }
            else{
                if (summary.equals("")){
                    summary = spinnerSelection + " [" + tvText + "]";
                }
                else{
                    summary = summary + "; " + spinnerSelection + " [" + tvText + "]";
                }
            }

            val.getActions().add((SliderAction) spinner.getSelectedItem());
        }


        SharedPreferences.Editor prefsEditor = settings.edit();
        Gson gson = new Gson();
        String json = gson.toJson(val);
        prefsEditor.putString(positionKey, json);
        prefsEditor.commit();

        Intent returnIntent = new Intent();
        //returnIntent.putExtra("selectedKey", ((SimpleKeyValue)spinner.getSelectedItem()).getId());
        //returnIntent.putExtra("selectedValue", ((SimpleKeyValue)spinner.getSelectedItem()).getName());
        returnIntent.putExtra("summary", summary);
        Toast.makeText(this, getResources().getString(R.string.savedToast),Toast.LENGTH_SHORT).show();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    // onClick handler for the "X" button of each row
    public void onDeleteClicked(View v) {
        // remove the row by calling the getParent on button
        mContainerView.removeView((View) v.getParent());
    }

    public void onConfigureClicked(View v){
        LinearLayout parent = (LinearLayout) v.getParent();
        LinearLayout spinnerParent = (LinearLayout) parent.getChildAt(0);
        Spinner spinner = (Spinner) spinnerParent.getChildAt(0);
        String selectedKey = getResources().getStringArray(R.array.pref_sliderposition_list_values)[spinner.getSelectedItemPosition()];
        String selectedValue = getResources().getStringArray(R.array.pref_sliderposition_list_titles)[spinner.getSelectedItemPosition()];
        SliderAction action = (SliderAction) spinner.getSelectedItem();

        if (getResources().getString(R.string.SCREEN_BRIGHTNESS).equals(selectedKey)){
            Intent i = new Intent(this, DynamicSubActivityScreenBrightness.class);
            i.putExtra("selectedKey", selectedKey);
            i.putExtra("selectedValue", selectedValue);
            i.putExtra("positionKey", positionKey);
            i.putExtra("actionKey", action.getId());
            lastConfigureOpenedTextViewSpinner = (TextView) spinnerParent.getChildAt(1);
            lastConfigureOpenedSpinner = (Spinner) spinner;
            startActivityForResult(i, 1);
        }
        else if (getResources().getString(R.string.AUDIO_VOLUME).equals(selectedKey)){
            Intent i = new Intent(this, DynamicSubActivityAudioVolume.class);
            i.putExtra("selectedKey", selectedKey);
            i.putExtra("selectedValue", selectedValue);
            i.putExtra("positionKey", positionKey);
            i.putExtra("actionKey", action.getId());
            lastConfigureOpenedTextViewSpinner = (TextView) spinnerParent.getChildAt(1);
            lastConfigureOpenedSpinner = (Spinner) spinner;
            startActivityForResult(i, 1);
        }
        else if (getResources().getString(R.string.STARTAPP).equals(selectedKey)){
            Intent i = new Intent(this, AllAppsActivity.class);
            i.putExtra("selectedKey", selectedKey);
            i.putExtra("selectedValue", selectedValue);
            i.putExtra("positionKey", positionKey);
            i.putExtra("actionKey", action.getId());
            lastConfigureOpenedTextViewSpinner = (TextView) spinnerParent.getChildAt(1);
            lastConfigureOpenedSpinner = (Spinner) spinner;
            startActivityForResult(i, 2);
        }
        else if (getResources().getString(R.string.TOAST).equals(selectedKey)){
            Intent i = new Intent(this, DynamicSubActivityTextBox.class);
            i.putExtra("selectedKey", selectedKey);
            i.putExtra("selectedValue", selectedValue);
            i.putExtra("positionKey", positionKey);
            i.putExtra("actionKey", action.getId());
            lastConfigureOpenedTextViewSpinner = (TextView) spinnerParent.getChildAt(1);
            lastConfigureOpenedSpinner = (Spinner) spinner;
            startActivityForResult(i, 3);
        }
        else{
            Intent i = new Intent(this, DynamicSubActivityComboBox.class);
            i.putExtra("selectedKey", selectedKey);
            i.putExtra("selectedValue", selectedValue);
            i.putExtra("positionKey", positionKey);
            i.putExtra("actionKey", action.getId());
            lastConfigureOpenedTextViewSpinner = (TextView) spinnerParent.getChildAt(1);
            lastConfigureOpenedSpinner = (Spinner) spinner;
            startActivityForResult(i, 1);
        }
    }

    // Helper for inflating a row
    private void inflateEditRow(SliderAction action) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.row, null);
        final ImageButton configureButton = (ImageButton) rowView
                .findViewById(R.id.buttonConfigure);
        final Spinner spinner = (Spinner) rowView.findViewById(R.id.spinnerCategory);
        final TextView textView = (TextView) rowView.findViewById(R.id.spinnerTextView);


        String[] values = getResources().getStringArray(R.array.pref_sliderposition_list_values);
        String[] titles = getResources().getStringArray(R.array.pref_sliderposition_list_titles);

        ArrayList<SliderAction> sliderActionList = new ArrayList<>();
        int i = 0;

        for (String s: values) {
            sliderActionList.add(new SliderAction(values[i], titles[i], null, null, null));
            i++;
        }
        ArrayAdapter<SliderAction> adapter = new ArrayAdapter<SliderAction>(this, R.layout.support_simple_spinner_dropdown_item, sliderActionList);
        spinner.setAdapter(adapter);

        if (action != null){ //which means recreating the settings from sharedpref
            List<String> valueList = Arrays.asList(values);
            spinner.setSelection(valueList.indexOf(action.getId()), false);

            String textViewText = "";

            /*Set<Map.Entry<String, Boolean>> set = action.getBooleanParameters().entrySet();
            for (Map.Entry<String, Boolean> boolparam : set) {

            }*/
            SliderAction actionNew = (SliderAction) spinner.getSelectedItem();

            Set<Map.Entry<String, String>> set2 = action.getStringParameters().entrySet();
            for (Map.Entry<String, String> stringparam : set2) {
                if (stringparam.getKey().equalsIgnoreCase("mode") || stringparam.getKey().equalsIgnoreCase("apptostart") || stringparam.getKey().equalsIgnoreCase("text")){
                    textViewText = keyToValConvert(stringparam.getValue());
                    //textViewText = getResources().getStringArray(R.array.pref_sliderposition_list_values)[spinner.getSelectedItemPosition()];
                }
                actionNew.getStringParameters().put(stringparam.getKey(), stringparam.getValue());
            }

            Set<Map.Entry<String, Integer>> set3 = action.getIntParameters().entrySet();
            for (Map.Entry<String, Integer> intparam : set3) {
                if (intparam.getKey().equalsIgnoreCase("brightness_level") || intparam.getKey().equalsIgnoreCase("volume")){
                    textViewText = textViewText + ", " + intparam.getValue();
                }

                actionNew.getIntParameters().put(intparam.getKey(), intparam.getValue());
            }

            textView.setText(textViewText);

            if(Arrays.asList(getResources().getStringArray(R.array.noConfigurationNeeded_values)).contains(action.getId())){
                configureButton.setVisibility(View.INVISIBLE);
            }
            else{
                configureButton.setVisibility(View.VISIBLE);
            }
        }

        spinner.post(new Runnable() {
            public void run() {
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        Spinner s = (Spinner) view.getParent();
                        textView.setText("");
                        String selectedKey = getResources().getStringArray(R.array.pref_sliderposition_list_values)[s.getSelectedItemPosition()];
                        if(Arrays.asList(getResources().getStringArray(R.array.noConfigurationNeeded_values)).contains(selectedKey)){
                            configureButton.setVisibility(View.INVISIBLE);
                        }
                        else{
                            configureButton.setVisibility(View.VISIBLE);
                            onConfigureClicked(configureButton);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }
        });





        // Inflate at the end of all rows but before the "Add new" button
        mContainerView.addView(rowView, mContainerView.getChildCount() - 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1){
            if (resultCode == Activity.RESULT_OK){
                String selectedValue = data.getStringExtra("selectedValue");
                String selectedKey = data.getStringExtra("selectedKey");
                String actionKey = data.getStringExtra("actionKey");

                if (lastConfigureOpenedTextViewSpinner != null && lastConfigureOpenedSpinner != null){
                    SliderAction action = (SliderAction) lastConfigureOpenedSpinner.getSelectedItem();

                    action.getStringParameters().put("mode", selectedKey);

                    if (selectedKey.equalsIgnoreCase(getResources().getString(R.string.screen_brightness_level))){
                        int lvl = data.getIntExtra("brightness_level", 100);
                        lastConfigureOpenedTextViewSpinner.setText(keyToValConvert(selectedValue) + ", " + lvl);
                        action.getIntParameters().put("brightness_level", lvl);
                    }
                    else if (actionKey.equalsIgnoreCase(getResources().getString(R.string.AUDIO_VOLUME))){
                        int vol = data.getIntExtra("volume", 5);
                        lastConfigureOpenedTextViewSpinner.setText(keyToValConvert(selectedValue) + ": " + vol);
                        action.getIntParameters().put("volume", vol);
                    }
                    else{
                        lastConfigureOpenedTextViewSpinner.setText(keyToValConvert(selectedValue));
                    }
                }
            }
        }
        else if (requestCode == 2){
            if (resultCode == Activity.RESULT_OK){
                String selectedValue = data.getStringExtra("selectedValue");
                String selectedKey = data.getStringExtra("selectedKey");

                if (lastConfigureOpenedTextViewSpinner != null && lastConfigureOpenedSpinner != null){
                    SliderAction action = (SliderAction) lastConfigureOpenedSpinner.getSelectedItem();

                    if (selectedKey.equals(getResources().getString(R.string.STARTAPP))){
                        String appToStart = data.getStringExtra("apptostart");
                        lastConfigureOpenedTextViewSpinner.setText(appToStart);
                        action.getStringParameters().put("apptostart", appToStart);
                    }
                }
            }
        }
        else if (requestCode == 3){
            if (resultCode == Activity.RESULT_OK){
                String selectedValue = data.getStringExtra("text");

                if (lastConfigureOpenedTextViewSpinner != null && lastConfigureOpenedSpinner != null){
                    SliderAction action = (SliderAction) lastConfigureOpenedSpinner.getSelectedItem();
                    lastConfigureOpenedTextViewSpinner.setText(selectedValue);
                    action.getStringParameters().put("text", selectedValue);
                }
            }
        }
    }

    private String keyToValConvert(String key){
        String val = key;

        try{
            int keyPos = Arrays.asList(getResources().getStringArray(R.array.key_to_val_keys)).indexOf(key);
            val = getResources().getStringArray(R.array.key_to_val_vals)[keyPos];
        }
        catch (Exception e){
            return val;
        }
        return val;
    }
}
