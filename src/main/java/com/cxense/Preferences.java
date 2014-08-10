package com.cxense;

import java.io.*;
import java.util.*;

/**
 * Class representing the scheduling preferences loaded from a file.
 */
public class Preferences {

    final List<Employee> employees = new ArrayList<>();
    private static final Comparator<Employee> PICKIEST = new Comparator<Employee>() {
        @Override
        public int compare(Employee o1, Employee o2) {
            return Integer.compare(o1.getDifficulty(), o2.getDifficulty());
        }
    };
    private Map<Slot, SortedSet<Employee>> possibleEmployeesBySlot;

    public Preferences(final File file) throws IOException {

        loadEmployees(file);

        buildLookups();
    }

    private void loadEmployees(File file) throws IOException {
        try (final FileReader fr = new FileReader(file);
            final BufferedReader br = new BufferedReader(fr)) {

            // Skip header
            String line;
            do {
                line = br.readLine();
            } while (line.startsWith("#") || "".equals(line));

            // Read in the preferences.
            while ((line = br.readLine()) != null) {
                employees.add(new Employee(line));
            }
        }
    }

    private void buildLookups() {

        possibleEmployeesBySlot = new HashMap<>();
        for (Employee employee : employees) {
            for (PreferenceForDay dayPreference : employee.dayPreferences) {

                if ()

            }
        }
    }

    public List<Employee> getEmployeesHardestToAssignFirst() {
        List<Employee> employeesPickiestFirst = new ArrayList<>(employees);
        Collections.sort(employeesPickiestFirst, PICKIEST);
        return employeesPickiestFirst;
    }

    public List<Employee> getAvailableEmployees(Slot slot) {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    public Stack<Slot> getShiftsHardestToAssignFirst() {

    }

    public enum PreferenceForDay {
        NONE,
        EITHER,
        EARLY,
        LATE
    }

    public static class Employee {

        final String name;
        final boolean experienced;
        final PreferenceForDay[] dayPreferences = new PreferenceForDay[7];

        public Employee(final String line) {
            final StringTokenizer tokenizer = new StringTokenizer(line, "\t");
            name = tokenizer.nextToken();
            experienced = tokenizer.nextToken().equals("Y");
            for (int i=0; i<7; i++) {
                dayPreferences[i] = PreferenceForDay.valueOf(tokenizer.nextToken().toUpperCase());
            }
        }

        public int getDifficulty() {
            int difficulty = 0;
            for (PreferenceForDay dayPreference : dayPreferences) {
                if (dayPreference == PreferenceForDay.NONE) {
                    difficulty += 2;
                } else if (dayPreference == PreferenceForDay.EARLY || dayPreference == PreferenceForDay.LATE) {
                    difficulty += 1;
                }
            }
            return difficulty;
        }

        public PreferenceForDay getPreference(int day) {
            return dayPreferences[day];
        }
    }
}
