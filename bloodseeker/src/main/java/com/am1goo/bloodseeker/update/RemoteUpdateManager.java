package com.am1goo.bloodseeker.update;

import com.am1goo.bloodseeker.trails.TrailsManager;

import java.io.UnsupportedEncodingException;
import java.net.URI;

public class RemoteUpdateManager {

    protected final TrailsManager trailsManager;

    protected URI uri;
    protected byte[] secretKey;
    protected long cacheTTL;

    public RemoteUpdateManager(TrailsManager trailsManager) {
        this.trailsManager = trailsManager;
        this.uri = null;
    }

    public void setConfig(RemoteUpdateConfig config) throws IllegalArgumentException, UnsupportedEncodingException {
        String url = config.getUrl();
        this.uri = url != null ? URI.create(url) : null;

        String secretKey = config.getSecretKey();
        this.secretKey = secretKey != null ? secretKey.getBytes("utf-8") : null;

        this.cacheTTL = config.getCacheTTL();
    }

    public IRemoteUpdateRunnable getRunnable() {
        return new RemoteUpdateRunnable(uri, secretKey, cacheTTL, trailsManager);
    }
}
