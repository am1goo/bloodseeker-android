package com.am1goo.bloodseeker.android.update;

import com.am1goo.bloodseeker.android.trails.ClassNameTrail;
import com.am1goo.bloodseeker.android.trails.PackageNameTrail;
import com.am1goo.bloodseeker.android.trails.PathInApkTrail;
import com.am1goo.bloodseeker.android.trails.tests.DelayTrail;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class RemoteUpdateFileTest {

    @Test
    public void testLoadAndSave() throws Exception {

        byte[] decryptionKey = "0123456789ABCDEF".getBytes("utf-8");
        RemoteUpdateFile srcFile = new RemoteUpdateFile(decryptionKey);

        List<IRemoteUpdateTrail> trails = new ArrayList<>();
        trails.add(new ClassNameTrail("java.util.List"));
        trails.add(new ClassNameTrail("java.util.ArrayList"));
        trails.add(new ClassNameTrail("com.some.class.Name"));
        trails.add(new PackageNameTrail("java.util"));
        trails.add(new PathInApkTrail("META-INF/MANIFEST.MF"));
        trails.add(new DelayTrail(1000));
        srcFile.setTrails(trails);

        byte[] bytes;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            srcFile.save(outputStream);
            bytes = outputStream.toByteArray();
        }

        RemoteUpdateFile destFile = new RemoteUpdateFile(decryptionKey);
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes)) {
            destFile.load(inputStream);
        }

        Assert.assertArrayEquals(srcFile.getTrails().toArray(), destFile.getTrails().toArray());
    }
}
