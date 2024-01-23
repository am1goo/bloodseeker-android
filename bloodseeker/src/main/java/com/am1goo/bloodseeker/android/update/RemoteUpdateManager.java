package com.am1goo.bloodseeker.android.update;

import com.am1goo.bloodseeker.android.ITrail;
import com.am1goo.bloodseeker.android.trails.TrailsManager;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RemoteUpdateManager {

    private final TrailsManager trailsManager;

    private URI uri;
    private byte[] secretKey;

    public RemoteUpdateManager(TrailsManager trailsManager) {
        this.trailsManager = trailsManager;
        this.uri = null;
    }

    public void setURI(URI uri) {
        this.uri = uri;
    }

    public void setSecretKey(byte[] secretKey) {
        this.secretKey = secretKey;
    }

    public RemoteUpdateRunnable getRunnable()
    {
        return new RemoteUpdateRunnable(uri, secretKey, trailsManager);
    }
}
