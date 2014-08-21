package com.cxense;

import java.io.File;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Unit test for {@link Preferences}.
 */
public class PreferencesUnitTest {


    @Test
    public void testExampleData() throws Exception {

        final Preferences preferences = new Preferences(new File("src/test/resources/rostering.sample.in"));
        final Rostering rostering = new Rostering(preferences);
        Schedule schedule = rostering.findBestSchedule();
        Assert.assertNotNull(schedule);
        Assert.assertEquals(schedule.getPain(), 0);
        System.out.println(schedule);
    }
}
