package com.cxense;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;

/**
 * Unit test for {@link Preferences}.
 */
public class PreferencesUnitTest {


    @Test
    public void testExampleData() throws Exception {

        final Preferences preferences = new Preferences(new File("src/test/resources/rostering.sample.in"));
        final Rostering rostering = new Rostering(preferences);
        Schedule schedule = rostering.findBestSchedule();
        Assert.assertTrue(schedule.isFilled());
        Assert.assertEquals(schedule.getPain(), 0);
        System.out.println(schedule);
    }
}
