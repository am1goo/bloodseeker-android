package com.am1goo.bloodseeker.trails;

import com.am1goo.bloodseeker.IResult;
import com.am1goo.bloodseeker.ITrail;
import com.am1goo.bloodseeker.update.IRemoteUpdateTrail;
import com.am1goo.bloodseeker.update.RemoteUpdateReader;
import com.am1goo.bloodseeker.update.RemoteUpdateWriter;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class DelayTrail implements IRemoteUpdateTrail, ITrail {

    private long milliseconds;

    public DelayTrail() {
    }

    public DelayTrail(long milliseconds) {
        this.milliseconds = milliseconds;
    }

    @Override
    public void load(RemoteUpdateReader reader) throws IOException {
        milliseconds = reader.readLong();
    }

    @Override
    public void save(RemoteUpdateWriter writer) throws IOException {
        writer.writeLong(milliseconds);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        DelayTrail that = (DelayTrail) o;
        return milliseconds == that.milliseconds;
    }

    @Override
    public int hashCode() {
        return Objects.hash(milliseconds);
    }

    @Override
    public void seek(List<IResult> result, List<Exception> exceptions) {
        try {
            Thread.sleep(milliseconds);
        }
        catch (InterruptedException ex) {
            exceptions.add(ex);
        }
    }
}
