package com.am1goo.bloodseeker.update;

import com.am1goo.bloodseeker.BloodseekerExceptions;

import java.io.File;

public interface IRemoteUpdateRunnable extends Runnable {

    File getCacheDir();
    BloodseekerExceptions getExceptions();
}
