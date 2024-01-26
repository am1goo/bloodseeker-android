package com.am1goo.bloodseeker.update;

import com.am1goo.bloodseeker.trails.ClassNameTrail;
import com.am1goo.bloodseeker.trails.DelayTrail;
import com.am1goo.bloodseeker.trails.LibraryTrail;
import com.am1goo.bloodseeker.utilities.IOUtilities;
import com.am1goo.bloodseeker.utilities.StringUtilities;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class RemoteUpdateFilesTest {

    @Test
    public void testSaveAndLoadInMemory() throws Exception {
        String randomSecretKey = StringUtilities.getRandomString(76);
        List<IRemoteUpdateTrail> trails = new ArrayList<>();
        trails.add(new DelayTrail(50));
        trails.add(new ClassNameTrail("java.lang.String"));
        trails.add(new LibraryTrail("standard"));
        trails.add(new ClassNameTrail("java.lang.Number"));

        RemoteUpdateFile src = new RemoteUpdateFile(randomSecretKey.getBytes("utf-8"));
        src.setTrails(trails);

        byte[] bytes = null;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            src.save(outputStream);
            bytes = outputStream.toByteArray();
        }

        RemoteUpdateFile dest = new RemoteUpdateFile(randomSecretKey.getBytes("utf-8"));
        try (InputStream inputStream = new ByteArrayInputStream(bytes)) {
            dest.load(inputStream);
        }

        Assert.assertEquals(src.getVersion(), dest.getVersion());
        Assert.assertArrayEquals(src.getTrails().toArray(), dest.getTrails().toArray());
    }

    @Test
    public void testLoadFromProject() throws Exception {
        String randomSecretKey = "0123456789ABCDEF"; //from file 'examples/remote-update-project-hierarchy/project.json'
        Path pathToProject = Paths.get("examples/remote-update-project-hierarchy/");

        RemoteUpdateFile src = RemoteUpdateFiles.fromDirectory(pathToProject);
        Assert.assertNotNull(src);

        byte[] bytes = src.toByteArray();
        RemoteUpdateFile dest = new RemoteUpdateFile(randomSecretKey.getBytes("utf-8"));
        try (InputStream inputStream = new ByteArrayInputStream(bytes)) {
            dest.load(inputStream);
        }

        Assert.assertArrayEquals(src.getTrails().toArray(), dest.getTrails().toArray());

        Path tempFile = Paths.get(IOUtilities.getTempDir(), "test_" + System.currentTimeMillis() + ".bmx");
        try (OutputStream outputStream = Files.newOutputStream(tempFile.toFile().toPath())) {
            dest.save(outputStream);
        }

        Exception result = RemoteUpdateFiles.test(pathToProject, tempFile);
        Assert.assertNull(result);
    }
}
