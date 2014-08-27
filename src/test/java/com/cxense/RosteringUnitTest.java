package com.cxense;

import java.io.File;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Unit test for {@link Rostering}.
 */
public class RosteringUnitTest {


    @Test
    public void testExamples() throws Exception {

        doTest("src/test/resources/rostering.easy.in", 0);
        doTest("src/test/resources/rostering.impossible1.in", -1);
        doTest("src/test/resources/rostering.impossible2.in", -1);
        doTest("src/test/resources/rostering.difficult.in", 2*Schedule.PAIN_FOR_WORKING_NOT_PREFERRED_SHIFT);
        doTest("src/test/resources/rostering.sample.in", 2*Schedule.PAIN_FOR_TWO_INEXPERIENCED_ON_SAME_SHIFT);
    }

    private void doTest(final String filename, final int expectedPain) throws Exception {

        System.out.println("Testing " + filename);
        final Preferences preferences = new Preferences(new File(filename));
        final Rostering rostering = new Rostering(preferences);
        Schedule schedule = rostering.findBestSchedule();
        if (expectedPain == -1) {
            // Not expected to be solvable.
            Assert.assertNull(schedule, "Expected no solution for " + filename + "; got " + schedule);
            System.out.println("Unsolveable.");
        } else {
            Assert.assertNotNull(schedule, "Expected a solution for " + filename);
            Assert.assertEquals(schedule.getPain(), expectedPain, "Unexpected pain value");
            System.out.println(schedule);
        }
    }
}
