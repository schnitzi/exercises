package com.cxense;

import java.util.List;
import java.util.Stack;

/**
 * Class to do small business rostering as per https://wiki.cxense.com/display/FA/Coding+Exercise.
 */
public class Rostering {

    private Schedule schedule;
    private Schedule bestSchedule;
    private int bestPain = Integer.MAX_VALUE;
    private final Preferences preferences;

    public Rostering(Preferences preferences) {
        this.preferences = preferences;
    }

    public Schedule findBestSchedule() {

        // We could choose to loop through the days, trying to assign employees to each, but better
        // to try to satisfy the harder requirement that every employee gets at least one scheduled
        // day first.  So get the list of employees, pickiest first.
        final Stack<Employee> employeesHardestToAssignFirst = new Stack<>();
        employeesHardestToAssignFirst.addAll(preferences.getEmployeesHardestToAssignFirst());

        // Create the schedule to populate.
        schedule = new Schedule();

        // Gather all the shifts we have to assign, with the ones with the fewest employee options first.
        Stack<Slot> slotsToAssign = preferences.getSlotsHardestToAssignFirst();

        // Kick off the search for an optimal schedule.
        bestFirstSearch(employeesHardestToAssignFirst, slotsToAssign, 0);

        return bestSchedule;
    }

    // TODO all params necessary?

    private void bestFirstSearch(final Stack<Employee> employeesWithoutAssignments,
                                 final Stack<Slot> slotsToAssign, final int pain) {

        System.out.println("slots left = " + slotsToAssign.size());
        System.out.println("pain = " + pain);
//        System.out.println(schedule);

        if (pain > bestPain) {
            // Already worse than our current best effort; no point continue search down
            // this line.
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
                if (schedule.canAssign(employee, slot)) {
                    int newPain = schedule.assign(employee, slot);
                    bestFirstSearch(employeesWithoutAssignments, slotsToAssign, pain + newPain);
                    schedule.unassign(slot, employee);
                }
            }
            employeesWithoutAssignments.push(employee);
        } else {
            // Now start assigning shifts by day.
            Slot slot = slotsToAssign.pop();
            if (!schedule.isAssigned(slot)) {
                for (Employee employee : preferences.getAvailableEmployees(slot.getDay())) {
                    if (schedule.canAssign(employee, slot)) {
                        int newPain = schedule.assign(employee, slot);
                        bestFirstSearch(employeesWithoutAssignments, slotsToAssign, pain + newPain);
                        schedule.unassign(slot, employee);
                    }
                }
            }
            slotsToAssign.push(slot);
        }
    }
}
