package com.am1goo.bloodseeker;

import com.am1goo.bloodseeker.BloodseekerExceptions;
import com.am1goo.bloodseeker.TrailRunnable;
import com.am1goo.bloodseeker.ITrail;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class TrailsManager {

    private final ExecutorService asyncExecutor;
    private final List<ITrail> trails;
    protected final BloodseekerExceptions exceptions;

    public TrailsManager(ExecutorService asyncExecutor) {
        this.asyncExecutor = asyncExecutor;
        this.trails = new ArrayList<>();
        this.exceptions = new BloodseekerExceptions();
    }

    public boolean addTrail(ITrail trail) {
        if (trail == null)
            return false;

        return trails.add(trail);
    }

    public List<ITrail> getTrails() {
        return trails;
    }

    public void createTasks(List<TrailRunnable> result) {
        final int trailsCount = trails.size();
        for (int i = 0; i < trailsCount; ++i) {
            final ITrail trail = trails.get(i);
            final TrailRunnable runnable = new TrailRunnable(trail);
            result.add(runnable);
        }
    }

    public void completeTasks() {
        //do nothing
    }
}
