import com.am1goo.bloodseeker.android.AppContext;
import com.am1goo.bloodseeker.android.AsyncReport;
import com.am1goo.bloodseeker.android.Bloodseeker;
import com.am1goo.bloodseeker.android.IResult;
import com.am1goo.bloodseeker.android.ITrail;
import com.am1goo.bloodseeker.android.Report;
import com.am1goo.bloodseeker.android.trails.PackageNameTrail;
import com.am1goo.bloodseeker.android.trails.PathInApkTrail;
import com.am1goo.bloodseeker.android.trails.tests.DelayTrail;
import com.am1goo.bloodseeker.android.Async;
import com.am1goo.bloodseeker.android.trails.ClassNameTrail;
import com.am1goo.bloodseeker.android.trails.LibraryTrail;
import com.am1goo.bloodseeker.android.update.RemoteUpdateConfig;
import com.am1goo.bloodseeker.android.update.RemoteUpdateFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class BloodseekerTest {

    private static final String SECRET_KEY = "0123456789ABCDEF";

    @Test
    public void testSeekAsync() throws InterruptedException {
        Bloodseeker sdk = createSdk(SECRET_KEY);
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
        Bloodseeker sdk = createSdk(SECRET_KEY);

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
        ITrail trail = new ClassNameTrail("com.am1goo.bloodseeker.android.ITrail");
        boolean found = this.seek(trail);
        Assert.assertTrue(found);
    }

    @Test
    public void testLibraryTrail() {
        ITrail trail = new LibraryTrail("nothingButEmpty");
        boolean found = this.seek(trail);
        Assert.assertFalse(found);
    }

    private Bloodseeker createSdk(String secretKey) {
        Bloodseeker sdk = new Bloodseeker();
        RemoteUpdateConfig config = new RemoteUpdateConfig();
        config.setUrl("https://raw.githubusercontent.com/am1goo/bloodseeker-unity/main/package.json");
        config.setSecretKey(secretKey);
        sdk.setRemoteUpdateConfig(config);
        sdk.addTrail(new ClassNameTrail("java.util.List"));
        sdk.addTrail(new ClassNameTrail("java.util.ArrayList"));
        sdk.addTrail(new ClassNameTrail("com.some.class.Name"));
        sdk.addTrail(new PackageNameTrail("java.util"));
        sdk.addTrail(new PathInApkTrail("META-INF/MANIFEST.MF"));
        sdk.addTrail(new DelayTrail(1000));
        return sdk;
    }

    private boolean seek(ITrail trail) {
        AppContext context = new AppContext(null, null);

        List<IResult> result = new ArrayList<IResult>();
        List<Exception> exceptions = new ArrayList<Exception>();
        trail.seek(context, result, exceptions);
        return result.size() > 0;
    }
}
