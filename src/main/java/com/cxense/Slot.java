package com.cxense;

/**
 * A day (0=Monday to 6=Sunday) plus a time (early or late).
 */
public class Slot {

    public enum Shift {
        EARLY,
        LATE
    };

    private final int day;
    private final Shift shift;

    public Slot(final int day, final Shift shift) {
        this.day = day;
        this.shift = shift;
    }

    public int getDay() {
        return day;
    }

    public Shift getShift() {
        return shift;
    }

    public Slot getOtherShift() {
        return new Slot(day, shift == Shift.EARLY ? Shift.LATE : Shift.EARLY);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Slot slot = (Slot) o;

        if (day != slot.day) return false;
        if (shift != slot.shift) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = day;
        result = 31 * result + shift.ordinal();
        return result;
    }
}
