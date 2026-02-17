package com.mgmt_sym.department_service.client;
import com.mgmt_sym.department_service.dto.EmployeeDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "employee-service")
public interface EmployeeClient {

    @GetMapping("/api/employees/{id}")
    EmployeeDTO getEmployeeById(@PathVariable("id") Long id);

    @GetMapping("/api/employees")
    List<EmployeeDTO> getAllEmployees();

    @GetMapping("/api/employees/department/{departmentId}")
    List<EmployeeDTO> getEmployeesByDepartmentId(@PathVariable("departmentId") Long departmentId);

    @GetMapping("/api/employees/available")
    List<EmployeeDTO> getAvailableEmployees();

    @GetMapping("/api/employees/all")
    List<EmployeeDTO> getAllEmployeesWithOutPagination();

    @PutMapping("/api/employees/{id}")
    EmployeeDTO updateEmployee(@PathVariable("id") Long id, @RequestBody EmployeeDTO employeeDTO);

    @GetMapping("/api/employees/by-ids")
    @CircuitBreaker(name = "employeeService", fallbackMethod = "getEmployeesByIdsFallback")
    List<EmployeeDTO> getEmployeesByIds(@RequestParam("ids") List<Long> ids);
}


