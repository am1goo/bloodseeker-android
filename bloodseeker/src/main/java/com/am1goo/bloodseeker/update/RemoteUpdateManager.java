package com.am1goo.bloodseeker.update;

import com.am1goo.bloodseeker.ITrail;
import com.am1goo.bloodseeker.trails.TrailsManager;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

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
        return file.toByteArray();
    }
}
