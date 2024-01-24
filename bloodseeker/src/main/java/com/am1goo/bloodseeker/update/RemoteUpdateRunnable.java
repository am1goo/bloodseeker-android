package com.am1goo.bloodseeker.update;

import com.am1goo.bloodseeker.ITrail;
import com.am1goo.bloodseeker.trails.TrailsManager;
import com.am1goo.bloodseeker.utilities.DateUtilities;
import com.am1goo.bloodseeker.utilities.IOUtilities;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RemoteUpdateRunnable implements IRemoteUpdateRunnable {

    final URI uri;
    final byte[] secretKey;
    final long cacheTTL;
    final private TrailsManager trailsManager;
    final protected List<Exception> exceptions;
    final private File cacheDir;

    public RemoteUpdateRunnable(URI uri, byte[] secretKey, long cacheTTL, TrailsManager trailsManager) {
        this.uri = uri;
        this.secretKey = secretKey;
        this.cacheTTL = cacheTTL;
        this.trailsManager = trailsManager;
        this.exceptions = new ArrayList<>();
        this.cacheDir = findCacheDir();
    }

    private File findCacheDir() {
        String path;
        try {
            path = System.getProperty("java.io.tempdir");
        }
        catch (NullPointerException ex) {
            exceptions.add(ex);
            path = null;
        }
        if (path == null)
            path = "temp";
        return new File(path);
    }

    public File getCacheDir() {
        return cacheDir;
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

        File cacheDir = getCacheDir();
        long startTime = System.currentTimeMillis();

        try {
            List<ITrail> trails = new ArrayList<>();
            if (cacheLoadFile(uri, secretKey, cacheDir, trails)) {
                //do nothing
            }
            else {
                downloadAndLoadFile(uri, secretKey, cacheDir, trails);
            }
            for (ITrail trail : trails) {
                trailsManager.addTrail(trail);
            }
        }
        catch (Exception ex) {
            exceptions.add(ex);
        }

        long endTime = System.currentTimeMillis();
        long milliseconds = (endTime - startTime);
        System.out.println("Thread #" + Thread.currentThread().getId() + ": " + this.getClass() + " updated in " + milliseconds + " ms");
    }

    private void downloadAndLoadFile(URI uri, byte[] secretKey, File cacheDir, List<ITrail> trails) throws Exception {
        final URL url = uri.toURL();
        final HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
        try (InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream())) {
            loadFromFile(inputStream, secretKey, trails);
            saveToCache(uri, cacheDir, inputStream);
        }
        finally {
            urlConnection.disconnect();
        }
    }

    private void loadFromFile(final InputStream inputStream, final byte[] secretKey, final List<ITrail> result) throws Exception {
        RemoteUpdateFile file = new RemoteUpdateFile(secretKey);
        file.load(inputStream);
        result.clear();
        result.addAll(file.getTrails());
    }

    private Path getCachePath(URI uri, File cacheDir) throws NoSuchAlgorithmException {
        String uriHash = md5(uri.getPath());
        String fileName = uriHash + RemoteUpdateFile.EXTENSION;
        return Paths.get(cacheDir.getPath(), fileName);
    }

    private boolean cacheLoadFile(URI uri, byte[] secretKey, File cacheDir, List<ITrail> trails) {
        try {
            return checkCacheAndLoadFile(uri, secretKey, cacheDir, trails);
        }
        catch (Exception ex) {
            exceptions.add(ex);
            return false;
        }
    }

    private boolean checkCacheAndLoadFile(URI uri, byte[] secretKey, File cacheDir, List<ITrail> trails) throws Exception {
        if (cacheTTL <= 0) {
            System.out.println("Thread #" + Thread.currentThread().getId() + ": cache disabled");
            return false;
        }

        Path cachePath = getCachePath(uri, cacheDir);
        File cacheFile = new File(cachePath.toString());
        if (!cacheFile.exists())
            return false;

        BasicFileAttributes cacheFileAttrs = Files.readAttributes(cachePath, BasicFileAttributes.class);
        Date createdAt = new Date(cacheFileAttrs.creationTime().to(TimeUnit.MILLISECONDS));
        Date now = new Date();
        if (createdAt.getTime() >  now.getTime())
            return false;

        Duration delta = DateUtilities.getDuration(now, createdAt);
        long seconds = delta.get(ChronoUnit.SECONDS);
        if (seconds > cacheTTL)
            return false;

        try (InputStream cacheStream = Files.newInputStream(cacheFile.toPath())) {
            loadFromFile(cacheStream, secretKey, trails);
        }

        System.out.println("Thread #" + Thread.currentThread().getId() + ": update loaded from cache");
        return true;
    }

    private void saveToCache(URI uri, File cacheDir, InputStream inputStream) throws NoSuchAlgorithmException, IOException {
        if (cacheTTL <= 0) {
            System.out.println("Thread #" + Thread.currentThread().getId() + ": cache disabled");
            return;
        }

        Path cachePath = getCachePath(uri, cacheDir);
        File cacheFile = new File(cachePath.toString());

        boolean exists = false;
        if (cacheFile.exists())
            exists = !cacheFile.delete();

        if (exists)
            return;

        try (OutputStream outputStream = Files.newOutputStream(cacheFile.toPath())) {
            IOUtilities.copy(inputStream, outputStream);
        }
        System.out.println("Thread #" + Thread.currentThread().getId() + ": update saved in cache");
    }

    private static String md5(String str) throws NoSuchAlgorithmException {
        MessageDigest digest = java.security.MessageDigest
                .getInstance("MD5");
        digest.update(str.getBytes());
        byte[] digestBytes = digest.digest();

        StringBuilder hexString = new StringBuilder();
        for (byte digestByte : digestBytes) {
            String h = Integer.toHexString(0xFF & digestByte);
            while (h.length() < 2)
                h = "0" + h;
            hexString.append(h);
        }
        return hexString.toString();
    }
}
