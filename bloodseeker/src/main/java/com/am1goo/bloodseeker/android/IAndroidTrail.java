package com.am1goo.bloodseeker.android;

import com.am1goo.bloodseeker.ITrail;

public interface IAndroidTrail extends ITrail {

    AndroidAppContext getContext();
    void setContext(AndroidAppContext context);
}
