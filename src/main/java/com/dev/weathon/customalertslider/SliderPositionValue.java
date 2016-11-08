package com.dev.weathon.customalertslider;

import java.util.ArrayList;

/**
 * Created by Joshua on 19.10.2016.
 */
public class SliderPositionValue {
    private String id;
    private ArrayList<SliderAction> actions;

    public SliderPositionValue(String id, ArrayList<SliderAction> actions) {
        this.id = id;
        this.actions = actions != null ? actions : new ArrayList<SliderAction>();
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<SliderAction> getActions() {
        return actions;
    }

    public void setActions(ArrayList<SliderAction> actions) {
        this.actions = actions;
    }
}
