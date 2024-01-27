package com.am1goo.bloodseeker.android.update;

import com.am1goo.bloodseeker.android.trails.AndroidManifestXmlTrail;
import com.am1goo.bloodseeker.trails.ClassNameTrail;
import com.am1goo.bloodseeker.android.trails.PackageNameTrail;
import com.am1goo.bloodseeker.android.trails.PathInApkTrail;
import com.am1goo.bloodseeker.trails.DelayTrail;
import com.am1goo.bloodseeker.update.IRemoteUpdateTrail;
import com.am1goo.bloodseeker.update.RemoteUpdateFile;

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
        trails.add(new AndroidManifestXmlTrail(new AndroidManifestXmlTrail.Looker(new String[]
                { "application", "provider" },
                "android:name",
                "com.facebook.internal.FacebookInitProvider",
                AndroidManifestXmlTrail.Looker.Condition.Eq)));
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
