package com.am1goo.bloodseeker.android.trails;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;

import com.am1goo.bloodseeker.android.IResult;
import com.am1goo.bloodseeker.android.ITrail;
import com.am1goo.bloodseeker.android.Utilities;

import java.io.IOException;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class PathInApkTrail implements ITrail {

    private final String[] pathsInApk;

    public PathInApkTrail(String[] pathsInApk) {
        this.pathsInApk = pathsInApk;
    }

    @Override
    public void seek(List<IResult> result, List<Exception> exceptions) {
        Activity activity = null;
        try{
            activity = Utilities.getUnityPlayerActivity();
        }
        catch (Exception ex) {
            exceptions.add(ex);
        }

        if (activity == null)
            return;

        Context ctx = activity.getBaseContext();
        ApplicationInfo appInfo = ctx.getApplicationInfo();
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(appInfo.sourceDir);

            for (String pathInApk : pathsInApk) {
                ZipEntry zipEntry = jarFile.getEntry(pathInApk);
                if (zipEntry == null)
                    continue;

                result.add(new Result(pathInApk));
            }
        }
        catch (IOException ex) {
            exceptions.add(ex);
        }
        finally {
            if (jarFile != null) {
                try {
                    jarFile.close();
                } catch (IOException ex) {
                    exceptions.add(ex);
                }
            }
        }
    }

    public class Result implements IResult {
        private final String pathInApk;

        public Result(String pathInApk) {
            this.pathInApk = pathInApk;
        }

        @Override
        public String toString() {
            return "Entry '" + pathInApk + "' found in apk";
        }
    }
}
