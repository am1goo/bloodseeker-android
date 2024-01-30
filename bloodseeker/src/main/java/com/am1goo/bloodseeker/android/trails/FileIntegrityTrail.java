package com.am1goo.bloodseeker.android.trails;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.am1goo.bloodseeker.BloodseekerExceptions;
import com.am1goo.bloodseeker.android.AndroidAppContext;
import com.am1goo.bloodseeker.IResult;
import com.am1goo.bloodseeker.update.RemoteUpdateSerializable;
import com.am1goo.bloodseeker.utilities.StringUtilities;
import com.am1goo.bloodseeker.update.IRemoteUpdateTrail;
import com.am1goo.bloodseeker.update.RemoteUpdateReader;
import com.am1goo.bloodseeker.update.RemoteUpdateWriter;

import java.util.Arrays;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class FileIntegrityTrail extends BaseAndroidTrail implements IRemoteUpdateTrail {
    private static final short VERSION = 1;
    private short version;
    private FileInApk[] filesInApk;

    public FileIntegrityTrail(FileInApk[] filesInApk) {
        this.version = VERSION;
        this.filesInApk = filesInApk;
    }

    @Override
    public void load(RemoteUpdateReader reader) throws Exception {
        version = reader.readVersion();
        filesInApk = reader.readArray(FileInApk.class);
    }

    @Override
    public void save(RemoteUpdateWriter writer) throws Exception {
        writer.writeVersion(version);
        writer.writeArray(filesInApk);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        FileIntegrityTrail that = (FileIntegrityTrail) o;
        return Arrays.equals(filesInApk, that.filesInApk);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(filesInApk);
    }

    @Override
    public void seek(List<IResult> result, BloodseekerExceptions exceptions) {
        if (filesInApk == null)
            return;

        AndroidAppContext context = getContext();
        if (context == null)
            return;

        Activity activity = context.getActivity();
        if (activity == null)
            return;

        JarFile jarFile = context.getBaseApkJar();
        if (jarFile == null)
            return;

        for (FileInApk fileInApk : filesInApk) {
            if (fileInApk == null)
                continue;

            String pathInApk = fileInApk.getPathInApk();
            if (pathInApk == null)
                continue;

            pathInApk = StringUtilities.trim(pathInApk, '/');
            ZipEntry zipEntry = jarFile.getEntry(pathInApk);
            if (zipEntry == null) {
                result.add(new FileNotFoundResult(pathInApk));
                continue;
            }

            long expectedChecksum = fileInApk.getChecksum();
            if (expectedChecksum <= 0)
                continue;

            long actualChecksum = zipEntry.getCrc();
            if (expectedChecksum == actualChecksum)
                continue;

            result.add(new ChecksumMismatchedResult(pathInApk, actualChecksum, expectedChecksum));
        }
    }

    public static class FileInApk implements RemoteUpdateSerializable {
        private static final short VERSION = 1;
        private short version;
        private String pathInApk;
        private long checksum;

        public FileInApk(String pathInApk) {
            this(pathInApk, 0);
        }

        public FileInApk(String pathInApk, long checksum) {
            this();
            this.pathInApk = pathInApk;
            this.checksum = checksum;
        }

        public FileInApk() {
            version = VERSION;
        }

        @Override
        public void load(RemoteUpdateReader reader) throws Exception {
            version = reader.readVersion();
            pathInApk = reader.readString();
            checksum = reader.readLong();
        }

        @Override
        public void save(RemoteUpdateWriter writer) throws Exception {
            writer.writeVersion(version);
            writer.writeString(pathInApk, "utf-8");
            writer.writeLong(checksum);
        }

        public String getPathInApk() {
            return pathInApk;
        }

        public long getChecksum() {
            return checksum;
        }
    }

    public static class FileNotFoundResult implements IResult {
        private final String pathInApk;

        public FileNotFoundResult(String pathInApk) {
            this.pathInApk = pathInApk;
        }

        @NonNull
        @Override
        public String toString() {
            return "Entry '" + pathInApk + "' not found in apk";
        }
    }

    public static class ChecksumMismatchedResult implements IResult {
        private final String pathInApk;
        private final long actualChecksum;
        private final long expectedChecksum;

        public ChecksumMismatchedResult(String pathInApk, long actualChecksum, long expectedChecksum) {
            this.pathInApk = pathInApk;
            this.actualChecksum = actualChecksum;
            this.expectedChecksum = expectedChecksum;
        }

        @NonNull
        @Override
        public String toString() {
            return "Entry '" + pathInApk + "' checksum mismatched, actual=" + actualChecksum + ", expected=" + expectedChecksum;
        }
    }
}
