package com.am1goo.bloodseeker.android.trails;

import com.am1goo.bloodseeker.android.AppContext;
import com.am1goo.bloodseeker.android.IResult;
import com.am1goo.bloodseeker.android.ITrail;

import java.util.ArrayList;
import java.util.List;

public class TrailRunnable implements Runnable {

    private final ITrail trail;
    private final AppContext appContext;
    private final List<IResult> results;
    private final List<Exception> exceptions;

    public TrailRunnable(ITrail trail, AppContext appContext) {
        this(trail, appContext, new ArrayList<>(), new ArrayList<>());
    }

    private TrailRunnable(ITrail trail, AppContext appContext, List<IResult> results, List<Exception> exceptions) {
        this.trail = trail;
        this.appContext = appContext;
        this.results = results;
        this.exceptions = exceptions;
    }

    public List<IResult> getResults() {
        return results;
    }

    public List<Exception> getExceptions() {
        return exceptions;
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        trail.seek(appContext, results, exceptions);
        long endTime = System.currentTimeMillis();
        long milliseconds = (endTime - startTime);
        System.out.println("Thread #" + Thread.currentThread().getId() + ": " + trail.getClass() + " completed in " + milliseconds + " ms");
    }
}