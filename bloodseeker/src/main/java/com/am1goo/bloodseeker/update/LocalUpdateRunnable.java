package com.am1goo.bloodseeker.update;

import com.am1goo.bloodseeker.BloodseekerExceptions;
import com.am1goo.bloodseeker.ITrail;
import com.am1goo.bloodseeker.TrailsManager;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class LocalUpdateRunnable implements ILocalUpdateRunnable {

    final byte[] file;
    final byte[] secretKey;
    final private TrailsManager trailsManager;
    final protected BloodseekerExceptions exceptions;

    public LocalUpdateRunnable(byte[] file, byte[] secretKey, TrailsManager trailsManager) {
        this.file = file;
        this.secretKey = secretKey;
        this.trailsManager = trailsManager;
        this.exceptions = new BloodseekerExceptions();
    }

    public BloodseekerExceptions getExceptions() {
        return exceptions;
    }

    @Override
    public void run() {
        if (file == null) {
            System.out.println("Thread #" + Thread.currentThread().getId() + ": local update file is not defined, skip this step.");
            return;
        }

        long startTime = System.currentTimeMillis();

        RemoteUpdateFile file = new RemoteUpdateFile(secretKey);
        try (InputStream inputStream = new ByteArrayInputStream(this.file)) {
            file.load(inputStream);
        } catch (Exception ex) {
            exceptions.add(this, ex);
        }

        for (ITrail trail : file.getTrails()) {
            trailsManager.addTrail(trail);
        }

        long endTime = System.currentTimeMillis();
        long milliseconds = (endTime - startTime);
        System.out.println("Thread #" + Thread.currentThread().getId() + ": " + this.getClass() + " updated in " + milliseconds + " ms");
    }
}
