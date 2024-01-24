package com.am1goo.bloodseeker.android;

import com.am1goo.bloodseeker.Async;
import com.am1goo.bloodseeker.AsyncReport;
import com.am1goo.bloodseeker.Bloodseeker;
import com.am1goo.bloodseeker.BloodseekerTest;
import com.am1goo.bloodseeker.Report;
import com.am1goo.bloodseeker.android.trails.PackageNameTrail;
import com.am1goo.bloodseeker.android.trails.PathInApkTrail;
import com.am1goo.bloodseeker.trails.ClassNameTrail;
import com.am1goo.bloodseeker.trails.DelayTrail;
import com.am1goo.bloodseeker.update.RemoteUpdateConfig;
import com.am1goo.bloodseeker.update.RemoteUpdateFile;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class AndroidBloodseekerTest {

    private static final String SECRET_KEY = "0123456789ABCDEF";

    @Test
    public void testSeekAsync() throws InterruptedException {
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

    @Test
    public void testBake() throws Exception {
        AndroidBloodseeker sdk = new AndroidBloodseeker();
        setupSdk(sdk, SECRET_KEY);

        byte[] bytes = sdk.bake();
        Assert.assertNotNull(bytes);

        byte[] secretKey = SECRET_KEY.getBytes("utf-8");
        RemoteUpdateFile file = new RemoteUpdateFile(secretKey);
        try (InputStream inputStream = new ByteArrayInputStream(bytes)) {
            file.load(inputStream);
        }
    }

    private void setupSdk(AndroidBloodseeker sdk, String secretKey) {
        BloodseekerTest.setupSdk(sdk, secretKey);
        sdk.addTrail(new PackageNameTrail("java.util"));
        sdk.addTrail(new PathInApkTrail("META-INF/MANIFEST.MF"));
    }
}
