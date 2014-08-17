package com.cxense;

import java.util.*;

/**
 * Schedule created by running the {@link Rostering} class against a set of {@link Preferences}.
 */
public class Schedule {

    private static final int PAIN_FOR_TWO_INEXPERIENCED_ON_SAME_SHIFT = 1;
    private static final int PAIN_FOR_WORKING_NOT_PREFERRED_SHIFT = 1;
    private static final String DAY_NAMES[] = new String[] {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

    // The assignments of employee to shift, for each day of the week, for both shifts.
    final Map<Slot, Set<Employee>> assignments = new HashMap<>();

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

            final Slot earlySlot = new Slot(day, Slot.Shift.EARLY);
            final Slot lateSlot = new Slot(day, Slot.Shift.LATE);
            Set<Employee> currentEarly = assignments.get(earlySlot);
            Set<Employee> currentLate = assignments.get(lateSlot);

            // Check the early shift.
            if (currentEarly == null && !employee.equals(currentLate) && employee.canWork(earlySlot)) {
                possibleSlots.add(earlySlot);
            }

            // Check the late shift.
            if (currentLate == null && !employee.equals(currentEarly) && employee.canWork(lateSlot)) {
                possibleSlots.add(lateSlot);
            }
        }
        return possibleSlots;
    }

    public int assign(final Employee employee, final Slot slot) {
        Set<Employee> current = assignments.get(slot);
        if (current == null) {
            current = new HashSet<>();
            assignments.put(slot, current);
        }
        
        int pain = 0;
        if (!employee.experienced  && !current.isEmpty()) {
            assert current.size() == 1;
            Employee otherEmployeeOnShift = current.iterator().next();
            if (!otherEmployeeOnShift.experienced) {
                pain += PAIN_FOR_TWO_INEXPERIENCED_ON_SAME_SHIFT;
            }
        }
        Preferences.PreferenceForDay preferenceForDay = employee.getPreference(slot.getDay());
        if ((slot.getShift() == Slot.Shift.EARLY && preferenceForDay == Preferences.PreferenceForDay.LATE) ||
                (slot.getShift() == Slot.Shift.LATE && preferenceForDay == Preferences.PreferenceForDay.EARLY)) {
            pain += PAIN_FOR_WORKING_NOT_PREFERRED_SHIFT;
        }
        current.add(employee);
        return pain;
    }

    public void unassign(final Slot slot) {
        assignments.remove(slot);
    }

    public boolean isAssigned(final Slot slot) {
        return assignments.containsKey(slot);
    }

    public boolean canAssign(final Employee employee, final Slot slot) {
        return !assignments.containsKey(slot) &&
                !employee.equals(assignments.get(slot.getOtherShift()));
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder("Schedule{pain=" )
                .append(pain).append(", filled=").append(filled)
                .append("assignments=\n");
        for (int day=0; day<7; day++) {
            builder.append(DAY_NAMES[day]).append("\n  early=");
            Set<Employee> early = assignments.get(new Slot(day, Slot.Shift.EARLY));
            if (early != null) {
                for (Employee employee : early) {
                    builder.append(employee).append("\n");
                }
            }
            builder.append("\n  late=");
            Set<Employee> late = assignments.get(new Slot(day, Slot.Shift.LATE));
            if (late != null) {
                for (Employee employee : late) {
                    builder.append(employee).append("\n");
                }
            }
        }
        builder.append('}');
        return builder.toString();
    }
}
