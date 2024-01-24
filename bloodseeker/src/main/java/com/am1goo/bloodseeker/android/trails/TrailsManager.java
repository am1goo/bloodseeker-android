package com.am1goo.bloodseeker.android.trails;

import android.app.Activity;

import com.am1goo.bloodseeker.android.AppContext;
import com.am1goo.bloodseeker.android.IResult;
import com.am1goo.bloodseeker.android.ITrail;
import com.am1goo.bloodseeker.android.Report;
import com.am1goo.bloodseeker.android.Utilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.jar.JarFile;

public class TrailsManager {

    private final ExecutorService asyncExecutor;
    private final List<ITrail> trails;
    private final List<Exception> exceptions;

    private final Activity activity;
    private final JarFile baseApk;

    public TrailsManager(ExecutorService asyncExecutor) {
        this.asyncExecutor = asyncExecutor;
        this.trails = new ArrayList<>();
        this.exceptions = new ArrayList<>();

        activity = getActivity();
        baseApk = Utilities.getBaseApk(activity, exceptions);
    }

    private Activity getActivity() {
        try {
            return Utilities.getUnityPlayerActivity();
        }
        catch (Exception ex) {
            exceptions.add(ex);
            return null;
        }
    }

    public boolean addTrail(ITrail trail) {
        if (trail == null)
            return false;

        return trails.add(trail);
    }

    public List<ITrail> getTrails() {
        return trails;
    }

    public List<TrailRunnable> createTasks() {
        final List<TrailRunnable> runnables = new ArrayList<>();

        final int trailsCount = trails.size();
        if (trailsCount == 0)
            return runnables;

        final AppContext appContext = new AppContext(activity, baseApk);
        for (int i = 0; i < trailsCount; ++i) {
            final ITrail trail = trails.get(i);
            final TrailRunnable runnable = new TrailRunnable(trail, appContext);
            runnables.add(runnable);
        }
        return runnables;
    }

    public void completeTasks() {
        if (baseApk != null) {
            try {
                baseApk.close();
            }
            catch (IOException ex) {
                exceptions.add(ex);
            }
        }
    }
}
