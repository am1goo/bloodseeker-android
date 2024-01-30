package com.am1goo.bloodseeker.android;

import android.app.Activity;

import java.io.File;
import java.util.jar.JarFile;

public class AndroidAppContext {

    private final Activity activity;
    private final File baseApkFile;
    private final JarFile baseApkJar;
    private final File libraryDir;

    public AndroidAppContext(Activity activity, File baseApkFile, JarFile baseApkJar, File libraryDir) {
        this.activity = activity;
        this.baseApkFile = baseApkFile;
        this.baseApkJar = baseApkJar;
        this.libraryDir = libraryDir;
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

    public File getLibraryDir() {
        return libraryDir;
    }
}
