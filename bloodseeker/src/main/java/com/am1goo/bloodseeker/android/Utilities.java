package com.am1goo.bloodseeker.android;

import android.app.Activity;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.List;

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
}
