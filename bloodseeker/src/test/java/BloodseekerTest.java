import com.am1goo.bloodseeker.android.AppContext;
import com.am1goo.bloodseeker.android.IResult;
import com.am1goo.bloodseeker.android.ITrail;
import com.am1goo.bloodseeker.android.Utilities;
import com.am1goo.bloodseeker.android.trails.ClassNameTrail;
import com.am1goo.bloodseeker.android.trails.LibraryTrail;
import com.am1goo.bloodseeker.android.trails.PackageNameTrail;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class BloodseekerTest {

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
