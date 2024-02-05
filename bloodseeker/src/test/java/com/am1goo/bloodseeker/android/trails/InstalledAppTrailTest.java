package com.am1goo.bloodseeker.android.trails;

import com.am1goo.bloodseeker.ITrail;
import com.am1goo.bloodseeker.trails.BaseTrailTest;

import org.junit.Test;

public class InstalledAppTrailTest  extends BaseTrailTest {

    @Test
    public void testInstalledAppTrail() {
        ITrail trail = new InstalledAppTrail(new String[] {
                "com.any.strange.package.name"
        });
        seek(trail);
    }
}
