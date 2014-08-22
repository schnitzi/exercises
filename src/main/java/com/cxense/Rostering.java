package com.cxense;

import java.util.Stack;

/**
 * Class to do small business rostering as per https://wiki.cxense.com/display/FA/Coding+Exercise.
 */
public class Rostering {

    private Schedule schedule;
    private Schedule bestSchedule;
    private int bestPain = Integer.MAX_VALUE;
    private final Preferences preferences;
    private final Stack<Slot> slotsToAssign;

    public Rostering(Preferences preferences) {
        this.preferences = preferences;

        // Create the schedule to populate.
        schedule = new Schedule(preferences.getEmployees());

        // Gather all the shifts we have to assign, with the ones with the fewest employee options first.
        slotsToAssign = preferences.getSlotsHardestToAssignFirst();
    }

    public Schedule findBestSchedule() {

        // Kick off the search for an optimal schedule.
        bestFirstSearch(0);

        // And return it.
        return bestSchedule;
    }

    private void bestFirstSearch(final int pain) {

//        // TODO clean
//        System.out.println("slots left = " + slotsToAssign.size());
//        System.out.println("pain = " + pain);
//        System.out.println(schedule);

        if (pain >= bestPain) {
            // Already can't beat our current best effort; no point continuing search down
            // this path.
            return;
        }

        if (slotsToAssign.isEmpty()) {
            // Success!
            bestSchedule = schedule.copy();
            bestSchedule.setPain(pain);
            bestPain = pain;
            System.out.println("New best found:");
            System.out.println(bestSchedule);
            return;
        }

        Slot slot = slotsToAssign.pop();
        for (Employee employee : preferences.getAvailableEmployees(slot.getDay())) {
            tryToAssign(employee, slot, pain);
        }
        slotsToAssign.push(slot);
    }

    private void tryToAssign(final Employee employee, final Slot slot, final int pain) {
        if (schedule.canAssign(employee, slot)) {
            final int newPain = schedule.assign(employee, slot);
            bestFirstSearch(pain + newPain);
            schedule.unassign(slot);
        }
    }
}
