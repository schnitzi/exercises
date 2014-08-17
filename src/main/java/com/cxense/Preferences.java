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
    private final Map<Slot, List<Employee>> availableEmployeesBySlot = new HashMap<>();

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
    }

    private void addEmployee(String name, boolean experienced, PreferenceForDay[] dayPreferences) {
        Employee employee = new Employee(name, experienced, dayPreferences);
        employees.add(employee);
        for (int day=0; day<7; day++) {
            PreferenceForDay dayPreference = dayPreferences[day];
            if (dayPreference != PreferenceForDay.NONE) {
                saveEmployeeAsAvailable(employee, new Slot(day, Slot.Shift.EARLY));
                saveEmployeeAsAvailable(employee, new Slot(day, Slot.Shift.LATE));
            }
        }
    }

    private void saveEmployeeAsAvailable(final Employee employee, final Slot slot) {
        List<Employee> employees = availableEmployeesBySlot.get(slot);
        if (employees == null) {
            employees = new ArrayList<>();
            availableEmployeesBySlot.put(slot, employees);
        }
        employees.add(employee);
    }

    public List<Employee> getEmployeesHardestToAssignFirst() {
        List<Employee> employeesPickiestFirst = new ArrayList<>(employees);
        Collections.sort(employeesPickiestFirst, PICKIEST);
        return employeesPickiestFirst;
    }

    public List<Employee> getAvailableEmployees(Slot slot) {
        return availableEmployeesBySlot.get(slot);
    }

    public Stack<Slot> getSlotsHardestToAssignFirst() {
        Stack<Slot> slots = new Stack<>();
        slots.addAll(availableEmployeesBySlot.keySet());
        Collections.sort(slots, new Comparator<Slot>() {
            @Override
            public int compare(Slot o1, Slot o2) {
                return Integer.compare(availableEmployeesBySlot.get(o1).size(),
                        availableEmployeesBySlot.get(o2).size());
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
