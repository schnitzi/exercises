package com.cxense;

import java.util.ArrayList;
import java.util.List;

/**
 * Schedule created by running the {@link Rostering} class against a set of {@link Preferences}.
 */
public class Schedule {

    // The assignments of employee to shift, for each day of the week, for both shifts.
    final Preferences.Employee[][] assignments = new Preferences.Employee[7][2];
    private static final int PAIN_FOR_TWO_INEXPERIENCED_ON_SAME_DAY = 1;
    private static final int PAIN_FOR_WORKING_NOT_PREFERRED_SHIFT = 1;
    private int pain = Integer.MAX_VALUE;
    private boolean filled;

    public void setFilled(boolean filled) {
        this.filled = filled;
    }

    public boolean isFilled() {
        return filled;
    }

    public void setPain(int pain) {
        this.pain = pain;
    }

    public int getPain() {
        return pain;
    }

    /**
     * Gets the list of possible shifts available for an employee, given what's already been
     * scheduled, and the employee's preferences.
     */
    public List<Slot> getPossibleShifts(final Preferences.Employee employee) {

        List<Slot> possibleSlots = new ArrayList<>();
        for (int day=0; day<7; day++) {
            // Check the early shift.
            if (assignments[day][Slot.EARLY] == null &&
                    (assignments[day][Slot.LATE] == null || !assignments[day][Slot.LATE].equals(employee)) &&
                    employee.getPreference(day) != Preferences.PreferenceForDay.NONE) {
                possibleSlots.add(new Slot(day, Slot.EARLY));
            }
            // Check the late shift.
            if (assignments[day][Slot.LATE] == null &&
                    (assignments[day][Slot.EARLY] == null || !assignments[day][Slot.EARLY].equals(employee)) &&
                    employee.getPreference(day) != Preferences.PreferenceForDay.NONE) {
                possibleSlots.add(new Slot(day, Slot.LATE));
            }
        }
        return possibleSlots;
    }

    public int assign(final Preferences.Employee employee, final Slot slot) {
        assignments[slot.getDay()][slot.getShift()] = employee;
        int pain = 0;
        Preferences.Employee employeeOnOtherShift = assignments[slot.getDay()][slot.getOtherShift()];
        if (!employee.experienced && employeeOnOtherShift != null && !employeeOnOtherShift.experienced) {
            pain += PAIN_FOR_TWO_INEXPERIENCED_ON_SAME_DAY;
        }
        Preferences.PreferenceForDay preferenceForDay = employee.getPreference(slot.getDay());
        if ((slot.getShift() == Slot.EARLY && preferenceForDay == Preferences.PreferenceForDay.LATE) ||
                (slot.getShift() == Slot.LATE && preferenceForDay == Preferences.PreferenceForDay.EARLY)) {
            pain += PAIN_FOR_WORKING_NOT_PREFERRED_SHIFT;
        }
        return pain;
    }

    public void unassign(final Slot slot) {
        assignments[slot.getDay()][slot.getShift()] = null;
    }

    public boolean isAssigned(final Slot slot) {
        return assignments[slot.getDay()][slot.getShift()] == null;
    }

    public boolean canAssign(final Preferences.Employee employee, final Slot slot) {
        return assignments[slot.getDay()][slot.getShift()] == null &&
                !assignments[slot.getDay()][slot.getOtherShift()].equals(employee);
    }
}
