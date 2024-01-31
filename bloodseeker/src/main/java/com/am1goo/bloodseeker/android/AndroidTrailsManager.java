package com.am1goo.bloodseeker.android;

import android.app.Activity;

import com.am1goo.bloodseeker.ITrail;
import com.am1goo.bloodseeker.TrailRunnable;
import com.am1goo.bloodseeker.TrailsManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.jar.JarFile;

public class AndroidTrailsManager extends TrailsManager {

    private final List<IAndroidTrail> androidTrails;
    private final Activity activity;
    private JarFile baseApkJar;

    public AndroidTrailsManager(ExecutorService asyncExecutor) {
        super(asyncExecutor);

        this.androidTrails = new ArrayList<>();
        this.activity = getActivity();
    }

    @Override
    public boolean addTrail(ITrail trail) {
        if (trail instanceof IAndroidTrail) {
            IAndroidTrail androidTrail = (IAndroidTrail) trail;
            androidTrails.add((androidTrail));
            return true;
        }
        else {
            return super.addTrail(trail);
        }
    }

    @Override
    public void createTasks(List<TrailRunnable> result) {
        File baseApkFile = AndroidUtilities.getBaseApkFile(activity);
        baseApkJar = AndroidUtilities.getBaseApkJar(activity, exceptions);
        File libraryDir = AndroidUtilities.getLibraryDir(activity);

        final AndroidAppContext appContext = new AndroidAppContext(activity, baseApkFile, baseApkJar, libraryDir);
        for (IAndroidTrail androidTrail : androidTrails) {
            TrailRunnable runnable = new AndroidTrailRunnable(appContext, androidTrail);
            result.add(runnable);
        }
        super.createTasks(result);
    }

    @Override
    public void completeTasks() {
        if (baseApkJar != null) {
            try {
                baseApkJar.close();
            }
            catch (IOException ex) {
                exceptions.add(this, ex);
            }
        }
        super.completeTasks();
    }

    private Activity getActivity() {
        try {
            return AndroidUtilities.getUnityPlayerActivity();
        }
        catch (Exception ex) {
            exceptions.add(this, ex);
            return null;
        }
    }
}
