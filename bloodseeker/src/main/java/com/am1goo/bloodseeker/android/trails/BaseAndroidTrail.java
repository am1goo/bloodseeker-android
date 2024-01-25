package com.am1goo.bloodseeker.android.trails;

import com.am1goo.bloodseeker.BloodseekerExceptions;
import com.am1goo.bloodseeker.android.AndroidAppContext;
import com.am1goo.bloodseeker.IResult;
import com.am1goo.bloodseeker.android.IAndroidTrail;

import java.util.List;

public class BaseAndroidTrail implements IAndroidTrail {

    private transient AndroidAppContext context;

    @Override
    public void seek(List<IResult> result, BloodseekerExceptions exceptions) {
        //do nothing
    }

    @Override
    public AndroidAppContext getContext() {
        return context;
    }

    @Override
    public void setContext(AndroidAppContext context) {
        this.context = context;
    }
}
