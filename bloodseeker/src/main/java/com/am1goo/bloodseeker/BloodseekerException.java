package com.am1goo.bloodseeker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

public class BloodseekerException extends Exception {

    private String task;

    public BloodseekerException(Class<?> clazz, Exception ex) {
        this(clazz.getName(), ex);
    }

    public BloodseekerException(String task, Exception ex) {
        super(ex);
        this.task = task;
    }

    @Override
    public String getMessage() {
        return "[" + task + "] " + super.getMessage();
    }

    @NonNull
    @Override
    public String toString() {
        String message = this.getMessage();
        if (message != null)
            return message;

        return super.toString();
    }
}
