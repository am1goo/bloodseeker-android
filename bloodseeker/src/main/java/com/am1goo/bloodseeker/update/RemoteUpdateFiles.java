package com.am1goo.bloodseeker.update;

import com.am1goo.bloodseeker.utilities.IOUtilities;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class RemoteUpdateFiles {

    public static RemoteUpdateFile fromDirectory(String pathToDirectory) throws Exception {
        Path path = Paths.get(pathToDirectory);
        if (!path.isAbsolute()) {
            path = path.toAbsolutePath();
        }
        return fromDirectory(path.toFile());
    }

    public static RemoteUpdateFile fromDirectory(File file) throws Exception {
        if (!file.isDirectory())
            throw new Exception("project path '" + file.getPath() + "' should be directory");

        if (!file.exists())
            throw new Exception("project directory " + file.getPath() + " doesn't exists");

        Path path = Paths.get(file.getPath(), "project.json");
        try (InputStream inputStream = Files.newInputStream(path)) {
            String json = IOUtilities.readAllText(inputStream);
            Scheme scheme = new Gson().fromJson(json, Scheme.class);
            return fromScheme(scheme, file);
        }
    }

    public static RemoteUpdateFile fromScheme(Scheme scheme, File directory) throws Exception {
        byte[] secretKey = scheme.secretKey.getBytes("utf-8");

        if (scheme.files == null)
            throw new Exception("files are not defined");

        List<IRemoteUpdateTrail> trails = new ArrayList<>();
        for (Scheme.File schemeFile : scheme.files) {
            Class<?> clazz = Class.forName(schemeFile.className);
            Path path = Paths.get(directory.getPath(), schemeFile.path);
            try (InputStream inputStream = Files.newInputStream(path)) {
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

    public static class Scheme {
        public String secretKey;
        public File[] files;

        public static class File {
            public String className;
            public String path;
        }
    }
}