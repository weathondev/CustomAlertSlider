package com.dev.weathon.customalertslider;

/**
 * Created by Joshua on 19.10.2016.
 */
public class SimpleKeyValue {
    private String id;
    private String name;

    public SimpleKeyValue(String id, String name) {
        this.id = id;
        this.name = name;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    //to display object as a string in spinner
    public String toString() {
        return name;
    }
}
