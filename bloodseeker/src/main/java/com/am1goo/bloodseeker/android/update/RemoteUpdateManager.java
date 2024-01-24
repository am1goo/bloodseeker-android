package com.am1goo.bloodseeker.android.update;

import com.am1goo.bloodseeker.android.ITrail;
import com.am1goo.bloodseeker.android.trails.TrailsManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class RemoteUpdateManager {

    private final TrailsManager trailsManager;

    private URI uri;
    private byte[] secretKey;
    private long cacheTTL;

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

    public RemoteUpdateRunnable getRunnable()
    {
        return new RemoteUpdateRunnable(uri, secretKey, cacheTTL, trailsManager);
    }

    public RemoteUpdateFile exportToFile() throws Exception {
        RemoteUpdateFile file = new RemoteUpdateFile(secretKey);
        List<IRemoteUpdateTrail> trails = new ArrayList<>();
        for (ITrail trail : trailsManager.getTrails()) {
            if (trail instanceof IRemoteUpdateTrail) {
                IRemoteUpdateTrail remoteUpdateTrail = (IRemoteUpdateTrail)trail;
                trails.add(remoteUpdateTrail);
            }
            file.setTrails(trails);
        }
        return file;
    }

    public byte[] exportToBytes() throws Exception {
        RemoteUpdateFile file = exportToFile();
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            file.save(outputStream);
            return outputStream.toByteArray();
        }
    }
}
