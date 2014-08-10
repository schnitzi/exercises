package com.cxense;

/**
 * A day (0=Monday to 6=Sunday) plus a time (early or late).
 */
public class Slot {

    public static final int EARLY = 0;
    public static final int LATE = 1;

    private final int day;
    private final int shift;

    public Slot(final int day, final int shift) {
        this.day = day;
        this.shift = shift;
    }

    public int getDay() {
        return day;
    }

    public int getShift() {
        return shift;
    }

    public int getOtherShift() {
        return shift == EARLY ? LATE : EARLY;
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
        result = 31 * result + shift;
        return result;
    }
}
