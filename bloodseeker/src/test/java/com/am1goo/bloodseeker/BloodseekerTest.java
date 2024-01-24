package com.am1goo.bloodseeker;

import com.am1goo.bloodseeker.trails.DelayTrail;
import com.am1goo.bloodseeker.trails.ClassNameTrail;
import com.am1goo.bloodseeker.trails.LibraryTrail;
import com.am1goo.bloodseeker.update.RemoteUpdateConfig;
import com.am1goo.bloodseeker.update.RemoteUpdateFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class BloodseekerTest {

    private static final String SECRET_KEY = "0123456789ABCDEF";

    @Test
    public void testSeekAsync() throws InterruptedException {
        Bloodseeker sdk = new Bloodseeker();
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
        Bloodseeker sdk = new Bloodseeker();
        setupSdk(sdk, SECRET_KEY);

        byte[] bytes = sdk.bake();
        Assert.assertNotNull(bytes);

        byte[] secretKey = SECRET_KEY.getBytes("utf-8");
        RemoteUpdateFile file = new RemoteUpdateFile(secretKey);
        try (InputStream inputStream = new ByteArrayInputStream(bytes)) {
            file.load(inputStream);
        }
    }

    @Test
    public void testClassNameTrail() {
        ITrail trail = new ClassNameTrail("com.am1goo.bloodseeker.ITrail");
        boolean found = seek(trail);
        Assert.assertTrue(found);
    }

    @Test
    public void testLibraryTrail() {
        ITrail trail = new LibraryTrail("nothingButEmpty");
        boolean found = seek(trail);
        Assert.assertFalse(found);
    }

    public static void setupSdk(Bloodseeker sdk, String secretKey) {
        RemoteUpdateConfig config = new RemoteUpdateConfig();
        config.setUrl("https://raw.githubusercontent.com/am1goo/bloodseeker-unity/main/package.json");
        config.setSecretKey(secretKey);
        config.setCacheTTL(60);
        sdk.setRemoteUpdateConfig(config);
        sdk.addTrail(new ClassNameTrail("java.util.List"));
        sdk.addTrail(new ClassNameTrail("java.util.ArrayList"));
        sdk.addTrail(new ClassNameTrail("com.some.class.Name"));
        sdk.addTrail(new DelayTrail(1000));
    }

    private static boolean seek(ITrail trail) {
        List<IResult> result = new ArrayList<IResult>();
        List<Exception> exceptions = new ArrayList<Exception>();
        trail.seek(result, exceptions);
        return result.size() > 0;
    }
}
