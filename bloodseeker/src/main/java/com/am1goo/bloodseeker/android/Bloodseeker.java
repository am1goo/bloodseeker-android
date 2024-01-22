package com.am1goo.bloodseeker.android;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.jar.JarFile;

public class Bloodseeker {

    private final ExecutorService asyncExecutor;
	private final List<ITrail> trails;
    private boolean isSeeking;
    private boolean isShutdown;

    public Bloodseeker() {
        this.asyncExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        this.trails = new ArrayList<ITrail>();
        this.isSeeking = false;
        this.isShutdown = false;
    }

    public boolean addTrail(ITrail trail) {
    	if (trail == null)
    		return false;

    	return trails.add(trail);
    }

    public void seekAsync(final Async<Report> asyncReport) {
        if (isShutdown) {
            asyncReport.setException(new Exception("this instance is shutdown"));
            return;
        }

        if (isSeeking) {
            asyncReport.setException(new Exception("this instance is already running"));
            return;
        }

        isSeeking = true;
        final int trailsCount = trails.size();
        if (trailsCount == 0) {
            Report report = new Report(null, null);
            try {
                asyncReport.setResult(report);
            }
            catch (Exception ex) {
                asyncReport.setException(ex);
            }
            onShutdown();
            return;
        }

        final List<IResult> results = new ArrayList<IResult>();
        final List<Exception> exceptions = new ArrayList<Exception>();

        Activity activity;
        try {
            activity = Utilities.getUnityPlayerActivity();
        }
        catch (Exception ex) {
            exceptions.add(ex);
            activity = null;
        }

        final JarFile baseApk = Utilities.getBaseApk(activity, exceptions);
        final AppContext asyncContext = new AppContext(activity, baseApk);
        final AtomicInteger completedCounter = new AtomicInteger(0);

        final Object lock = new Object();
        for (int i = 0; i < trailsCount; ++i) {
            final ITrail trail = trails.get(i);
            asyncExecutor.execute(()-> {
                List<IResult> asyncResult = new ArrayList<>();
                List<Exception> asyncExceptions = new ArrayList<>();

                long startTime = System.currentTimeMillis();
                trail.seek(asyncContext, asyncResult, asyncExceptions);
                long endTime = System.currentTimeMillis();
                long milliseconds = (endTime - startTime);

                synchronized (lock) {
                    results.addAll(asyncResult);
                    exceptions.addAll(asyncExceptions);
                    System.out.println("Thread #" + Thread.currentThread().getId() + ": " + trail.getClass() + " completed in " + milliseconds + " ms");

                    int completedCount = completedCounter.incrementAndGet();
                    if (completedCount < trailsCount)
                        return;

                    Report report = new Report(results, exceptions);
                    try {
                        asyncReport.setResult(report);
                    }
                    catch (Exception ex) {
                        asyncReport.setException(ex);
                    }
                    if (baseApk != null) {
                        try {
                            baseApk.close();
                        }
                        catch (IOException ex) {
                            exceptions.add(ex);
                        }
                    }
                    onShutdown();
                }
            });
        }
    }

    private void onShutdown() {
        isShutdown = true;
        asyncExecutor.shutdown();
    }
}
