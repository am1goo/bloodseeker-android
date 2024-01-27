package com.am1goo.bloodseeker.update;

public interface RemoteUpdateSerializable {
    void load(RemoteUpdateReader reader) throws Exception;
    void save(RemoteUpdateWriter writer) throws Exception;
}
