import com.am1goo.bloodseeker.android.StringUtilities;

import org.junit.Assert;
import org.junit.Test;

public class StringUtilitiesTest {

    @Test
    public void testTrimAtStart() {
        testTrimAtStart("//path/to/folder", "path/to/folder");
        testTrimAtStart("/path/to/folder", "path/to/folder");
        testTrimAtStart("////", "");
        testTrimAtStart("","");
        testTrimAtStart(null, null);
    }

    @Test
    public void testTrimAtEnd() {
        testTrimAtEnd("path/to/folder/", "path/to/folder");
        testTrimAtEnd("path/to/folder/", "path/to/folder");
        testTrimAtEnd("////", "");
        testTrimAtEnd("","");
        testTrimAtEnd(null, null);
    }

    @Test
    public void testTrim() {
        testTrim("//path/to/folder/", "path/to/folder");
        testTrim("/path/to/folder/", "path/to/folder");
        testTrim("////", "");
        testTrim("","");
        testTrim(null, null);
    }

    private void testTrimAtStart(String actual, String expected) {
        String result = StringUtilities.trimAtStart(actual, '/');
        Assert.assertEquals(result, expected);
    }
    
    private void testTrimAtEnd(String actual, String expected) {
        String result = StringUtilities.trimAtEnd(actual, '/');
        Assert.assertEquals(result, expected);
    }

    private void testTrim(String actual, String expected) {
        String result = StringUtilities.trim(actual, '/');
        Assert.assertEquals(result, expected);
    }
}
