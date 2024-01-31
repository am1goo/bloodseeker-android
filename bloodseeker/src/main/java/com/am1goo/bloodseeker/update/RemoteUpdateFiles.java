package com.am1goo.bloodseeker.update;

import com.am1goo.bloodseeker.utilities.IOUtilities;
import com.am1goo.bloodseeker.utilities.PathUtilities;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class RemoteUpdateFiles {

    public static Exception test(String pathToDirectory, String pathToBmxFile) {
        File dirFile = new File(pathToDirectory);
        if (!dirFile.isAbsolute()) {
            dirFile = dirFile.getAbsoluteFile();
        }

        File bmxFile = new File(pathToBmxFile);
        if (!bmxFile.isAbsolute()) {
            bmxFile = bmxFile.getAbsoluteFile();
        }
        return test(dirFile, bmxFile);
    }

    public static Exception test(File pathToDirectory, File pathToBmxFile) {
        try {
            Scheme scheme = getScheme(pathToDirectory);
            byte[] secretKey = scheme.secretKey.getBytes("utf-8");
            RemoteUpdateFile file = new RemoteUpdateFile(secretKey);
            try (InputStream inputStream = new FileInputStream(pathToBmxFile)) {
                file.load(inputStream);
                return null;
            }
        }
        catch (Exception ex) {
            System.err.println(ex);
            return ex;
        }
    }

    public static RemoteUpdateFile fromDirectory(String pathToDirectory) throws Exception {
        File file = new File(pathToDirectory);
        if (!file.isAbsolute()) {
            file = file.getAbsoluteFile();
        }
        return fromDirectory(file);
    }

    public static RemoteUpdateFile fromDirectory(File directory) throws Exception {
        Scheme scheme = getScheme(directory);
        return fromScheme(scheme, directory);
    }

    public static RemoteUpdateFile fromScheme(Scheme scheme, File directory) throws Exception {
        byte[] secretKey = scheme.secretKey.getBytes("utf-8");

        if (scheme.files == null)
            throw new Exception("files are not defined");

        List<IRemoteUpdateTrail> trails = new ArrayList<>();
        for (Scheme.File schemeFile : scheme.files) {
            Class<?> clazz = Class.forName(schemeFile.className);
            String path = PathUtilities.join(directory.getPath(), schemeFile.path);
            try (InputStream inputStream = new FileInputStream(path)) {
                String json = IOUtilities.readAllText(inputStream);
                Object obj = new Gson().fromJson(json, clazz);
                if (!(obj instanceof IRemoteUpdateTrail))
                    throw new Exception("unsupported class " + obj.getClass() + ", it should be one of " + IRemoteUpdateTrail.class.getName() + " implementations");

                IRemoteUpdateTrail trail = (IRemoteUpdateTrail) obj;
                trails.add(trail);
            }
        }

        RemoteUpdateFile file = new RemoteUpdateFile(secretKey);
        file.setTrails(trails);
        return file;
    }

    private static Scheme getScheme(File directory) throws Exception {
        if (!directory.isDirectory())
            throw new Exception("project path '" + directory.getPath() + "' should be directory");

        if (!directory.exists())
            throw new Exception("project directory " + directory.getPath() + " doesn't exists");

        String path = PathUtilities.join(directory.getPath(), "project.json");
        try (InputStream inputStream = new FileInputStream(path)) {
            String json = IOUtilities.readAllText(inputStream);
            return new Gson().fromJson(json, Scheme.class);
        }
    }

    public static class Scheme {
        public String secretKey;
        public File[] files;

        public static class File {
            public String className;
            public String path;
        }
    }
}
