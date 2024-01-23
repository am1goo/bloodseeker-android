package com.am1goo.bloodseeker.android;

import com.am1goo.bloodseeker.android.trails.TrailRunnable;
import com.am1goo.bloodseeker.android.trails.TrailsManager;
import com.am1goo.bloodseeker.android.update.RemoteUpdateManager;
import com.am1goo.bloodseeker.android.update.RemoteUpdateRunnable;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Bloodseeker {

    private final ExecutorService asyncExecutor;
    private final TrailsManager trailsManager;
    private final RemoteUpdateManager remoteUpdateManager;
    private final List<Exception> exceptions;
    private boolean isUpdating;
    private boolean isSeeking;
    private boolean isShutdown;

    public Bloodseeker() {
        this.asyncExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        this.trailsManager = new TrailsManager(asyncExecutor);
        this.remoteUpdateManager = new RemoteUpdateManager(trailsManager);
        this.exceptions= new ArrayList<>();
        this.isUpdating = false;
        this.isSeeking = false;
        this.isShutdown = false;
    }

    public boolean addTrail(ITrail trail) {
    	return trailsManager.addTrail(trail);
    }

    public boolean setUpdateUrl(String url) {
        try {
            URI uri = URI.create(url);
            remoteUpdateManager.setURI(uri);
            return true;
        }
        catch (IllegalArgumentException ex) {
            exceptions.add(ex);
            return false;
        }
    }

    public boolean setUpdateDecryptKey(String secretKey) {
        if (secretKey == null)
            return false;

        try {
            byte[] bytes = secretKey.getBytes("utf-8");
            remoteUpdateManager.setSecretKey(bytes);
            return true;
        }
        catch (IOException ex) {
            exceptions.add(ex);
            return false;
        }
    }

    public void seekAsync(final Async<Report> asyncReport) {
        if (isShutdown) {
            asyncReport.setException(new Exception("this instance is shutdown"));
            return;
        }

        if (isUpdating) {
            asyncReport.setException(new Exception("this instance is updating"));
            return;
        }

        if (isSeeking) {
            asyncReport.setException(new Exception("this instance is already running"));
            return;
        }

        final List<IResult> results = new ArrayList<>();
        final List<Exception> exceptions = new ArrayList<>();
        exceptions.addAll(this.exceptions);

        isUpdating = true;
        asyncExecutor.execute(() -> {
            RemoteUpdateRunnable updateRunnable = remoteUpdateManager.getRunnable();
            updateRunnable.run();
            exceptions.addAll(updateRunnable.getExceptions());
            isUpdating = false;

            isSeeking = true;

            final List<TrailRunnable> trails = trailsManager.createTasks();
            final int trailsCount = trails.size();

            final Object lock = new Object();
            final AtomicInteger completedCounter = new AtomicInteger(0);

            for (TrailRunnable runnable : trails) {
                asyncExecutor.execute(() -> {
                    runnable.run();

                    synchronized (lock) {
                        results.addAll(runnable.getResults());
                        exceptions.addAll(runnable.getExceptions());

                        int completedCount = completedCounter.incrementAndGet();
                        if (completedCount < trailsCount)
                            return;

                        Report report = new Report(results, exceptions);
                        try {
                            asyncReport.setResult(report);
                        } catch (Exception ex) {
                            asyncReport.setException(ex);
                        }
                        trailsManager.completeTasks();
                        onShutdown();
                    }
                });
            }
        });
    }

    private void onShutdown() {
        isShutdown = true;
        asyncExecutor.shutdown();
    }
}
