package com.bigcompany.analyzer.service;


import com.bigcompany.Employee;
import com.bigcompany.analyzer.config.ConfigLoader;
import com.bigcompany.analyzer.model.GlobalConstants;

import java.util.*;

public class ReportService {

    public void checkSalaries(Employee ceo) {
        Queue<Employee> queue = new LinkedList<>();
        queue.add(ceo);

        List<String> tooMuch = new ArrayList<>();
        List<String> tooLittle = new ArrayList<>();

        while (!queue.isEmpty()) {
            Employee manager = queue.poll();
            List<Employee> subs = manager.getSubordinates();

            if (!subs.isEmpty()) {
                double avg = subs.stream().mapToDouble(Employee::getSalary).average().orElse(0);

                double minPercentage = ConfigLoader.getDouble(GlobalConstants.MIN_PERCENT);
                double maxPercentage = ConfigLoader.getDouble(GlobalConstants.MAX_PERCENT);

                double min = avg * minPercentage;
                double max = avg * maxPercentage;


                if (manager.getSalary() < min) {
                    tooLittle.add(formatTooLittle(manager, min));
                } else if (manager.getSalary() > max) {
                    tooMuch.add(formatTooMuch(manager, max));
                }
            }
            queue.addAll(subs);
        }

        System.out.println("Managers earning TOO MUCH:");
        tooMuch.forEach(System.out::println);

        System.out.println("\nManagers earning TOO LITTLE:");
        tooLittle.forEach(System.out::println);

       System.out.println();
    }


    public void checkDepth(Employee ceo) {
        checkDepthRecursive(ceo, 0);
    }

    private void checkDepthRecursive(Employee e, int depth) {
        int reportingLine = Integer.parseInt(ConfigLoader.get(GlobalConstants.REPORTING_LINE_MAX));
        if (depth > reportingLine) {
            System.out.printf("Employee %s has reporting line TOO LONG by %d%n",
                    e.getFirstName() + " " + e.getLastName(), depth - reportingLine);
        }
        for (Employee sub : e.getSubordinates()) {
            checkDepthRecursive(sub, depth + 1);
        }
    }

    private String formatTooMuch(Employee manager, double maxAllowed) {
        return String.format("%s %s (%.2f > %.2f)",
                manager.getFirstName(),
                manager.getLastName(),
                manager.getSalary(),
                maxAllowed);
    }

    private String formatTooLittle(Employee manager, double minAllowed) {
        return String.format("%s %s (%.2f < %.2f)",
                manager.getFirstName(),
                manager.getLastName(),
                manager.getSalary(),
                minAllowed);
    }
}