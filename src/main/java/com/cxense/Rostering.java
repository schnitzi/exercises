package com.cxense;

import java.util.List;
import java.util.Stack;

/**
 * Class to do small business rostering as per https://wiki.cxense.com/display/FA/Coding+Exercise.
 */
public class Rostering {

    public Schedule schedule(final Preferences preferences) {

        // Could choose to loop through the days, trying to assign employees to each, but best
        // to try to satisfy the harder requirement that every employee gets at least one scheduled
        // day first.  So get the list of employees, pickiest first.
        final Stack<Preferences.Employee> employeesHardestToAssignFirst = new Stack<>();
        employeesHardestToAssignFirst.addAll(preferences.getEmployeesHardestToAssignFirst());

        // Create the schedule to populate.
        final Schedule schedule = new Schedule();

        // Gather all the shifts we have to assign, with the ones with the fewest employee options first.
        Stack<Slot> shiftsToAssign = preferences.getShiftsHardestToAssignFirst();

        // Kick off the search for an optimal schedule.
        doRosteringSearch(schedule, preferences, employeesHardestToAssignFirst, shiftsToAssign, 0);

        return schedule;
    }

    private void doRosteringSearch(final Schedule schedule, final Preferences preferences,
                                   final Stack<Preferences.Employee> employeesNeedingAssignment,
                                   final Stack<Slot> shiftsToAssign, final int pain) {

        if (pain > schedule.getPain()) {
            // Already worse than our current best effort; no point continue search down
            // this line.
            return;
        }

        if (shiftsToAssign.isEmpty()) {
            // Success!
            schedule.setFilled(true);
            schedule.setPain(pain);
            return;
        }

        // See if we still need to assign any employees to at least one day.
        if (!employeesNeedingAssignment.isEmpty()) {

            final Preferences.Employee employeeNeedingAssignment = employeesNeedingAssignment.pop();

            final List<Slot> possibleSlots = schedule.getPossibleShifts(employeeNeedingAssignment);
            for (Slot slot : possibleSlots) {
                int newPain = schedule.assign(employeeNeedingAssignment, slot);
                doRosteringSearch(schedule, preferences, employeesNeedingAssignment, shiftsToAssign, pain + newPain);
                schedule.unassign(slot);
            }
        }

        // Now start assigning shifts by day.
        Slot slot = shiftsToAssign.pop();
        if (!schedule.isAssigned(slot)) {
            for (Preferences.Employee employee : preferences.getAvailableEmployees(slot)) {
                if (schedule.canAssign(employee, slot)) {
                    int newPain = schedule.assign(employee, slot);
                    doRosteringSearch(schedule, preferences, employeesNeedingAssignment, shiftsToAssign, pain + newPain);
                    schedule.unassign(slot);
                }
            }
        }
        shiftsToAssign.push(slot);
    }
}
