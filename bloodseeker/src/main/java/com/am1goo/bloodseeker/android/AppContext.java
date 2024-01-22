package com.am1goo.bloodseeker.android;

import android.app.Activity;

import java.util.jar.JarFile;

public class AppContext {

    private final Activity activity;
    private final JarFile baseApk;

    public AppContext(Activity activity, JarFile baseApk) {
        this.activity = activity;
        this.baseApk = baseApk;
    }

    public Activity getActivity() {
        return activity;
    }

    public JarFile getBaseApk() {
        return baseApk;
    }
}
