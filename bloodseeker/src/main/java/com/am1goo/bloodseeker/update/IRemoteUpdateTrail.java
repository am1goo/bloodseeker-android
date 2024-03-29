package com.am1goo.bloodseeker.update;

import com.am1goo.bloodseeker.ITrail;

public interface IRemoteUpdateTrail extends ITrail {

    void load(RemoteUpdateReader reader) throws Exception;
    void save(RemoteUpdateWriter writer) throws Exception;

}
