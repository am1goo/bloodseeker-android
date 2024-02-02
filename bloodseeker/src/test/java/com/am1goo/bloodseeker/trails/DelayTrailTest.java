package com.am1goo.bloodseeker.trails;

import com.am1goo.bloodseeker.ITrail;

import org.junit.Test;

public class DelayTrailTest extends BaseTrailTest {

    @Test
    public void testDelayTrail() {
        ITrail trail = new DelayTrail(1);
        seek(trail);
    }
}
