package com.am1goo.bloodseeker;

import java.util.ArrayList;
import java.util.List;

public class BloodseekerExceptions {

    private final List<Exception> exceptions;

    public BloodseekerExceptions() {
        exceptions = new ArrayList<>();
    }

    public void add(BloodseekerExceptions other) {
        synchronized (exceptions) {
            exceptions.addAll(other.exceptions);
        }
    }

    public void add(Object obj, Exception ex) {
        add(obj.getClass(), ex);
    }

    public void add(Class<?> clazz, Exception ex) {
        add(new BloodseekerException(clazz, ex));
    }

    public void add(BloodseekerException ex) {
        synchronized (exceptions) {
            exceptions.add(ex);
        }
    }

    public List<Exception> getExceptions() {
        synchronized (exceptions) {
            return exceptions;
        }
    }
}
