package com.am1goo.bloodseeker.update;

import com.am1goo.bloodseeker.trails.TrailsManager;

import java.io.UnsupportedEncodingException;

public class LocalUpdateManager {

    protected final TrailsManager trailsManager;
    protected byte[] file;
    protected byte[] secretKey;

    public LocalUpdateManager(TrailsManager trailsManager) {
        this.trailsManager = trailsManager;
    }

    public void setConfig(LocalUpdateConfig config) throws IllegalArgumentException, UnsupportedEncodingException {
        this.file = config.getFile();

        String secretKey = config.getSecretKey();
        this.secretKey = secretKey != null ? secretKey.getBytes("utf-8") : null;
    }

    public ILocalUpdateRunnable getRunnable() {
        return new LocalUpdateRunnable(file, secretKey, trailsManager);
    }
}
