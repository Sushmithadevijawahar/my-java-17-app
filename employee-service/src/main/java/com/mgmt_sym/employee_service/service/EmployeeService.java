package com.mgmt_sym.employee_service.service;

import com.mgmt_sym.employee_service.dto.EmployeeDTO;

import java.util.List;

import com.mgmt_sym.employee_service.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface EmployeeService {
    EmployeeDTO createEmployee(EmployeeDTO employeeDTO);
    EmployeeDTO getEmployeeById(Long id);
    List<EmployeeDTO> getAllEmployees();
    List<EmployeeDTO> getEmployeesByDepartmentId(Long departmentId);
    EmployeeDTO updateEmployee(Long id, EmployeeDTO employeeDTO);
    void deleteEmployee(Long id);
    List<EmployeeDTO> getEmployeesByStatus(String status);
    Page<EmployeeDTO> getAllEmployees(Pageable pageable);
    List<EmployeeDTO> getAvailableEmployees();
    List<EmployeeDTO> getAllEmployeesWithoutPaging();
}