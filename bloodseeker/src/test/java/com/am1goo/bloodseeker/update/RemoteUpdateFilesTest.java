package com.am1goo.bloodseeker.update;

import com.am1goo.bloodseeker.utilities.IOUtilities;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RemoteUpdateFilesTest {

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
