package com.am1goo.bloodseeker.android.update;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import com.am1goo.bloodseeker.trails.TrailsManager;
import com.am1goo.bloodseeker.update.LocalUpdateRunnable;

public class AndroidLocalUpdateRunnable extends LocalUpdateRunnable {

    private final Activity activity;

    public AndroidLocalUpdateRunnable(Activity activity, byte[] file, byte[] secretKey, TrailsManager trailsManager) {
        super(file, secretKey, trailsManager);
        this.activity = activity;
    }

    @Override
    public void run() {
        if (activity == null) {
            System.out.println("Thread #" + Thread.currentThread().getId() + ": local update is disabled, activity not found");
            exceptions.add(getClass(), new Exception("local update unavailable, activity not found"));
            return;
        }

        super.run();
    }
}
