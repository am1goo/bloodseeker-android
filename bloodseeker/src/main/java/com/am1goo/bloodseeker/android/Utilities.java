package com.am1goo.bloodseeker.android;

import android.app.Activity;

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
}
