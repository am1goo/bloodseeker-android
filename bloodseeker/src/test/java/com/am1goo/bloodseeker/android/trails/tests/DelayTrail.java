package com.am1goo.bloodseeker.android.trails.tests;

import com.am1goo.bloodseeker.android.AppContext;
import com.am1goo.bloodseeker.android.IResult;
import com.am1goo.bloodseeker.android.trails.BaseTrail;
import com.am1goo.bloodseeker.android.update.IRemoteUpdateTrail;
import com.am1goo.bloodseeker.android.update.RemoteUpdateReader;
import com.am1goo.bloodseeker.android.update.RemoteUpdateWriter;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class DelayTrail extends BaseTrail implements IRemoteUpdateTrail {

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
    public void seek(AppContext context, List<IResult> result, List<Exception> exceptions) {
        try {
            Thread.sleep(milliseconds);
        }
        catch (InterruptedException ex) {
            exceptions.add(ex);
        }
    }
}
