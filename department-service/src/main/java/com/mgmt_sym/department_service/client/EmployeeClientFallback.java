package com.mgmt_sym.department_service.client;


import com.mgmt_sym.department_service.dto.EmployeeDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class EmployeeClientFallback implements EmployeeClient {

    @Override
    public List<EmployeeDTO> getAllEmployees() {
        log.warn("Fallback triggered for getAllEmployees");
        return new ArrayList<>();
    }

    @Override
    public List<EmployeeDTO> getEmployeesByDepartmentId(Long departmentId) {
        return List.of();
    }

    @Override
    public List<EmployeeDTO> getAvailableEmployees() {
        return List.of();
    }

    @Override
    public List<EmployeeDTO> getAllEmployeesWithOutPagination() {
        return List.of();
    }

    @Override
    public EmployeeDTO updateEmployee(Long id, EmployeeDTO employeeDTO) {
        return null;
    }

    @Override
    public EmployeeDTO getEmployeeById(Long id) {
        log.warn("Fallback triggered for getEmployeeById with id: {}", id);
        EmployeeDTO fallbackEmployee = new EmployeeDTO();
        fallbackEmployee.setId(id);
        fallbackEmployee.setFirstName("Employee");
        fallbackEmployee.setLastName("Unavailable");
        fallbackEmployee.setEmail("unavailable@example.com");
        return fallbackEmployee;
    }

    @Override
    public List<EmployeeDTO> getEmployeesByIds(List<Long> ids) {
        log.warn("Fallback triggered for getEmployeesByIds with ids: {}", ids);
        List<EmployeeDTO> fallbackList = new ArrayList<>();
        for (Long id : ids) {
            fallbackList.add(getEmployeeById(id));
        }
        return fallbackList;
    }
}