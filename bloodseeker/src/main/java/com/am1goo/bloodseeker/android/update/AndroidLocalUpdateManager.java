package com.am1goo.bloodseeker.android.update;

import android.app.Activity;

import com.am1goo.bloodseeker.android.AndroidUtilities;
import com.am1goo.bloodseeker.TrailsManager;
import com.am1goo.bloodseeker.update.ILocalUpdateRunnable;
import com.am1goo.bloodseeker.update.LocalUpdateManager;

public class AndroidLocalUpdateManager extends LocalUpdateManager {

    private Activity activity;

    public AndroidLocalUpdateManager(TrailsManager trailsManager) {
        super(trailsManager);

        try {
            activity = AndroidUtilities.getUnityPlayerActivity();
        }
        catch (Exception ex) {
            //do nothing
        }
    }

    @Override
    public ILocalUpdateRunnable getRunnable() {
        return new AndroidLocalUpdateRunnable(activity, file, secretKey, trailsManager);
    }
}
