package com.am1goo.bloodseeker.android;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.jar.JarFile;

public class Bloodseeker {
	
	private List<ITrail> trails;

    public Bloodseeker() {
		super();
		this.trails = new ArrayList<ITrail>();
    }

    public boolean addTrail(ITrail trail) {
    	if (trail == null)
    		return false;

    	return trails.add(trail);
    }

    public Report seek()
    {
        List<IResult> results = new ArrayList<IResult>();
        List<Exception> exceptions = new ArrayList<Exception>();

        Activity activity;
        try {
            activity = Utilities.getUnityPlayerActivity();
        }
        catch (Exception ex) {
            exceptions.add(ex);
            activity = null;
        }

        JarFile jarFile = null;
        if (activity != null) {
            Context ctx = activity.getBaseContext();
            ApplicationInfo appInfo = ctx.getApplicationInfo();
            try {
                jarFile = new JarFile(appInfo.sourceDir);
            }
            catch (Exception ex) {
                exceptions.add(ex);
            }
        }

        AppContext context = new AppContext(activity, jarFile);
        for (int i = 0; i < trails.size(); ++i) {
            try {
            	ITrail trail = trails.get(i);
                trail.seek(context, results, exceptions);
            }
            catch (Exception ex) {
                exceptions.add(ex);
            }
        }

        if (jarFile != null) {
            try {
                jarFile.close();
            }
            catch (IOException ex) {
                exceptions.add(ex);
            }
        }
        return new Report(results, exceptions);
    }
}
