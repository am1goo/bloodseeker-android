package com.am1goo.bloodseeker.android;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.jar.JarFile;

public class Utilities {

    private final static String unityPlayer = "com.unity3d.player.UnityPlayer";

    public static Activity getUnityPlayerActivity() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Class<?> activityClass = Class.forName(unityPlayer);

        try {
            Field fi = activityClass.getDeclaredField("currentActivity");
            return (Activity) fi.get(null);
        }
        catch (Exception ex) {
            throw ex;
        }
    }

    public static JarFile getBaseApk(Activity activity, List<Exception> exceptions) {
        if (activity == null)
            return null;

        Context ctx = activity.getBaseContext();
        ApplicationInfo appInfo = ctx.getApplicationInfo();
        try {
            return new JarFile(appInfo.sourceDir);
        } catch (Exception ex) {
            exceptions.add(ex);
            return null;
        }
    }

    public static Class<?> getClass(String className, List<Exception> exceptions) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException ex) {
            //do nothing
        }
        catch (Exception ex) {
            exceptions.add(ex);
        }
        return null;
    }

    public static void copy(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, len);
        }
    }

    public static byte[] readAllBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[8 * 1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.flush();
        return outputStream.toByteArray();
    }

    public static String readAllText(InputStream inputStream) {
        try {
            int bufferSize = 1024;
            char[] buffer = new char[bufferSize];
            StringBuilder out = new StringBuilder();
            java.io.Reader in = new java.io.InputStreamReader(inputStream);
            for (int numRead; (numRead = in.read(buffer, 0, buffer.length)) > 0; ) {
                out.append(buffer, 0, numRead);
            }
            return out.toString();
        }
        catch (Exception ex) {
            return "";
        }
    }
}
