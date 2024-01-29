package com.am1goo.bloodseeker.android.trails;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.am1goo.bloodseeker.BloodseekerExceptions;
import com.am1goo.bloodseeker.IResult;
import com.am1goo.bloodseeker.android.AndroidAppContext;
import com.am1goo.bloodseeker.update.IRemoteUpdateTrail;
import com.am1goo.bloodseeker.update.RemoteUpdateReader;
import com.am1goo.bloodseeker.update.RemoteUpdateWriter;
import com.am1goo.bloodseeker.utilities.Crc32Utilities;

import java.io.File;
import java.util.List;

public class ApkChecksumTrail extends BaseAndroidTrail implements IRemoteUpdateTrail {
    private static final short VERSION = 1;
    private short version;
    private long checksum;

    public ApkChecksumTrail(long checksum) {
        this();
        this.checksum = checksum;
    }

    public ApkChecksumTrail() {
        version = VERSION;
    }

    @Override
    public void load(RemoteUpdateReader reader) throws Exception {
        this.version = reader.readVersion();
        this.checksum = reader.readLong();
    }

    @Override
    public void save(RemoteUpdateWriter writer) throws Exception {
        writer.writeVersion(version);
        writer.writeLong(checksum);
    }

    @Override
    public void seek(List<IResult> result, BloodseekerExceptions exceptions) {
        if (this.checksum <= 0) {
            //checksum unwanted
            return;
        }

        AndroidAppContext context = getContext();
        if (context == null)
            return;

        Activity activity = context.getActivity();
        if (activity == null)
            return;

        File file = context.getBaseApkFile();
        long checksum = Crc32Utilities.getCrc32(file);
        if (checksum <= 0) {
            //checksum cannot be determined
            return;
        }

        if (this.checksum == checksum) {
            //everything is okay
            return;
        }

        result.add(new Result(checksum, this.checksum));
    }

    public static class Result implements IResult {
        private final long actualChecksum;
        private final long expectedChecksum;

        public Result(long actualChecksum, long expectedChecksum) {
            this.actualChecksum = actualChecksum;
            this.expectedChecksum = expectedChecksum;
        }

        @NonNull
        @Override
        public String toString() {
            return "Checksum mismatch, actual=" + actualChecksum + ", expected=" + expectedChecksum;
        }
    }
}
