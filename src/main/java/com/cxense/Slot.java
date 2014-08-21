package com.cxense;

/**
 * A tuple consisting of a day (0=Monday to 6=Sunday), a time (early or late), and an index
 * (0 or 1, representing the two spaces that need to be filled on a shift).
 */
public class Slot {

    /**
     * Enum for early versus late shift.
     */
    public enum Shift {
        EARLY,
        LATE
    }
    private final int day;

    private final Shift shift;
    private final int index;
    public Slot(final int day, final Shift shift, int index) {
        this.day = day;
        this.shift = shift;
        this.index = index;
    }

    public int getDay() {
        return day;
    }

    public Shift getShift() {
        return shift;
    }

    public int getIndex() {
        return index;
    }

    public Slot getOtherSlotOnSameShift() {
        return new Slot(day, shift, 1-index);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Slot slot = (Slot) o;

        if (day != slot.day) return false;
        if (shift != slot.shift) return false;
        if (index != slot.index) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = day;
        result = 31 * result + shift.ordinal();
        result = 31 * result + index;
        return result;
    }
}
