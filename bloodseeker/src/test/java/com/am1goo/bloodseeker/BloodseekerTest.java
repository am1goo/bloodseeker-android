package com.am1goo.bloodseeker;

import com.am1goo.bloodseeker.trails.DelayTrail;
import com.am1goo.bloodseeker.trails.ClassNameTrail;
import com.am1goo.bloodseeker.update.LocalUpdateConfig;
import com.am1goo.bloodseeker.update.RemoteUpdateConfig;
import com.am1goo.bloodseeker.utilities.StringUtilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class BloodseekerTest {

    private static final String SECRET_KEY = StringUtilities.getRandomString(44);

    @Test
    public void testSeekAsync() throws InterruptedException, IOException {
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
    public void testClassNameTrail() {
        ITrail trail = new ClassNameTrail("com.am1goo.bloodseeker.ITrail");
        boolean found = seek(trail);
        Assert.assertTrue(found);
    }

    public static void setupSdk(Bloodseeker sdk, String secretKey) throws IOException {
        LocalUpdateConfig localConfig = new LocalUpdateConfig();
        localConfig.setFile("examples/remote-update-project-hierarchy/generated.bmx");
        localConfig.setSecretKey("0123456789ABCDEF");

        RemoteUpdateConfig remoteConfig = new RemoteUpdateConfig();
        remoteConfig.setUrl("https://raw.githubusercontent.com/am1goo/bloodseeker-unity/main/package.json");
        remoteConfig.setSecretKey(secretKey);
        remoteConfig.setCacheTTL(60);

        sdk.setLocalUpdateConfig(localConfig);
        sdk.setRemoteUpdateConfig(remoteConfig);
        sdk.addTrail(new ClassNameTrail("java.util.List"));
        sdk.addTrail(new ClassNameTrail("java.util.ArrayList"));
        sdk.addTrail(new ClassNameTrail("com.some.class.Name"));
        sdk.addTrail(new DelayTrail(1000));
    }

    private static boolean seek(ITrail trail) {
        List<IResult> result = new ArrayList<IResult>();
        BloodseekerExceptions exceptions = new BloodseekerExceptions();
        trail.seek(result, exceptions);
        return result.size() > 0;
    }
}
