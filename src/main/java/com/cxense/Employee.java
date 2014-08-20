package com.cxense;

/**
 * Class representing an employee.
 */
public class Employee {

    final String name;
    final boolean experienced;
    final Preferences.PreferenceForDay[] dayPreferences;

    public Employee(final String name, final boolean experienced, final Preferences.PreferenceForDay[] dayPreferences) {
        this.name = name;
        this.experienced = experienced;
        this.dayPreferences = dayPreferences;
    }

    public int getDifficulty() {
        int difficulty = 0;
        for (Preferences.PreferenceForDay dayPreference : dayPreferences) {
            if (dayPreference == Preferences.PreferenceForDay.NONE) {
                difficulty += 2;
            } else if (dayPreference == Preferences.PreferenceForDay.EARLY || dayPreference == Preferences.PreferenceForDay.LATE) {
                difficulty += 1;
            }
        }
        return difficulty;
    }

    public Preferences.PreferenceForDay getPreference(final int day) {
        return dayPreferences[day];
    }

    @Override
    public String toString() {
        return name + " (" + (experienced ? "Y" : "N") + ")";
    }
}
