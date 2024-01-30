package com.am1goo.bloodseeker.android;

import com.am1goo.bloodseeker.Async;
import com.am1goo.bloodseeker.Bloodseeker;
import com.am1goo.bloodseeker.ITrail;
import com.am1goo.bloodseeker.Report;
import com.am1goo.bloodseeker.android.update.AndroidLocalUpdateManager;
import com.am1goo.bloodseeker.android.update.AndroidRemoteUpdateManager;
import com.am1goo.bloodseeker.TrailsManager;
import com.am1goo.bloodseeker.update.LocalUpdateConfig;
import com.am1goo.bloodseeker.update.LocalUpdateManager;
import com.am1goo.bloodseeker.update.RemoteUpdateConfig;
import com.am1goo.bloodseeker.update.RemoteUpdateManager;

import java.util.concurrent.ExecutorService;

public class AndroidBloodseeker extends Bloodseeker {

    public AndroidBloodseeker() {

    }

    @Override
    protected TrailsManager createTrailsManager(ExecutorService asyncExecutor) {
        return new AndroidTrailsManager(asyncExecutor);
    }

    @Override
    protected LocalUpdateManager createLocalUpdateManager(TrailsManager trailsManager) {
        return new AndroidLocalUpdateManager(trailsManager);
    }

    @Override
    protected RemoteUpdateManager createRemoteUpdateManager(TrailsManager trailsManager) {
        return new AndroidRemoteUpdateManager(trailsManager);
    }

    @Override
    public boolean addTrail(ITrail trail) {
        return super.addTrail(trail);
    }

    @Override
    public boolean setLocalUpdateConfig(LocalUpdateConfig config) {
        return super.setLocalUpdateConfig(config);
    }

    @Override
    public boolean setRemoteUpdateConfig(RemoteUpdateConfig config) {
        return super.setRemoteUpdateConfig(config);
    }

    @Override
    public void seekAsync(Async<Report> asyncReport) {
        super.seekAsync(asyncReport);
    }
}
