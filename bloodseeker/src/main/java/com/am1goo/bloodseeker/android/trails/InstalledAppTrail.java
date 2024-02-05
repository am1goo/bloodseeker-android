package com.am1goo.bloodseeker.android.trails;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.am1goo.bloodseeker.BloodseekerExceptions;
import com.am1goo.bloodseeker.IResult;
import com.am1goo.bloodseeker.android.AndroidAppContext;
import com.am1goo.bloodseeker.update.IRemoteUpdateTrail;
import com.am1goo.bloodseeker.update.RemoteUpdateReader;
import com.am1goo.bloodseeker.update.RemoteUpdateWriter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InstalledAppTrail extends BaseAndroidTrail implements IRemoteUpdateTrail {

    private static final short VERSION = 1;
    private short version;
    private String[] appNames;

    private InstalledAppTrail() {
    }

    public InstalledAppTrail(String[] appNames) {
        this.version = VERSION;
        this.appNames = appNames;
    }

    @Override
    public void load(RemoteUpdateReader reader) throws Exception {
        version = reader.readVersion();
        appNames = reader.readStringArray();
    }

    @Override
    public void save(RemoteUpdateWriter writer) throws Exception {
        writer.writeVersion(version);
        writer.writeStringArray(appNames, "utf-8");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        InstalledAppTrail that = (InstalledAppTrail) o;
        return Arrays.equals(appNames, that.appNames);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(appNames);
    }

    @Override
    public void seek(List<IResult> result, BloodseekerExceptions exceptions) {
        if (appNames == null)
            return;

        AndroidAppContext context = getContext();
        if (context == null)
            return;

        Activity activity = context.getActivity();
        if (activity == null)
            return;

        PackageManager packageManager = activity.getPackageManager();

        List<String> packageNames = new ArrayList<>();
        getInstalledPackages(packageManager, packageNames);
        for (String packageName : packageNames) {
            for (String appName : appNames) {
                if (appName == null)
                    continue;

                if (packageName.matches(appName)) {
                    result.add(new Result(packageName, appName));
                }
            }
        }
    }

    private void getInstalledPackages(@Nullable PackageManager packageManager, List<String> result) {
        if (packageManager == null)
            return;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
            for (PackageInfo info : packageInfos) {
                getInstalledPackege(info.applicationInfo, result);
            }
        }
        else {
            List<ApplicationInfo> applicationInfos = packageManager.getInstalledApplications(0);
            for (ApplicationInfo info : applicationInfos) {
                getInstalledPackege(info, result);
            }
        }
    }

    private void getInstalledPackege(@NonNull ApplicationInfo applicationInfo, List<String> result) {
        if (hasFlag(applicationInfo, ApplicationInfo.FLAG_SYSTEM))
            return;

        result.add(applicationInfo.packageName);
    }

    private static boolean hasFlag(@NonNull ApplicationInfo applicationInfo, int flag) {
        return ((applicationInfo.flags & flag) != 0);
    }

    public static class Result implements IResult {
        private final String appName;
        private final String regex;

        public Result(String appName, String regex) {
            this.appName = appName;
            this.regex = regex;
        }

        @NonNull
        @Override
        public String toString() {
            return "App " + appName + " found on device by regex '" + regex + "'";
        }
    }
}
