package com.cxense;

import java.io.*;
import java.util.*;

/**
 * Class representing the scheduling preferences loaded from a file.
 */
public class Preferences {

    private final List<Employee> employees = new ArrayList<>();
    private static final Comparator<Employee> PICKIEST = new Comparator<Employee>() {
        @Override
        public int compare(Employee o1, Employee o2) {
            return Integer.compare(o1.getDifficulty(), o2.getDifficulty());
        }
    };
    private final List<Employee>[] availableEmployeesByDay = new List[7];

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
        Employee employee = new Employee(name, experienced, dayPreferences);
        employees.add(employee);
        for (int day=0; day<7; day++) {
            PreferenceForDay dayPreference = dayPreferences[day];
            if (dayPreference != PreferenceForDay.NONE) {
                saveEmployeeAsAvailable(employee, day);
            }
        }
    }

    private void saveEmployeeAsAvailable(final Employee employee, final int day) {
        List<Employee> employees = availableEmployeesByDay[day];
        if (employees == null) {
            employees = new ArrayList<>();
            availableEmployeesByDay[day] = employees;
        }
        employees.add(employee);
    }

    public List<Employee> getEmployeesHardestToAssignFirst() {
        List<Employee> employeesPickiestFirst = new ArrayList<>(employees);
        Collections.sort(employeesPickiestFirst, PICKIEST);
        return employeesPickiestFirst;
    }

    public List<Employee> getAvailableEmployees(final int day) {
        return availableEmployeesByDay[day];
    }

    public Stack<Slot> getSlotsHardestToAssignFirst() {
        Stack<Slot> slots = new Stack<>();
        for (int day=0; day<7; day++) {
            slots.add(new Slot(day, Slot.Shift.EARLY, 0));
            slots.add(new Slot(day, Slot.Shift.EARLY, 1));
            slots.add(new Slot(day, Slot.Shift.LATE, 0));
            slots.add(new Slot(day, Slot.Shift.LATE, 1));
        }
        Collections.sort(slots, new Comparator<Slot>() {
            @Override
            public int compare(Slot o1, Slot o2) {
                return Integer.compare(availableEmployeesByDay[o1.getDay()].size(),
                        availableEmployeesByDay[o2.getDay()].size());
            }
        });
        return slots;
    }

    public enum PreferenceForDay {
        NONE,
        EITHER,
        EARLY,
        LATE
    }
}
