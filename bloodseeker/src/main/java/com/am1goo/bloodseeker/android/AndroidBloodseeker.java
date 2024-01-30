package com.am1goo.bloodseeker.android;

import com.am1goo.bloodseeker.Bloodseeker;
import com.am1goo.bloodseeker.android.update.AndroidLocalUpdateManager;
import com.am1goo.bloodseeker.android.update.AndroidRemoteUpdateManager;
import com.am1goo.bloodseeker.TrailsManager;
import com.am1goo.bloodseeker.update.LocalUpdateManager;
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
}
