import com.am1goo.bloodseeker.android.AppContext;
import com.am1goo.bloodseeker.android.Bloodseeker;
import com.am1goo.bloodseeker.android.IResult;
import com.am1goo.bloodseeker.android.ITrail;
import com.am1goo.bloodseeker.android.Report;
import com.am1goo.bloodseeker.android.trails.tests.DelayTrail;
import com.am1goo.bloodseeker.android.Async;
import com.am1goo.bloodseeker.android.trails.ClassNameTrail;
import com.am1goo.bloodseeker.android.trails.LibraryTrail;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class BloodseekerTest {

    @Test
    public void testSeek() {
        Bloodseeker sdk = new Bloodseeker();
        sdk.addTrail(new ClassNameTrail("java.util.List"));
        sdk.addTrail(new ClassNameTrail("java.util.ArrayList"));
        Report report = sdk.seek();
        Assert.assertTrue(report.isSuccess());
    }

    @Test
    public void testSeekAsync() throws InterruptedException {
        Bloodseeker sdk = new Bloodseeker();
        sdk.addTrail(new ClassNameTrail("java.util.List"));
        sdk.addTrail(new ClassNameTrail("java.util.ArrayList"));
        sdk.addTrail(new DelayTrail(1000));
        Async<Report> op = new Async<>();
        sdk.seekAsync(op);
        while (!op.isDone())
            Thread.sleep(10);
        Report report = op.getResult();
        Assert.assertTrue(report.isSuccess());
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

    private boolean seek(ITrail trail) {
        AppContext context = new AppContext(null, null);

        List<IResult> result = new ArrayList();
        List<Exception> exceptions = new ArrayList();
        trail.seek(context, result, exceptions);
        return result.size() > 0;
    }
}
