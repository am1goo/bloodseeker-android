package com.am1goo.bloodseeker.android.trails.tests;

import com.am1goo.bloodseeker.android.AppContext;
import com.am1goo.bloodseeker.android.IResult;
import com.am1goo.bloodseeker.android.trails.BaseTrail;

import java.util.List;

public class DelayTrail extends BaseTrail {

    private long milliseconds;

    public DelayTrail(long milliseconds) {
        this.milliseconds = milliseconds;
    }

    @Override
    public void seek(AppContext context, List<IResult> result, List<Exception> exceptions) {
        try {
            Thread.sleep(milliseconds);
        }
        catch (InterruptedException ex) {
            exceptions.add(ex);
        }
    }
}
