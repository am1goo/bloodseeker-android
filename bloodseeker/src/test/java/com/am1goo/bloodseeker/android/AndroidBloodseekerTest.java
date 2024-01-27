package com.am1goo.bloodseeker.android;

import com.am1goo.bloodseeker.Async;
import com.am1goo.bloodseeker.AsyncReport;
import com.am1goo.bloodseeker.Bloodseeker;
import com.am1goo.bloodseeker.BloodseekerTest;
import com.am1goo.bloodseeker.Report;
import com.am1goo.bloodseeker.android.trails.AndroidManifestXmlTrail;
import com.am1goo.bloodseeker.android.trails.PackageNameTrail;
import com.am1goo.bloodseeker.android.trails.PathInApkTrail;
import com.am1goo.bloodseeker.trails.ClassNameTrail;
import com.am1goo.bloodseeker.trails.DelayTrail;
import com.am1goo.bloodseeker.update.RemoteUpdateConfig;
import com.am1goo.bloodseeker.update.RemoteUpdateFile;
import com.am1goo.bloodseeker.utilities.StringUtilities;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class AndroidBloodseekerTest {

    private static final String SECRET_KEY = StringUtilities.getRandomString(85);

    @Test
    public void testSeekAsync() throws InterruptedException, IOException {
        AndroidBloodseeker sdk = new AndroidBloodseeker();
        setupSdk(sdk, SECRET_KEY);

        Async<Report> op = new AsyncReport();
        sdk.seekAsync(op);
        while (!op.isDone())
            Thread.sleep(10);
        Report report = op.getResult();

        String[] errors = report.getErrors();
        for (int i = 0; i < errors.length; ++i) {
            String error = errors[i];
            System.out.println("Error #" + (i + 1) + ": " + error);
        }
        Assert.assertTrue(report.isSuccess());
    }

    private void setupSdk(AndroidBloodseeker sdk, String secretKey) throws IOException {
        BloodseekerTest.setupSdk(sdk, secretKey);
        sdk.addTrail(new PackageNameTrail("java.util"));
        sdk.addTrail(new PathInApkTrail("META-INF/MANIFEST.MF"));
        sdk.addTrail(new AndroidManifestXmlTrail(new AndroidManifestXmlTrail.Looker(new String[]
                { "application", "provider" },
                "android:name",
                "com.facebook.internal.FacebookInitProvider",
                AndroidManifestXmlTrail.Looker.Condition.Eq)));
    }
}
