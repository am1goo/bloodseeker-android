package com.am1goo.bloodseeker.trails;

import com.am1goo.bloodseeker.BloodseekerExceptions;
import com.am1goo.bloodseeker.IResult;
import com.am1goo.bloodseeker.ITrail;

import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseTrailTest {

    protected void seek(ITrail trail) {
        List<IResult> results = new ArrayList<>();
        BloodseekerExceptions exceptions = new BloodseekerExceptions();
        trail.seek(results, exceptions);

        Assert.assertEquals(exceptions.getExceptions().size(), 0);

        for (int i = 0; i < results.size(); ++i ) {
            IResult result = results.get(i);
            System.out.println("#" + (i + 1) + ": " + result.toString());
        }
    }
}
