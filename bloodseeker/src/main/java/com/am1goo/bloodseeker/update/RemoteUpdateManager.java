package com.am1goo.bloodseeker.update;

import com.am1goo.bloodseeker.trails.TrailsManager;
import com.am1goo.bloodseeker.utilities.SecureUtilities;

import java.io.IOException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLSocketFactory;

public class RemoteUpdateManager {

    protected final TrailsManager trailsManager;

    protected URI uri;
    protected SSLSocketFactory ssl;
    protected byte[] secretKey;
    protected long cacheTTL;

    public RemoteUpdateManager(TrailsManager trailsManager) {
        this.trailsManager = trailsManager;
        this.uri = null;
    }

    public void setConfig(RemoteUpdateConfig config) throws IllegalArgumentException, IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        String url = config.getUrl();
        this.uri = url != null ? URI.create(url) : null;

        RemoteUpdateConfig.Keystore keystore = config.getKeystore();
        this.ssl = keystore != null ? SecureUtilities.createSslSocketFactory(keystore.getCert(), keystore.getPwd()) : null;

        String secretKey = config.getSecretKey();
        this.secretKey = secretKey != null ? secretKey.getBytes("utf-8") : null;

        this.cacheTTL = config.getCacheTTL();
    }

    public IRemoteUpdateRunnable getRunnable() {
        return new RemoteUpdateRunnable(uri, ssl, secretKey, cacheTTL, trailsManager);
    }
}
