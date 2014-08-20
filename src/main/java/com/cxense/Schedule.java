package com.cxense;

import java.util.*;

/**
 * Schedule created by running the {@link Rostering} class against a set of {@link Preferences}.
 */
public class Schedule {

    private static final int PAIN_FOR_TWO_INEXPERIENCED_ON_SAME_SHIFT = 1;
    private static final int PAIN_FOR_WORKING_NOT_PREFERRED_SHIFT = 1;
    private static final String DAY_NAMES[] = new String[] {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

    // The assignments of employee to slots, for all slots of the week.
    final Map<Slot, Employee> assignments = new HashMap<>();

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
    public List<Slot> getPossibleShifts(final Employee employee) {

        List<Slot> possibleSlots = new ArrayList<>();
        for (int day=0; day<7; day++) {

            for (Slot.Shift shift : Slot.Shift.values()) {
                for (int index=0; index<2; index++) {
                    final Slot slot = new Slot(day, shift, index);
                    if (assignments.get(slot) == null && !employee.equals(assignments.get(slot.getOtherSlotOnSameShift()))) {
                        possibleSlots.add(slot);
                    }
                }
            }
        }
        return possibleSlots;
    }

    public int assign(final Employee employee, final Slot slot) {

        assignments.put(slot, employee);

        // Compute the pain this assignment caused.
        int pain = 0;

        // First, the pain of having two inexperienced employees on the same shift.
        final Employee otherEmployeeSameSlot = assignments.get(slot.getOtherSlotOnSameShift());
        if (!employee.experienced && otherEmployeeSameSlot != null && !otherEmployeeSameSlot.experienced) {
            pain += PAIN_FOR_TWO_INEXPERIENCED_ON_SAME_SHIFT;
        }

        // Then, the pain of assigning an employee to a shift that's not his/her preferred shift.
        Preferences.PreferenceForDay preferenceForDay = employee.getPreference(slot.getDay());
        if ((slot.getShift() == Slot.Shift.EARLY && preferenceForDay == Preferences.PreferenceForDay.LATE) ||
                (slot.getShift() == Slot.Shift.LATE && preferenceForDay == Preferences.PreferenceForDay.EARLY)) {
            pain += PAIN_FOR_WORKING_NOT_PREFERRED_SHIFT;
        }

        return pain;
    }

    public void unassign(final Slot slot) {
        assignments.remove(slot);
    }

    public boolean canAssign(final Employee employee, final Slot slot) {
        return !assignments.containsKey(slot) &&
                employee.getPreference(slot.getDay()) != Preferences.PreferenceForDay.NONE &&
                !isAssignedForDay(employee, slot.getDay());
    }

    public boolean isAssignedForDay(final Employee employee, final int day) {
        return employee.equals(assignments.get(new Slot(day, Slot.Shift.EARLY, 0))) ||
                employee.equals(assignments.get(new Slot(day, Slot.Shift.EARLY, 1))) ||
                employee.equals(assignments.get(new Slot(day, Slot.Shift.LATE, 0))) ||
                employee.equals(assignments.get(new Slot(day, Slot.Shift.LATE, 1)));
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder("Schedule{pain=" )
                .append(pain).append(", filled=").append(filled)
                .append(" assignments=\n");
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

        final Schedule copy = new Schedule();
        copy.filled = filled;
        copy.pain = pain;
        copy.assignments.putAll(assignments);
        return copy;
    }
}
