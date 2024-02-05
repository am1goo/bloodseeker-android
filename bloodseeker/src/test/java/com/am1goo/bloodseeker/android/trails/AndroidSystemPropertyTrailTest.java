package com.am1goo.bloodseeker.android.trails;

import com.am1goo.bloodseeker.ITrail;
import com.am1goo.bloodseeker.trails.BaseTrailTest;

import org.junit.Test;

public class AndroidSystemPropertyTrailTest extends BaseTrailTest {
    @Test
    public void testAndroidSystemPropertyTrail() {
        ITrail trail = new AndroidSystemPropertyTrail(new AndroidSystemPropertyTrail.SystemProperty[] {
                new AndroidSystemPropertyTrail.SystemProperty("ril.serialnumber")
        });
        seek(trail);
    }
}
