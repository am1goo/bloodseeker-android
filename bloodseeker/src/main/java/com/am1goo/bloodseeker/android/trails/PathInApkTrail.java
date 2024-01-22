package com.am1goo.bloodseeker.android.trails;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;

import com.am1goo.bloodseeker.android.AppContext;
import com.am1goo.bloodseeker.android.IResult;
import com.am1goo.bloodseeker.android.ITrail;
import com.am1goo.bloodseeker.android.Utilities;

import java.io.IOException;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class PathInApkTrail extends BaseTrail {

    private final String[] pathsInApk;

    public PathInApkTrail(String[] pathsInApk) {
        this.pathsInApk = pathsInApk;
    }

    @Override
    public void seek(AppContext context, List<IResult> result, List<Exception> exceptions) {
        if (pathsInApk == null)
            return;

        Activity activity = context.getActivity();
        if (activity == null)
            return;

        JarFile jarFile = context.getBaseApk();
        if (jarFile == null)
            return;

        for (String pathInApk : pathsInApk) {
            if (pathInApk == null)
                continue;

            ZipEntry zipEntry = jarFile.getEntry(pathInApk);
            if (zipEntry == null)
                continue;

            result.add(new Result(pathInApk));
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
