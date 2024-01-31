package com.am1goo.bloodseeker.android.trails;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import com.am1goo.bloodseeker.BloodseekerExceptions;
import com.am1goo.bloodseeker.IResult;
import com.am1goo.bloodseeker.android.AndroidAppContext;
import com.am1goo.bloodseeker.update.IRemoteUpdateTrail;
import com.am1goo.bloodseeker.update.RemoteUpdateFile;
import com.am1goo.bloodseeker.update.RemoteUpdateReader;
import com.am1goo.bloodseeker.update.RemoteUpdateWriter;

public class LibraryTrail extends BaseAndroidTrail implements IRemoteUpdateTrail {

    private String[] libraryNames;

    public LibraryTrail() {
    }

    public LibraryTrail(String libraryName) {
        this(new String[] { libraryName } );
    }

    public LibraryTrail(String[] libraryNames) {
        this.libraryNames = libraryNames;
    }

    @Override
    public void load(RemoteUpdateReader reader) throws IOException {
        libraryNames = reader.readStringArray();
    }

    @Override
    public void save(RemoteUpdateWriter writer) throws IOException {
        writer.writeStringArray(libraryNames, RemoteUpdateFile.CHARSET_NAME);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        LibraryTrail that = (LibraryTrail) o;
        return Arrays.equals(libraryNames, that.libraryNames);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(libraryNames);
    }

    @Override
    public void seek(List<IResult> result, BloodseekerExceptions exceptions) {
        if (libraryNames == null)
            return;

        AndroidAppContext context = getContext();
        if (context == null)
            return;

        File libraryDir = context.getLibraryDir();
        if (libraryDir == null)
            return;

        for (String libraryName : libraryNames) {
            if (libraryName == null)
                continue;

            final String mappedName = System.mapLibraryName(libraryName);
            final Path libraryPath = Paths.get(libraryDir.getPath(), mappedName);
            final File libraryFile = libraryPath.toFile();
            if (!libraryFile.exists())
                continue;

            result.add(new Result(libraryName, mappedName));
        }
    }

    public static class Result implements IResult {
        private final String libraryName;
        private final String mappedName;

        public Result(String libraryName, String mappedName) {
            this.libraryName = libraryName;
            this.mappedName = mappedName;
        }

        @NonNull
        @Override
        public String toString() {
            return "Library '" + libraryName + "' found as '" + mappedName + "'";
        }
    }
}
