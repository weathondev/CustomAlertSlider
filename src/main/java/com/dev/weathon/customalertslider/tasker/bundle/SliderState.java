package com.dev.weathon.customalertslider.tasker.bundle;

/**
 * Created by simon on 9/16/16.
 */
public enum SliderState {
    ANY(0), TOP(1), MIDDLE(2), BOTTOM(3);

    private final int value;
    private SliderState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static SliderState fromValue(int id) {
        for (SliderState type : SliderState.values()) {
            if (type.getValue() == id) {
                return type;
            }
        }
        return null;
    }
}
