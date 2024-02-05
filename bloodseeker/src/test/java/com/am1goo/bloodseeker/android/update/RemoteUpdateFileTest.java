package com.am1goo.bloodseeker.android.update;

import com.am1goo.bloodseeker.Condition;
import com.am1goo.bloodseeker.android.trails.AndroidManifestXmlTrail;
import com.am1goo.bloodseeker.android.trails.AndroidSystemPropertyTrail;
import com.am1goo.bloodseeker.android.trails.FileIntegrityTrail;
import com.am1goo.bloodseeker.android.trails.InstalledAppTrail;
import com.am1goo.bloodseeker.android.trails.LibraryTrail;
import com.am1goo.bloodseeker.trails.ClassNameTrail;
import com.am1goo.bloodseeker.android.trails.PackageNameTrail;
import com.am1goo.bloodseeker.android.trails.PathInApkTrail;
import com.am1goo.bloodseeker.trails.DelayTrail;
import com.am1goo.bloodseeker.trails.JavaSystemPropertyTrail;
import com.am1goo.bloodseeker.update.IRemoteUpdateTrail;
import com.am1goo.bloodseeker.update.RemoteUpdateFile;
import com.am1goo.bloodseeker.utilities.StringUtilities;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class RemoteUpdateFileTest {

    @Test
    public void testLoadAndSave() throws Exception {
        String randomSecretKey = StringUtilities.getRandomString(76);
        List<IRemoteUpdateTrail> trails = new ArrayList<>();
        trails.add(new DelayTrail(50));
        trails.add(new ClassNameTrail("java.lang.String"));
        trails.add(new ClassNameTrail("java.lang.Number"));
        trails.add(new LibraryTrail("unity"));
        trails.add(new LibraryTrail("someLibrary"));
        trails.add(new PackageNameTrail("java.util"));
        trails.add(new PathInApkTrail("META-INF/MANIFEST.MF"));
        trails.add(new JavaSystemPropertyTrail(
                new JavaSystemPropertyTrail.SystemProperty[] {
                        new JavaSystemPropertyTrail.SystemProperty("os.name"),
                        new JavaSystemPropertyTrail.SystemProperty("os.version", Condition.Eq,"10.*"),
                        new JavaSystemPropertyTrail.SystemProperty("os.version",Condition.NonEq, "11.0"),
                        new JavaSystemPropertyTrail.SystemProperty("os.version",Condition.NonEq, "10.0"),
                        new JavaSystemPropertyTrail.SystemProperty("java.version",Condition.Eq, "17.*"),
                }));
        trails.add(new AndroidManifestXmlTrail(new AndroidManifestXmlTrail.Looker[] {
                new AndroidManifestXmlTrail.Looker(new String[]
                        { "application", "provider" },
                        "android:name",
                        "com.facebook.internal.FacebookInitProvider",
                        AndroidManifestXmlTrail.Looker.Condition.Eq)
        }));
        trails.add(new FileIntegrityTrail(new FileIntegrityTrail.FileInApk[] {
                new FileIntegrityTrail.FileInApk("META-INF/MANIFEST.MF", 0),
        }));
        trails.add(new InstalledAppTrail(new String[] {
                "com.any.strange.package.name.one",
                "com.any.strange.package.name.two",
        }));
        trails.add(new AndroidSystemPropertyTrail(
                new AndroidSystemPropertyTrail.SystemProperty[] {
                        new AndroidSystemPropertyTrail.SystemProperty("os.name"),
                        new AndroidSystemPropertyTrail.SystemProperty("os.version",Condition.NonEq, "11.0")
        }));

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
}
