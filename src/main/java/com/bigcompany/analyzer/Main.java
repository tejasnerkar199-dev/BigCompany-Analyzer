package com.bigcompany.analyzer;

import com.bigcompany.Employee;
import com.bigcompany.EmployeeService;
import com.bigcompany.analyzer.service.ReportService;

import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {
        String filePath = "src/main/resources/employees.csv";

        EmployeeService employeeService = new EmployeeService();
        Map<Integer, Employee> employees = employeeService.loadEmployees(filePath);

        Employee ceo = employeeService.findCEO(employees);

        ReportService reportService = new ReportService();
        reportService.checkSalaries(ceo);
        reportService.checkDepth(ceo);
    }
}