package com.am1goo.bloodseeker.android;

import com.am1goo.bloodseeker.Bloodseeker;
import com.am1goo.bloodseeker.android.trails.AndroidTrailsManager;
import com.am1goo.bloodseeker.android.update.AndroidRemoteUpdateManager;
import com.am1goo.bloodseeker.trails.TrailsManager;
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
    protected RemoteUpdateManager createRemoteUpdateManager(TrailsManager trailsManager) {
        return new AndroidRemoteUpdateManager(trailsManager);
    }
}
