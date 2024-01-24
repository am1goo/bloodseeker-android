package com.am1goo.bloodseeker.android.trails;

import android.app.Activity;

import com.am1goo.bloodseeker.android.AndroidAppContext;
import com.am1goo.bloodseeker.IResult;
import com.am1goo.bloodseeker.utilities.StringUtilities;
import com.am1goo.bloodseeker.update.IRemoteUpdateTrail;
import com.am1goo.bloodseeker.update.RemoteUpdateFile;
import com.am1goo.bloodseeker.update.RemoteUpdateReader;
import com.am1goo.bloodseeker.update.RemoteUpdateWriter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class PathInApkTrail extends BaseAndroidTrail implements IRemoteUpdateTrail {

    private String[] pathsInApk;

    public PathInApkTrail() {
    }

    public PathInApkTrail(String pathInApk) {
        this( new String[] { pathInApk } );
    }

    public PathInApkTrail(String[] pathsInApk) {
        this.pathsInApk = pathsInApk;
    }

    @Override
    public void load(RemoteUpdateReader reader) throws IOException {
        pathsInApk = reader.readStringArray();
    }

    @Override
    public void save(RemoteUpdateWriter writer) throws IOException {
        writer.writeStringArray(pathsInApk, RemoteUpdateFile.CHARSET_NAME);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        PathInApkTrail that = (PathInApkTrail) o;
        return Arrays.equals(pathsInApk, that.pathsInApk);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(pathsInApk);
    }

    @Override
    public void seek(List<IResult> result, List<Exception> exceptions) {
        if (pathsInApk == null)
            return;

        AndroidAppContext context = getContext();
        if (context == null)
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

            pathInApk = StringUtilities.trim(pathInApk, '/');
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
