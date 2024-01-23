package com.am1goo.bloodseeker.android.update;

import com.am1goo.bloodseeker.android.trails.TrailsManager;

import java.io.UnsupportedEncodingException;
import java.net.URI;

public class RemoteUpdateManager {

    private final TrailsManager trailsManager;

    private URI uri;
    private byte[] secretKey;

    public RemoteUpdateManager(TrailsManager trailsManager) {
        this.trailsManager = trailsManager;
        this.uri = null;
    }

    public void setConfig(RemoteUpdateConfig config) throws IllegalArgumentException, UnsupportedEncodingException {
        String url = config.getUrl();
        this.uri = url != null ? URI.create(url) : null;

        String secretKey = config.getSecretKey();
        this.secretKey = secretKey != null ? secretKey.getBytes("utf-8") : null;
    }

    public RemoteUpdateRunnable getRunnable()
    {
        return new RemoteUpdateRunnable(uri, secretKey, trailsManager);
    }
}
