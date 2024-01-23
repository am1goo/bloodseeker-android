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

public class RemoteUpdateRunnable implements Runnable {
    final URI uri;
    final byte[] secretKey;
    final private TrailsManager trailsManager;
    final List<Exception> exceptions;

    public RemoteUpdateRunnable(URI uri, byte[] secretKey, TrailsManager trailsManager) {
        this.uri = uri;
        this.secretKey = secretKey;
        this.trailsManager = trailsManager;
        this.exceptions = new ArrayList<>();
    }

    public List<Exception> getExceptions() {
        return exceptions;
    }

    @Override
    public void run() {
        if (uri == null) {
            System.out.println("Thread #" + Thread.currentThread().getId() + ": update url not defined, skip this step.");
            return;
        }

        long startTime = System.currentTimeMillis();

        try {
            List<ITrail> trails = downloadFile(uri);
            for (ITrail trail : trails) {
                trailsManager.addTrail(trail);
            }
        }
        catch (IOException ex) {
            exceptions.add(ex);
        }

        long endTime = System.currentTimeMillis();
        long milliseconds = (endTime - startTime);
        System.out.println("Thread #" + Thread.currentThread().getId() + ": " + this.getClass() + " updated in " + milliseconds + " ms");
    }

    private List<ITrail> downloadFile(URI uri) throws IOException {
        final List<ITrail> trails = new ArrayList<>();

        final URL url = uri.toURL();
        final HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
        try {
            InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
            loadFromFile(inputStream, trails);
        }
        finally {
            urlConnection.disconnect();
        }
        return trails;
    }

    private void loadFromFile(InputStream inputStream, List<ITrail> result) {
        RemoteUpdateFile file;
        try {
            file = new RemoteUpdateFile(secretKey);
            file.load(inputStream);
        }
        catch (Exception ex) {
            exceptions.add(ex);
            return;
        }

        result.addAll(file.getTrails());
    }
}
