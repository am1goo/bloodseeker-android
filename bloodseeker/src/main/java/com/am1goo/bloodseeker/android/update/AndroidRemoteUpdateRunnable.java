package com.am1goo.bloodseeker.android.update;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import com.am1goo.bloodseeker.update.RemoteUpdateRunnable;
import com.am1goo.bloodseeker.trails.TrailsManager;

import java.io.File;
import java.net.URI;

public class AndroidRemoteUpdateRunnable extends RemoteUpdateRunnable {

    private final Activity activity;

    public AndroidRemoteUpdateRunnable(Activity activity, URI uri, byte[] secretKey, long cacheTTL, TrailsManager trailsManager) {
        super(uri, secretKey, cacheTTL, trailsManager);
        this.activity = activity;
    }

    @Override
    public File getCacheDir() {
        if (activity == null)
            return null;

        Context ctx = activity.getBaseContext();
        return ctx.getCacheDir();
    }

    @Override
    public void run() {
        if (activity == null) {
            System.out.println("Thread #" + Thread.currentThread().getId() + ": remote update is disabled, activity not found");
            exceptions.add(this, new Exception("remote update unavailable, activity not found"));
            return;
        }

        Context ctx = activity.getBaseContext();
        String permission = Manifest.permission.INTERNET;
        int permissionResult = ctx.checkCallingOrSelfPermission(permission);
        if (permissionResult != PackageManager.PERMISSION_GRANTED) {
            System.out.println("Thread #" + Thread.currentThread().getId() + ": remote update is disabled, app doesn't have permission " + permission);
            exceptions.add(this, new Exception("remote update unavailable, app doesn't have permission " + permission));
            return;
        }

        super.run();
    }
}
