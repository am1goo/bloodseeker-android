package com.am1goo.bloodseeker.android.update;

import android.app.Activity;

import com.am1goo.bloodseeker.android.AndroidUtilities;
import com.am1goo.bloodseeker.trails.TrailsManager;
import com.am1goo.bloodseeker.update.IRemoteUpdateRunnable;
import com.am1goo.bloodseeker.update.RemoteUpdateManager;

public class AndroidRemoteUpdateManager extends RemoteUpdateManager {

    private Activity activity;

    public AndroidRemoteUpdateManager(TrailsManager trailsManager) {
        super(trailsManager);

        try {
            activity = AndroidUtilities.getUnityPlayerActivity();
        }
        catch (Exception ex) {
            //do nothing
        }
    }

    @Override
    public IRemoteUpdateRunnable getRunnable() {
        return new AndroidRemoteUpdateRunnable(activity, ssl, uri, secretKey, cacheTTL, trailsManager);
    }
}
