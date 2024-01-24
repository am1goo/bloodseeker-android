package com.am1goo.bloodseeker.update;

import java.io.File;
import java.util.List;

public interface IRemoteUpdateRunnable extends Runnable {

    File getCacheDir();
    List<Exception> getExceptions();
}
