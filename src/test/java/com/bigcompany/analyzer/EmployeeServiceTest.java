package com.bigcompany.analyzer;
import com.bigcompany.Employee;
import com.bigcompany.analyzer.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeServiceTest {

    private EmployeeService service;

    @BeforeEach
    void setUp() {
        service = new EmployeeService();
    }

    @Test
    void testLoadEmployees_buildsHierarchyCorrectly() throws IOException {
        // Sample CSV input (header + employees)
        String csv = """
                id,firstName,lastName,salary,managerId
                1,CEO,,200000,
                2,Alice,Smith,150000,1
                3,Bob,Jones,120000,1
                4,Charlie,Brown,90000,2
                """;

        BufferedReader reader = new BufferedReader(new StringReader(csv));
        Map<Integer, Employee> employees = service.loadEmployees(reader);

        // Assertions
        assertEquals(4, employees.size());
        Employee ceo = employees.get(1);
        assertNull(ceo.getManagerId(), "CEO should not have a manager");
        assertEquals(2, ceo.getSubordinates().size(), "CEO should have 2 direct reports");

        Employee alice = employees.get(2);
        assertEquals(1, alice.getManagerId());
        assertEquals(1, alice.getSubordinates().size(), "Alice should have 1 subordinate");
        assertEquals("Charlie", alice.getSubordinates().get(0).getFirstName());
    }

    @Test
    void testFindCEO_returnsCorrectEmployee() throws IOException {
        String csv = """
                id,firstName,lastName,salary,managerId
                1,CEO,,200000,
                2,Alice,Smith,150000,1
                """;

        BufferedReader reader = new BufferedReader(new StringReader(csv));
        Map<Integer, Employee> employees = service.loadEmployees(reader);

        Employee ceo = service.findCEO(employees);
        assertEquals(1, ceo.getId());
        assertEquals("CEO", ceo.getFirstName());
        assertNull(ceo.getManagerId());
    }

    @Test
    void testFindCEO_throwsIfNoCEO() {
        Map<Integer, Employee> employees = Map.of(
                2, new Employee(2, "Alice", "Smith", 150000, 3),
                3, new Employee(3, "Bob", "Jones", 120000, 2)
        );

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> service.findCEO(employees));

        assertEquals("No CEO found in file", ex.getMessage());
    }
}
