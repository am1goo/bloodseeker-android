package com.am1goo.bloodseeker.update;

import com.am1goo.bloodseeker.Condition;
import com.am1goo.bloodseeker.trails.ClassNameTrail;
import com.am1goo.bloodseeker.trails.DelayTrail;
import com.am1goo.bloodseeker.trails.JavaSystemPropertyTrail;
import com.am1goo.bloodseeker.utilities.IOUtilities;
import com.am1goo.bloodseeker.utilities.PathUtilities;
import com.am1goo.bloodseeker.utilities.StringUtilities;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class RemoteUpdateFilesTest {

    @Test
    public void testLoadFromProject() throws Exception {
        String randomSecretKey = "0123456789ABCDEF"; //from file 'examples/remote-update-project-hierarchy/project.json'
        File pathToProject = new File("examples/remote-update-project-hierarchy/");

        RemoteUpdateFile src = RemoteUpdateFiles.fromDirectory(pathToProject);
        Assert.assertNotNull(src);

        byte[] bytes = src.toByteArray();
        RemoteUpdateFile dest = new RemoteUpdateFile(randomSecretKey.getBytes("utf-8"));
        try (InputStream inputStream = new ByteArrayInputStream(bytes)) {
            dest.load(inputStream);
        }

        Assert.assertArrayEquals(src.getTrails().toArray(), dest.getTrails().toArray());

        String tempPath = PathUtilities.join(IOUtilities.getTempDir(), "test_" + System.currentTimeMillis() + ".bmx");
        File tempFile = new File(tempPath);
        try (OutputStream outputStream = new FileOutputStream(tempFile)) {
            dest.save(outputStream);
        }

        Exception result = RemoteUpdateFiles.test(pathToProject, tempFile);
        Assert.assertNull(result);
    }
}
