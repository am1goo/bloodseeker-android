package com.am1goo.bloodseeker.android;

import com.am1goo.bloodseeker.IResult;
import com.am1goo.bloodseeker.TrailRunnable;

import java.util.ArrayList;
import java.util.List;

public class AndroidTrailRunnable extends TrailRunnable {

    public AndroidTrailRunnable(AndroidAppContext appContext, IAndroidTrail trail) {
        this(appContext, trail, new ArrayList<>(), new ArrayList<>());
    }

    public AndroidTrailRunnable(AndroidAppContext appContext, IAndroidTrail trail, List<IResult> results, List<Exception> exceptions) {
        super(trail, results, exceptions);
        trail.setContext(appContext);
    }
}
