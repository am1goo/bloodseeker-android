package com.am1goo.bloodseeker.trails;

import com.am1goo.bloodseeker.Condition;
import com.am1goo.bloodseeker.ITrail;

import org.junit.Test;

public class JavaSystemPropertyTrailTest extends BaseTrailTest {

    @Test
    public void testSystemPropertyTrail() {
        ITrail trail = new JavaSystemPropertyTrail(new JavaSystemPropertyTrail.SystemProperty[] {
                new JavaSystemPropertyTrail.SystemProperty("os.name"),
                new JavaSystemPropertyTrail.SystemProperty("os.version", Condition.Eq,"10.*"),
                new JavaSystemPropertyTrail.SystemProperty("os.version",Condition.NonEq, "11.0"),
                new JavaSystemPropertyTrail.SystemProperty("os.version",Condition.NonEq, "10.0"),
                new JavaSystemPropertyTrail.SystemProperty("java.version",Condition.Eq, "17.*"),
        });
        seek(trail);
    }
}
