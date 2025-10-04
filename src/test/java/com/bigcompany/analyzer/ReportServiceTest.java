package com.bigcompany.analyzer;
import com.bigcompany.Employee;
import com.bigcompany.analyzer.config.ConfigLoader;
import com.bigcompany.analyzer.model.GlobalConstants;
import com.bigcompany.analyzer.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportServiceTest {

    private ReportService reportService;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        reportService = new ReportService();
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @Test
    void testCheckSalaries_detectsTooMuchAndTooLittle() {
        // Mock ConfigLoader
        try (MockedStatic<ConfigLoader> mocked = mockStatic(ConfigLoader.class)) {
            mocked.when(() -> ConfigLoader.getDouble(GlobalConstants.MIN_PERCENT)).thenReturn(0.9);
            mocked.when(() -> ConfigLoader.getDouble(GlobalConstants.MAX_PERCENT)).thenReturn(1.1);

            // Build employee hierarchy
            Employee ceo = new Employee(1, "CEO", "", 200_000, null);
            Employee alice = new Employee(2, "Alice", "", 150_000, 1);
            Employee bob = new Employee(3, "Bob", "", 100_000, 1);

            ceo.addSubordinate(alice);
            ceo.addSubordinate(bob);

            // Run the salary check
            reportService.checkSalaries(ceo);

            String output = outContent.toString();

            // CEO salary = 200k, subordinates avg = 125k → max = 125k*1.1=137.5k, min=125k*0.9=112.5k
            assertTrue(output.contains("Managers earning TOO MUCH"));
            assertTrue(output.contains("CEO"), "CEO should be detected as earning too much");

            assertTrue(output.contains("Managers earning TOO LITTLE"));
            assertFalse(output.contains("Alice"), "Alice is not manager of anyone → no too little");
        }
    }

    @Test
    void testFormatTooMuchAndTooLittle_individualMethods() {
        Employee manager = new Employee(1, "John", "Doe", 120_000, null);

        // Use reflection to call private methods
        String tooMuch = invokePrivateFormatTooMuch(manager, 100_000);
        String tooLittle = invokePrivateFormatTooLittle(manager, 130_000);

        assertEquals("John Doe (120000.00 > 100000.00)", tooMuch);
        assertEquals("John Doe (120000.00 < 130000.00)", tooLittle);
    }

    // Helper methods to invoke private formatting methods
    private String invokePrivateFormatTooMuch(Employee manager, double max) {
        try {
            var method = ReportService.class.getDeclaredMethod("formatTooMuch", Employee.class, double.class);
            method.setAccessible(true);
            return (String) method.invoke(reportService, manager, max);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String invokePrivateFormatTooLittle(Employee manager, double min) {
        try {
            var method = ReportService.class.getDeclaredMethod("formatTooLittle", Employee.class, double.class);
            method.setAccessible(true);
            return (String) method.invoke(reportService, manager, min);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
