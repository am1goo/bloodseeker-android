package com.am1goo.bloodseeker.trails;

import com.am1goo.bloodseeker.Condition;
import com.am1goo.bloodseeker.ITrail;

import org.junit.Test;

public class SystemPropertyTrailTest extends BaseTrailTest {

    @Test
    public void testSystemPropertyTrail() {
        ITrail trail = new SystemPropertyTrail(new SystemPropertyTrail.SystemProperty[] {
                new SystemPropertyTrail.SystemProperty("os.name"),
                new SystemPropertyTrail.SystemProperty("os.version", Condition.Eq,"10.*"),
                new SystemPropertyTrail.SystemProperty("os.version",Condition.NonEq, "11.0"),
                new SystemPropertyTrail.SystemProperty("os.version",Condition.NonEq, "10.0"),
                new SystemPropertyTrail.SystemProperty("java.version",Condition.Eq, "17.*"),
        });
        seek(trail);
    }
}
