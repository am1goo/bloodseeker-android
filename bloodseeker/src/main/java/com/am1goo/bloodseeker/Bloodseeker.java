package com.am1goo.bloodseeker;

import com.am1goo.bloodseeker.trails.TrailsManager;
import com.am1goo.bloodseeker.update.ILocalUpdateRunnable;
import com.am1goo.bloodseeker.update.IRemoteUpdateRunnable;
import com.am1goo.bloodseeker.update.LocalUpdateConfig;
import com.am1goo.bloodseeker.update.LocalUpdateManager;
import com.am1goo.bloodseeker.update.RemoteUpdateConfig;
import com.am1goo.bloodseeker.update.RemoteUpdateManager;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Bloodseeker {

    private final ExecutorService asyncExecutor;
    private final TrailsManager trailsManager;
    private final LocalUpdateManager localUpdateManager;
    private final RemoteUpdateManager remoteUpdateManager;
    private final BloodseekerExceptions exceptions;
    private boolean isUpdating;
    private boolean isSeeking;
    private boolean isShutdown;

    public Bloodseeker() {
        this.asyncExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        this.trailsManager = createTrailsManager(asyncExecutor);
        this.localUpdateManager = createLocalUpdateManager(trailsManager);
        this.remoteUpdateManager = createRemoteUpdateManager(trailsManager);
        this.exceptions= new BloodseekerExceptions();
        this.isUpdating = false;
        this.isSeeking = false;
        this.isShutdown = false;
    }

    protected TrailsManager createTrailsManager(ExecutorService asyncExecutor) {
        return new TrailsManager(asyncExecutor);
    }

    protected LocalUpdateManager createLocalUpdateManager(TrailsManager trailsManager) {
        return new LocalUpdateManager(trailsManager);
    }

    protected RemoteUpdateManager createRemoteUpdateManager(TrailsManager trailsManager) {
        return new RemoteUpdateManager(trailsManager);
    }

    public boolean addTrail(ITrail trail) {
    	return trailsManager.addTrail(trail);
    }

    public boolean setLocalUpdateConfig(LocalUpdateConfig config) {
        try {
            localUpdateManager.setConfig(config);
            return true;
        }
        catch (IOException ex) {
            exceptions.add(this, ex);
            return false;
        }
    }

    public boolean setRemoteUpdateConfig(RemoteUpdateConfig config) {
        try {
            remoteUpdateManager.setConfig(config);
            return true;
        }
        catch (IOException | CertificateException | NoSuchAlgorithmException | KeyStoreException |
               KeyManagementException ex) {
            exceptions.add(this, ex);
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
        final BloodseekerExceptions exceptions = new BloodseekerExceptions();
        exceptions.add(this.exceptions);

        isUpdating = true;
        asyncExecutor.execute(() -> {
            ILocalUpdateRunnable localUpdateRunnable = localUpdateManager.getRunnable();
            localUpdateRunnable.run();
            exceptions.add(localUpdateRunnable.getExceptions());

            IRemoteUpdateRunnable remoteUpdateRunnable = remoteUpdateManager.getRunnable();
            remoteUpdateRunnable.run();
            exceptions.add(remoteUpdateRunnable.getExceptions());

            isUpdating = false;

            isSeeking = true;

            final List<TrailRunnable> trails = new ArrayList<>();
            trailsManager.createTasks(trails);

            final int trailsCount = trails.size();

            final Object lock = new Object();
            final AtomicInteger completedCounter = new AtomicInteger(0);

            for (TrailRunnable runnable : trails) {
                asyncExecutor.execute(() -> {
                    runnable.run();

                    synchronized (lock) {
                        results.addAll(runnable.getResults());
                        exceptions.add(runnable.getExceptions());

                        int completedCount = completedCounter.incrementAndGet();
                        if (completedCount < trailsCount)
                            return;

                        Report report = new Report(results, exceptions.getExceptions());
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
