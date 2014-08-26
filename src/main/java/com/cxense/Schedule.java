package com.cxense;

import java.util.*;

/**
 * Schedule created by running the {@link Rostering} class against a set of {@link Preferences}.
 */
public class Schedule {

    /**
     * The pain value associated with having two inexperienced employees on the same shift.
     */
    static final int PAIN_FOR_TWO_INEXPERIENCED_ON_SAME_SHIFT = 1;
    /**
     * The pain value for an employee having to work his/her not-preferred shift.
     */
    static final int PAIN_FOR_WORKING_NOT_PREFERRED_SHIFT = 10;
    /**
     * Names of the days of the week.
     */
    private static final String DAY_NAMES[] = new String[] {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
    /**
     * The maximum assignments per week.
     */
    private static final int MAX_ASSIGNMENTS_PER_WEEK = 4;
    /**
     * The assignments of employee to slots, for all slots of the week.
     */
    final Map<Slot, Employee> assignments = new HashMap<>();
    /**
     * The full list of employees.
     */
    final List<Employee> employees;
    /**
     * The total "pain" of this schedule.  Low pain means we did a better job scheduling.
     */
    private int pain = Integer.MAX_VALUE;
    /**
     * The number of days each employee has been assigned.
     */
    private Map<Employee, Integer> employeeAssignmentCounts = new HashMap<>();


    public Schedule(final List<Employee> employees) {
        this.employees = employees;
    }

    public void setPain(int pain) {
        this.pain = pain;
    }

    public int getPain() {
        return pain;
    }

    /**
     * Gets the list of possible slots available for an employee, given what's already been
     * scheduled, and the employee's preferences.
     */
    public List<Slot> getPossibleSlots(final Employee employee) {

        List<Slot> possibleSlots = new ArrayList<>();
        for (int day=0; day<7; day++) {

            if (employee.getPreference(day) != Preferences.PreferenceForDay.NONE) {
                for (Slot.Shift shift : Slot.Shift.values()) {
                    for (int index = 0; index < 2; index++) {
                        final Slot slot = new Slot(day, shift, index);
                        if (assignments.get(slot) == null && !employee.equals(assignments.get(slot.getOtherSlotOnSameShift()))) {
                            possibleSlots.add(slot);
                        }
                    }
                }
            }
        }
        return possibleSlots;
    }

    private int getAssignmentCount(final Employee employee) {
        Integer count = employeeAssignmentCounts.get(employee);
        return count == null ? 0 : count;
    }

    /**
     * Performs the assignment to the schedule.  Returns the pain value that this assignment caused.
     */
    public int assign(final Employee employeeToAssign, final Slot slotToAssignTo) {

        assignments.put(slotToAssignTo, employeeToAssign);
        Integer currentAssignmentCount = employeeAssignmentCounts.get(employeeToAssign);
        employeeAssignmentCounts.put(employeeToAssign, currentAssignmentCount == null ? 1 : currentAssignmentCount+1);

        // Compute the pain this assignment caused.

        // If there are employees who haven't been assigned, and who now have no slots left to be
        // assigned to, that's bad.
        for (Employee employee : employees) {
            if (getAssignmentCount(employee) == 0) {
                boolean stillHasAvailableSlot = false;
                for (Slot slot : getPossibleSlots(employee)) {
                    if (assignments.get(slot) == null) {
                        stillHasAvailableSlot = true;
                        break;
                    }
                }
                if (!stillHasAvailableSlot) {
                    return Integer.MAX_VALUE;
                }
            }
        }

        // Otherwise, the pain of having two inexperienced employees on the same shift.
        int pain = 0;
        final Employee otherEmployeeSameSlot = assignments.get(slotToAssignTo.getOtherSlotOnSameShift());
        if (!employeeToAssign.experienced && otherEmployeeSameSlot != null && !otherEmployeeSameSlot.experienced) {
            pain += PAIN_FOR_TWO_INEXPERIENCED_ON_SAME_SHIFT;
        }

        // Then, the pain of assigning an employee to a shift that's not his/her preferred shift.
        Preferences.PreferenceForDay preferenceForDay = employeeToAssign.getPreference(slotToAssignTo.getDay());
        if ((slotToAssignTo.getShift() == Slot.Shift.EARLY && preferenceForDay == Preferences.PreferenceForDay.LATE) ||
                (slotToAssignTo.getShift() == Slot.Shift.LATE && preferenceForDay == Preferences.PreferenceForDay.EARLY)) {
            pain += PAIN_FOR_WORKING_NOT_PREFERRED_SHIFT;
        }

        return pain;
    }

    /**
     * Undoes an earlier assignment that was done to a slot.
     */
    public void unassign(final Slot slot) {
        final Employee employee = assignments.get(slot);
        if (employee == null) {
            throw new RuntimeException("Expected employee to be assigned to slot");
        }
        assignments.remove(slot);
        Integer currentAssignmentCount = employeeAssignmentCounts.get(employee);
        if (currentAssignmentCount == null) {
            throw new RuntimeException("Expected employee to have assignment count");
        }
        employeeAssignmentCounts.put(employee, currentAssignmentCount-1);
    }

    /**
     * Returns whether the employee can be assigned to the given slot.
     */
    public boolean canAssign(final Employee employee, final Slot slot) {
        return getAssignmentCount(employee) < MAX_ASSIGNMENTS_PER_WEEK &&
                !assignments.containsKey(slot) &&
                employee.getPreference(slot.getDay()) != Preferences.PreferenceForDay.NONE &&
                !isAssignedForDay(employee, slot.getDay());
    }

    /**
     * Returns whether the employee is assigned to the given day.
     */
    public boolean isAssignedForDay(final Employee employee, final int day) {
        return employee.equals(assignments.get(new Slot(day, Slot.Shift.EARLY, 0))) ||
                employee.equals(assignments.get(new Slot(day, Slot.Shift.EARLY, 1))) ||
                employee.equals(assignments.get(new Slot(day, Slot.Shift.LATE, 0))) ||
                employee.equals(assignments.get(new Slot(day, Slot.Shift.LATE, 1)));
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder("Schedule{pain=" )
                .append(pain).append(", assignments=\n");
        for (int day=0; day<7; day++) {
            builder.append(DAY_NAMES[day]).append("\n");
            for (Slot.Shift shift : Slot.Shift.values()) {
                builder.append(shift).append("=");
                for (int index=0; index<2; index++) {
                    Employee employee = assignments.get(new Slot(day, shift, index));
                    builder.append(employee).append(" ");
                }
                builder.append("\n");
            }
        }
        builder.append('}');
        return builder.toString();
    }

    public Schedule copy() {
        final Schedule copy = new Schedule(employees);
        copy.pain = pain;
        copy.assignments.putAll(assignments);
        return copy;
    }
}
