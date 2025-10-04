package com.bigcompany.analyzer;

import com.bigcompany.Employee;
import com.bigcompany.analyzer.model.GlobalConstants;
import com.bigcompany.analyzer.service.EmployeeService;
import com.bigcompany.analyzer.service.ReportService;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {
        EmployeeService employeeService = new EmployeeService();
        //Load Employees from employees.csv file
        Map<Integer, Employee> employees =  employeeService.loadEmployeesFromResource(GlobalConstants.FILE_NAME);

        //find CEO of company
        Employee ceo = employeeService.findCEO(employees);

        ReportService reportService = new ReportService();

        //Reporting
        reportService.checkSalaries(ceo);
        reportService.checkDepth(ceo);
    }
}