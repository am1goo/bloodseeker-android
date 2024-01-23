package com.am1goo.bloodseeker.android.update;

import com.am1goo.bloodseeker.android.ITrail;

import java.io.IOException;

public interface IRemoteUpdateTrail extends ITrail {

    void load(RemoteUpdateReader reader) throws IOException;
    void save(RemoteUpdateWriter writer) throws IOException;

}
