package com.dev.weathon.customalertslider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Joshua on 19.10.2016.
 */
public class SliderAction {
    private String id;
    private String displayName;
    private Map<String, String> stringParameters;
    private Map<String, Integer> intParameters;
    private Map<String, Boolean> booleanParameters;

    public SliderAction(String id, String displayName,Map<String, String> stringParameters, Map<String, Integer> intParameters, Map<String, Boolean> booleanParameters) {
        this.id = id;
        this.displayName = displayName;
        this.stringParameters = stringParameters != null ? stringParameters : new HashMap<String, String>();
        this.intParameters = intParameters != null ? intParameters : new HashMap<String, Integer>();
        this.booleanParameters = booleanParameters != null ? booleanParameters : new HashMap<String, Boolean>();
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Map<String, String> getStringParameters() {
        return stringParameters;
    }

    public void setStringParameters(Map<String, String> stringParameters) {
        this.stringParameters = stringParameters;
    }

    public Map<String, Integer> getIntParameters() {
        return intParameters;
    }

    public void setIntParameters(Map<String, Integer> intParameters) {
        this.intParameters = intParameters;
    }

    public Map<String, Boolean> getBooleanParameters() {
        return booleanParameters;
    }

    public void setBooleanParameters(Map<String, Boolean> booleanParameters) {
        this.booleanParameters = booleanParameters;
    }
    public String toString() {
        return displayName;
    }

}
