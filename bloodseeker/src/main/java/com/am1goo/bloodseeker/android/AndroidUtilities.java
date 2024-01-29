package com.am1goo.bloodseeker.android;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;

import com.am1goo.bloodseeker.BloodseekerExceptions;

import java.io.File;
import java.lang.reflect.Field;
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

    public static File getBaseApkFile(Activity activity) {
        if (activity == null)
            return null;

        Context ctx = activity.getBaseContext();
        ApplicationInfo appInfo = ctx.getApplicationInfo();
        return new File(appInfo.sourceDir);
    }

    public static JarFile getBaseApkJar(Activity activity, BloodseekerExceptions exceptions) {
        File file = getBaseApkFile(activity);
        return getJar(file, exceptions);
    }

    public static JarFile getJar(File file, BloodseekerExceptions exceptions) {
        try {
            return new JarFile(file);
        } catch (Exception ex) {
            exceptions.add(AndroidUtilities.class, ex);
            return null;
        }
    }
}
