package com.cxense;

import junit.framework.Assert;
import org.junit.Test;

import java.io.File;

/**
 * Unit test for {@link Preferences}.
 */
public class PreferencesUnitTest {

    private static final Rostering ROSTERING = new Rostering();

    @Test
    public void testExampleData() throws Exception {

        final Preferences preferences = new Preferences(new File("src/test/resources/rostering.sample.in"));
        Schedule schedule = ROSTERING.schedule(preferences);
        Assert.assertTrue(schedule.isFilled());
        Assert.assertEquals(schedule.getPain(), 0);
        System.out.println(schedule);
    }
}
