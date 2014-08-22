package com.cxense;

import java.io.File;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Unit test for {@link Preferences}.
 */
public class PreferencesUnitTest {


    @Test
    public void testExamples() throws Exception {

        doTest("src/test/resources/rostering.easy.in", 0);
        doTest("src/test/resources/rostering.impossible1.in", -1);
        doTest("src/test/resources/rostering.impossible2.in", -1);
//        doTest("src/test/resources/rostering.sample.in", 3);
    }

    private void doTest(final String filename, final int expectedPain) throws Exception {

        System.out.println("Testing " + filename);
        final Preferences preferences = new Preferences(new File(filename));
        final Rostering rostering = new Rostering(preferences);
        Schedule schedule = rostering.findBestSchedule();
        if (expectedPain == -1) {
            // Not expected to be solvable.
            Assert.assertNull(schedule, "Expected no solution for " + filename);
        } else {
            Assert.assertNotNull(schedule, "Expected a solution for " + filename);
            Assert.assertEquals(schedule.getPain(), expectedPain, "Unexpected pain value");
            System.out.println(schedule);
        }
    }

    // TODO make sure employee pickiness compares correctly
}
