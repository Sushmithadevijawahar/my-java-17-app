package com.mgmt_sym.department_service.service;
import com.mgmt_sym.department_service.dto.AssignEmployeeRequest;
import com.mgmt_sym.department_service.dto.DepartmentDTO;
import com.mgmt_sym.department_service.dto.DepartmentDetailsDTO;
import com.mgmt_sym.department_service.dto.EmployeeDTO;
import java.util.List;

public interface DepartmentService {
    DepartmentDTO createDepartment(DepartmentDTO departmentDTO);
    DepartmentDTO getDepartmentById(Long id);
    List<DepartmentDTO> getAllDepartments();
    DepartmentDTO updateDepartment(Long id, DepartmentDTO departmentDTO);
    void deleteDepartment(Long id);
    DepartmentDetailsDTO getDepartmentWithEmployees(Long id);
    void assignEmployeesToDepartment(AssignEmployeeRequest request);
    void removeEmployeeFromDepartment(Long departmentId, Long employeeId);
    List<EmployeeDTO> getAvailableEmployees();
    List<DepartmentDTO> getDepartmentsByStatus(String status);
}