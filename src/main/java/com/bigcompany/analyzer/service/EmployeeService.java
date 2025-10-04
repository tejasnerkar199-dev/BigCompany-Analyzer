package com.bigcompany.analyzer.service;

import com.bigcompany.Employee;

import java.io.*;
import java.util.*;

public class EmployeeService {
    private final Map<Integer, Employee> employees = new HashMap<>();

    public Map<Integer, Employee> loadEmployees(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        while ((line = reader.readLine()) != null) {
            if (line.trim().isEmpty()) continue;
            String[] parts = line.split(",");
            int id = Integer.parseInt(parts[0]);
            String firstName = parts[1];
            String lastName = parts[2];
            double salary = Double.parseDouble(parts[3]);
            Integer managerId = parts.length > 4 && !parts[4].isEmpty() ? Integer.parseInt(parts[4]) : null;
            Employee employee = new Employee(id, firstName, lastName, salary, managerId);
            employees.put(id, employee);
        }

        for (com.bigcompany.Employee employee : employees.values()) {
            if (employee.getManagerId() != null) {
               Employee manager = employees.get(employee.getManagerId());
                if (manager != null) {
                    manager.addSubordinate(employee);
                }
            }
        }
        return employees;
    }

    public Map<Integer, Employee> loadEmployeesFromResource(String resourceName) throws IOException {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(resourceName)) {
            if (input != null) {
                return loadEmployees(new BufferedReader(new InputStreamReader(input)));
            }else{
                throw new FileNotFoundException(resourceName);
            }
        }
    }

    public Employee findCEO(Map<Integer, Employee> employees) {
        for (Employee employee : employees.values()) {
            if (employee.getManagerId() == null) {
                return employee;
            }
        }
        throw new IllegalStateException("No CEO found in file");
    }
}
