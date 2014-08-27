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

        // See if we already can't beat our current best effort; if so, no point continuing
        // search down this path.
        if (pain >= bestPain) {
            return;
        }

        // Have we filled the schedule?
        if (slotsToAssign.isEmpty()) {
            // Success!
            bestSchedule = schedule.copy();
            bestSchedule.setPain(pain);
            bestPain = pain;
            // System.out.println("New best found: " + bestSchedule);
            return;
        }

        // Otherwise, try to fill the current hardest slot, and recurse.
        Slot slot = slotsToAssign.pop();
        for (Employee employee : preferences.getAvailableEmployees(slot.getDay())) {
            if (schedule.canAssign(employee, slot)) {

                // do the assignment
                final int newPain = schedule.assign(employee, slot);

                // keep searching
                bestFirstSearch(pain + newPain);

                // undo the assignment
                schedule.unassign(slot);
            }
        }
        slotsToAssign.push(slot);
    }
}
