package com.am1goo.bloodseeker.android.trails;

import android.app.Activity;

import com.am1goo.bloodseeker.ITrail;
import com.am1goo.bloodseeker.TrailRunnable;
import com.am1goo.bloodseeker.android.AndroidAppContext;
import com.am1goo.bloodseeker.android.AndroidTrailRunnable;
import com.am1goo.bloodseeker.android.AndroidUtilities;
import com.am1goo.bloodseeker.android.IAndroidTrail;
import com.am1goo.bloodseeker.trails.TrailsManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.jar.JarFile;

public class AndroidTrailsManager extends TrailsManager {

    private final List<IAndroidTrail> androidTrails;
    private final Activity activity;
    private JarFile baseApk;

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
        baseApk = AndroidUtilities.getBaseApk(activity, exceptions);

        final AndroidAppContext appContext = new AndroidAppContext(activity, baseApk);
        for (IAndroidTrail androidTrail : androidTrails) {
            TrailRunnable runnable = new AndroidTrailRunnable(appContext, androidTrail);
            result.add(runnable);
        }
        super.createTasks(result);
    }

    @Override
    public void completeTasks() {
        if (baseApk != null) {
            try {
                baseApk.close();
            }
            catch (IOException ex) {
                exceptions.add(ex);
            }
        }
        super.completeTasks();
    }

    private Activity getActivity() {
        try {
            return AndroidUtilities.getUnityPlayerActivity();
        }
        catch (Exception ex) {
            exceptions.add(ex);
            return null;
        }
    }
}
