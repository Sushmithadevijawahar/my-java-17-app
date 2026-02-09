package com.mgmt_sym.department_service.client;
import com.mgmt_sym.department_service.dto.EmployeeDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "employee-service")
public interface EmployeeClient {

    @GetMapping("/api/employees/{id}")
    EmployeeDTO getEmployeeById(@PathVariable("id") Long id);

    @GetMapping("/api/employees")
    List<EmployeeDTO> getAllEmployees();

    @GetMapping("/api/employees/department/{departmentId}")
    List<EmployeeDTO> getEmployeesByDepartmentId(@PathVariable("departmentId") Long departmentId);

    @PutMapping("/api/employees/{id}")
    EmployeeDTO updateEmployee(@PathVariable("id") Long id, @RequestBody EmployeeDTO employeeDTO);
}