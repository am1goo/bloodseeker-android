package com.am1goo.bloodseeker.android;

import android.app.Activity;

import java.io.File;
import java.util.jar.JarFile;

public class AndroidAppContext {

    private final Activity activity;
    private final File baseApkFile;
    private final JarFile baseApkJar;

    public AndroidAppContext(Activity activity, File baseApkFile, JarFile baseApkJar) {
        this.activity = activity;
        this.baseApkFile = baseApkFile;
        this.baseApkJar = baseApkJar;
    }

    public Activity getActivity() {
        return activity;
    }

    public File getBaseApkFile() {
        return baseApkFile;
    }

    public JarFile getBaseApkJar() {
        return baseApkJar;
    }
}
