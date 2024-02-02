package com.am1goo.bloodseeker;

public enum Condition {
    Eq(0),
    NonEq(1);

    private final int value;

    Condition(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static int valueOf(Condition condition) {
        if (condition == null)
            return -1;

        return condition.getValue();
    }

    public static Condition valueOf(int value) {
        return valueOf(value, null);
    }

    public static Condition valueOf(int value, Condition defaultValue) {
        for (Condition c : values()) {
            if (c.value == value)
                return c;
        }
        return defaultValue;
    }
}
