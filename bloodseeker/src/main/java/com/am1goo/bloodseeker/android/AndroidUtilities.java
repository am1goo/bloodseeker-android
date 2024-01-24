package com.am1goo.bloodseeker.android;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.jar.JarFile;

public class AndroidUtilities {

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
}
