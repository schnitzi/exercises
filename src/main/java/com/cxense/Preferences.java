package com.cxense;

import java.io.*;
import java.util.*;

/**
 * Class representing the scheduling preferences loaded from a file.
 */
public class Preferences {

    private static final Comparator<Employee> PICKIEST = new Comparator<Employee>() {
        @Override
        public int compare(Employee o1, Employee o2) {
            final int compare = Integer.compare(o2.getDifficulty(), o1.getDifficulty());
            if (compare != 0) return compare;
            return o1.getName().compareTo(o2.getName());
        }
    };

    private final List<Employee> employees = new ArrayList<>();
    private final SortedSet<Employee>[] availableEmployeesByDay = new SortedSet[7];

    public List<Employee> getEmployees() {
        return employees;
    }

    public Preferences(final File file) throws IOException {

        try (final FileReader fr = new FileReader(file);
            final BufferedReader br = new BufferedReader(fr)) {

            // Skip header
            String line;
            do {
                line = br.readLine();
            } while (line.startsWith("#") || "".equals(line));

            // Read in the preferences.
            while ((line = br.readLine()) != null) {
                final StringTokenizer tokenizer = new StringTokenizer(line, "\t");
                final String name = tokenizer.nextToken();
                final boolean experienced = tokenizer.nextToken().equals("Y");
                final PreferenceForDay[] dayPreferences = new PreferenceForDay[7];
                for (int i=0; i<7; i++) {
                    dayPreferences[i] = PreferenceForDay.valueOf(tokenizer.nextToken().toUpperCase());
                }

                addEmployee(name, experienced, dayPreferences);
            }
        }

        for (int day=0; day<7; day++) {
            if (availableEmployeesByDay[day] == null) {
                System.err.println("No available employees for day " + day);
                System.exit(-1);
            }
        }
    }

    private void addEmployee(String name, boolean experienced, PreferenceForDay[] dayPreferences) {
        final Employee employee = new Employee(name, experienced, dayPreferences);
        employees.add(employee);
        for (int day=0; day<7; day++) {
            PreferenceForDay dayPreference = dayPreferences[day];
            if (dayPreference != PreferenceForDay.NONE) {
                saveEmployeeAsAvailable(employee, day);
            }
        }
    }

    private void saveEmployeeAsAvailable(final Employee employee, final int day) {
        SortedSet<Employee> employees = availableEmployeesByDay[day];
        if (employees == null) {
            employees = new TreeSet<>(PICKIEST);
            availableEmployeesByDay[day] = employees;
        }
        employees.add(employee);
    }

    public SortedSet<Employee> getAvailableEmployees(final int day) {
        return availableEmployeesByDay[day];
    }

    public Stack<Slot> getSlotsHardestToAssignFirst() {
        SortedSet<Slot> slots = new TreeSet<>(new Comparator<Slot>() {
            @Override
            public int compare(Slot o1, Slot o2) {
                int compare = Integer.compare(availableEmployeesByDay[o2.getDay()].size(),
                        availableEmployeesByDay[o1.getDay()].size());
                if (compare != 0) return compare;
                compare = Integer.compare(o1.getDay(), o2.getDay());
                if (compare != 0) return compare;
                compare = o1.getShift().compareTo(o2.getShift());
                if (compare != 0) return compare;
                return Integer.compare(o1.getIndex(), o2.getIndex());
            }
        });
        for (int day=0; day<7; day++) {
            slots.add(new Slot(day, Slot.Shift.EARLY, 0));
            slots.add(new Slot(day, Slot.Shift.EARLY, 1));
            slots.add(new Slot(day, Slot.Shift.LATE, 0));
            slots.add(new Slot(day, Slot.Shift.LATE, 1));
        }

        final Stack<Slot> stack = new Stack<>();
        stack.addAll(slots);
        return stack;
    }

    public enum PreferenceForDay {
        NONE,
        EITHER,
        EARLY,
        LATE
    }
}
