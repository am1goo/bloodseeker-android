package com.am1goo.bloodseeker.utilities;

import androidx.annotation.NonNull;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

public class SecureUtilities {

    public static SSLSocketFactory createSslSocketFactory(@NonNull final byte[] keystore, @NonNull final String pwd) throws NoSuchAlgorithmException, IOException, KeyStoreException, CertificateException, KeyManagementException {
        TrustManager[] tm = SecureUtilities.createTrustManagers(keystore, pwd);
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, tm, new SecureRandom());
        return sc.getSocketFactory();
    }

    public static TrustManager[] createTrustManagers(@NonNull final byte[] keystore, @NonNull final String pwd) throws NoSuchAlgorithmException, IOException, KeyStoreException, CertificateException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(keystore)) {
            return createTrustManagers(inputStream, pwd);
        }
    }

    public static TrustManager[] createTrustManagers(@NonNull final File file, @NonNull final String pwd) throws NoSuchAlgorithmException, IOException, KeyStoreException, CertificateException {
        try (FileInputStream inputStream = new FileInputStream(file)) {
            return createTrustManagers(inputStream, pwd);
        }
    }

    public static TrustManager[] createTrustManagers(InputStream inputStream, String pwd) throws NoSuchAlgorithmException, IOException, KeyStoreException, CertificateException {
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("PKIX");
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(inputStream, pwd.toCharArray());
        tmf.init(ks);
        return tmf.getTrustManagers();
    }
}
