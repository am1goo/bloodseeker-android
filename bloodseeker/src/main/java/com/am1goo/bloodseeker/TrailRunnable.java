package com.am1goo.bloodseeker;

import java.util.ArrayList;
import java.util.List;

public class TrailRunnable implements Runnable {

    private final ITrail trail;
    private final List<IResult> results;
    private final List<Exception> exceptions;

    public TrailRunnable(ITrail trail) {
        this(trail, new ArrayList<>(), new ArrayList<>());
    }

    protected TrailRunnable(ITrail trail, List<IResult> results, List<Exception> exceptions) {
        this.trail = trail;
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
        trail.seek(results, exceptions);
        long endTime = System.currentTimeMillis();
        long milliseconds = (endTime - startTime);
        System.out.println("Thread #" + Thread.currentThread().getId() + ": " + trail.getClass() + " completed in " + milliseconds + " ms");
    }
}