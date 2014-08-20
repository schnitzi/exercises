package com.cxense;

import java.util.List;
import java.util.SortedSet;
import java.util.Stack;

/**
 * Class to do small business rostering as per https://wiki.cxense.com/display/FA/Coding+Exercise.
 */
public class Rostering {

    private Schedule schedule;
    private Schedule bestSchedule;
    private int bestPain = Integer.MAX_VALUE;
    private final Preferences preferences;
    private final Stack<Employee> employeesWithoutAssignments;
    private final SortedSet<Slot> slotsToAssign;

    public Rostering(Preferences preferences) {
        this.preferences = preferences;

        // We could choose to loop through the days, trying to assign employees to each, but better
        // to try to satisfy the harder requirement that every employee gets at least one scheduled
        // day first.  So get the list of employees, pickiest first.
        employeesWithoutAssignments = new Stack<>();
        employeesWithoutAssignments.addAll(preferences.getEmployeesHardestToAssignFirst());

        // Create the schedule to populate.
        schedule = new Schedule();

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

        // TODO clean
        System.out.println("slots left = " + slotsToAssign.size());
        System.out.println("pain = " + pain);
//        System.out.println(schedule);

        if (pain >= bestPain) {
            // Already can't beat our current best effort; no point continuing search down
            // this path.
            return;
        }

        if (slotsToAssign.isEmpty()) {
            // Success!
            bestSchedule = schedule.copy();
            bestSchedule.setFilled(true);
            bestSchedule.setPain(pain);
            bestPain = pain;
            return;
        }

        // See if we still need to assign any employees to at least one day.
        if (!employeesWithoutAssignments.isEmpty()) {

            final Employee employee = employeesWithoutAssignments.pop();
            final List<Slot> possibleSlots = schedule.getPossibleShifts(employee);
            for (Slot slot : possibleSlots) {
                slotsToAssign.remove(slot);
                tryToAssign(employee, slot, pain);
                slotsToAssign.add(slot);
            }
            employeesWithoutAssignments.push(employee);
        } else {

            // All employees have at least one shift, so start assigning shifts by day.
            Slot slot = slotsToAssign.first();
            slotsToAssign.remove(slot);
            for (Employee employee : preferences.getAvailableEmployees(slot.getDay())) {
                tryToAssign(employee, slot, pain);
            }
            slotsToAssign.add(slot);
        }
    }

    private void tryToAssign(final Employee employee, final Slot slot, final int pain) {
        if (schedule.canAssign(employee, slot)) {
            final int newPain = schedule.assign(employee, slot);
            bestFirstSearch(pain + newPain);
            schedule.unassign(slot);
        }
    }
}
