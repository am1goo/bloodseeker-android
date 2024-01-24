package com.am1goo.bloodseeker.update;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class RemoteUpdateFilesTest {

    @Test
    public void testLoadFromProject() throws Exception {

        RemoteUpdateFile src = RemoteUpdateFiles.fromDirectory("examples/remote-update-project-hierarchy/");
        Assert.assertNotNull(src);

        byte[] bytes = src.toByteArray();
        RemoteUpdateFile dest = new RemoteUpdateFile("0123456789ABCDEF".getBytes("utf-8"));
        try (InputStream inputStream = new ByteArrayInputStream(bytes)) {
            dest.load(inputStream);
        }
        Assert.assertArrayEquals(src.getTrails().toArray(), dest.getTrails().toArray());
    }
}
