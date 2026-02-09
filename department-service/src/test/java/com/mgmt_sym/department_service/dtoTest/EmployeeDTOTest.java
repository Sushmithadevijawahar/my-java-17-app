package com.mgmt_sym.department_service.dtoTest;

import com.mgmt_sym.department_service.dto.EmployeeDTO;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeDTOTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        EmployeeDTO employee = new EmployeeDTO();

        employee.setId(1L);
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setEmail("john.doe@example.com");
        employee.setDesignation("Developer");
        employee.setPhoneNumber("1234567890");
        employee.setJoiningDate(LocalDate.now());
        employee.setSalary(75000.0);
        employee.setDepartmentId(10L);
        employee.setStatus("ACTIVE");

        assertEquals(1L, employee.getId());
        assertEquals("John", employee.getFirstName());
        assertEquals("Doe", employee.getLastName());
        assertEquals("john.doe@example.com", employee.getEmail());
        assertEquals("Developer", employee.getDesignation());
        assertEquals("1234567890", employee.getPhoneNumber());
        assertEquals("ACTIVE", employee.getStatus());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDate joiningDate = LocalDate.of(2024, 1, 1);

        EmployeeDTO employee = new EmployeeDTO(
                1L,
                "Jane",
                "Smith",
                "jane.smith@example.com",
                "Manager",
                "9876543210",
                joiningDate,
                90000.0,
                20L,
                "ACTIVE"
        );

        assertNotNull(employee);
        assertEquals("Jane", employee.getFirstName());
        assertEquals(90000.0, employee.getSalary());
        assertEquals(joiningDate, employee.getJoiningDate());
    }

    @Test
    void testEqualsAndHashCode() {
        EmployeeDTO emp1 = new EmployeeDTO();
        emp1.setId(1L);
        emp1.setEmail("a@test.com");

        EmployeeDTO emp2 = new EmployeeDTO();
        emp2.setId(1L);
        emp2.setEmail("a@test.com");

        assertEquals(emp1, emp2);
        assertEquals(emp1.hashCode(), emp2.hashCode());
    }

    @Test
    void testToString() {
        EmployeeDTO employee = new EmployeeDTO();
        employee.setFirstName("Debug");

        String result = employee.toString();

        assertTrue(result.contains("Debug"));
    }
}