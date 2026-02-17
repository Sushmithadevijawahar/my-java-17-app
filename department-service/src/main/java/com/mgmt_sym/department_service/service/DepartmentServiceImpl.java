package com.mgmt_sym.department_service.service;

import com.mgmt_sym.department_service.client.EmployeeClient;
import com.mgmt_sym.department_service.dto.AssignEmployeeRequest;
import com.mgmt_sym.department_service.dto.DepartmentDTO;
import com.mgmt_sym.department_service.dto.DepartmentDetailsDTO;
import com.mgmt_sym.department_service.dto.EmployeeDTO;
import com.mgmt_sym.department_service.model.Department;
import com.mgmt_sym.department_service.model.DepartmentEmployee;
import com.mgmt_sym.department_service.repository.DepartmentEmployeeRepository;
import com.mgmt_sym.department_service.repository.DepartmentRepository;
import com.mgmt_sym.department_service.service.DepartmentService;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentEmployeeRepository departmentEmployeeRepository;
    private final EmployeeClient employeeClient;

    @Override
    public DepartmentDTO createDepartment(DepartmentDTO departmentDTO) {
        log.info("Creating department: {}", departmentDTO.getDepartmentName());
        Department department = mapToEntity(departmentDTO);
        Department savedDepartment = departmentRepository.save(department);
        DepartmentDTO dto = mapToDTO(savedDepartment);
        dto.setEmployeeCount(0);
        log.info("Department created successfully with ID: {}", savedDepartment.getId());
        return dto;
    }

    @Override
    @CircuitBreaker(name = "employeeService", fallbackMethod = "getDepartmentByIdFallback")
    @Retry(name = "employeeService")
    @RateLimiter(name = "employeeService")
    public DepartmentDTO getDepartmentById(Long id) {
        log.info("Fetching department with ID: {}", id);
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
        DepartmentDTO dto = mapToDTO(department);

        // This might fail if employee service is down - circuit breaker will handle it
        try {
            dto.setEmployeeCount(departmentEmployeeRepository.countByDepartmentId(id).intValue());
        } catch (Exception e) {
            log.error("Error getting employee count: {}", e.getMessage());
            dto.setEmployeeCount(0);
        }

        return dto;
    }

    // Fallback method for getDepartmentById
    public DepartmentDTO getDepartmentByIdFallback(Long id, Exception ex) {
        log.warn("Fallback triggered for getDepartmentById. Department ID: {}, Error: {}", id, ex.getMessage());
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
        DepartmentDTO dto = mapToDTO(department);
        dto.setEmployeeCount(0); // Default value when service is down
        return dto;
    }

    @Override
    @CircuitBreaker(name = "employeeService", fallbackMethod = "getAllDepartmentsFallback")
    @Bulkhead(name = "employeeService")
    public List<DepartmentDTO> getAllDepartments() {
        log.info("Fetching all departments");
        return departmentRepository.findAll().stream()
                .map(department -> {
                    DepartmentDTO dto = mapToDTO(department);
                    try {
                        dto.setEmployeeCount(departmentEmployeeRepository.countByDepartmentId(department.getId()).intValue());
                    } catch (Exception e) {
                        log.error("Error getting employee count for department {}: {}", department.getId(), e.getMessage());
                        dto.setEmployeeCount(0);
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // Fallback method for getAllDepartments
    public List<DepartmentDTO> getAllDepartmentsFallback(Exception ex) {
        log.warn("Fallback triggered for getAllDepartments. Error: {}", ex.getMessage());
        return departmentRepository.findAll().stream()
                .map(department -> {
                    DepartmentDTO dto = mapToDTO(department);
                    dto.setEmployeeCount(0);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public DepartmentDTO updateDepartment(Long id, DepartmentDTO departmentDTO) {
        log.info("Updating department with ID: {}", id);
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));

        department.setDepartmentName(departmentDTO.getDepartmentName());
        department.setDepartmentCode(departmentDTO.getDepartmentCode());

        Department updatedDepartment = departmentRepository.save(department);
        DepartmentDTO dto = mapToDTO(updatedDepartment);
        dto.setEmployeeCount(departmentEmployeeRepository.countByDepartmentId(id).intValue());
        log.info("Department updated successfully");
        return dto;
    }

    @Override
    @Transactional
    @CircuitBreaker(name = "employeeService", fallbackMethod = "deleteDepartmentFallback")
    @Retry(name = "employeeService")
    public void deleteDepartment(Long id) {
        log.info("Deleting department with ID: {}", id);
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));

        // Remove all employee assignments
        List<DepartmentEmployee> assignments = departmentEmployeeRepository.findByDepartmentId(id);
        for (DepartmentEmployee assignment : assignments) {
            try {
                EmployeeDTO employee = employeeClient.getEmployeeById(assignment.getEmployeeId());
                employee.setDepartmentId(null);
                employeeClient.updateEmployee(employee.getId(), employee);
            } catch (Exception e) {
                log.error("Error updating employee {}: {}", assignment.getEmployeeId(), e.getMessage());
                // Continue with deletion even if employee update fails
            }
        }
        departmentEmployeeRepository.deleteAll(assignments);
        departmentRepository.delete(department);
        log.info("Department deleted successfully");
    }

    // Fallback method for deleteDepartment
    public void deleteDepartmentFallback(Long id, Exception ex) {
        log.warn("Fallback triggered for deleteDepartment. Department ID: {}, Error: {}", id, ex.getMessage());
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));

        // Delete assignments locally even if employee service is down
        List<DepartmentEmployee> assignments = departmentEmployeeRepository.findByDepartmentId(id);
        departmentEmployeeRepository.deleteAll(assignments);
        departmentRepository.delete(department);
        log.warn("Department deleted with fallback (employee service unavailable)");
    }

    @Override
    @CircuitBreaker(name = "employeeService", fallbackMethod = "getDepartmentWithEmployeesFallback")
    @Retry(name = "employeeService")
    @Bulkhead(name = "employeeService")
    public DepartmentDetailsDTO getDepartmentWithEmployees(Long id) {
        log.info("Fetching department details with employees for ID: {}", id);
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));

        List<EmployeeDTO> employees = employeeClient.getEmployeesByDepartmentId(id);

        DepartmentDetailsDTO detailsDTO = new DepartmentDetailsDTO();
        detailsDTO.setId(department.getId());
        detailsDTO.setDepartmentName(department.getDepartmentName());
        detailsDTO.setDepartmentCode(department.getDepartmentCode());
        detailsDTO.setEmployees(employees);

        log.info("Department details fetched successfully with {} employees", employees.size());
        return detailsDTO;
    }

    // Fallback method for getDepartmentWithEmployees
    public DepartmentDetailsDTO getDepartmentWithEmployeesFallback(Long id, Exception ex) {
        log.warn("Fallback triggered for getDepartmentWithEmployees. Department ID: {}, Error: {}", id, ex.getMessage());
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));

        DepartmentDetailsDTO detailsDTO = new DepartmentDetailsDTO();
        detailsDTO.setId(department.getId());
        detailsDTO.setDepartmentName(department.getDepartmentName());
        detailsDTO.setDepartmentCode(department.getDepartmentCode());

        detailsDTO.setEmployees(new ArrayList<>()); // Empty list when service is down

        return detailsDTO;
    }

    @Override
    @Transactional
    @CircuitBreaker(name = "employeeService", fallbackMethod = "assignEmployeesToDepartmentFallback")
    @Retry(name = "employeeService")
    public void assignEmployeesToDepartment(AssignEmployeeRequest request) {
        log.info("Assigning {} employees to department ID: {}", request.getEmployeeIds().size(), request.getDepartmentId());
        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + request.getDepartmentId()));

        for (Long employeeId : request.getEmployeeIds()) {
            // Check if already assigned
            if (departmentEmployeeRepository.findByDepartmentIdAndEmployeeId(
                    request.getDepartmentId(), employeeId).isPresent()) {
                log.warn("Employee {} already assigned to department {}", employeeId, request.getDepartmentId());
                continue;
            }

            // Create assignment
            DepartmentEmployee assignment = new DepartmentEmployee();
            assignment.setDepartmentId(request.getDepartmentId());
            assignment.setEmployeeId(employeeId);
            departmentEmployeeRepository.save(assignment);

            // Update employee's department
            EmployeeDTO employee = employeeClient.getEmployeeById(employeeId);
            employee.setDepartmentId(request.getDepartmentId());
            employeeClient.updateEmployee(employeeId, employee);
        }
        log.info("Employees assigned successfully");
    }

    // Fallback method for assignEmployeesToDepartment
    public void assignEmployeesToDepartmentFallback(AssignEmployeeRequest request, Exception ex) {
        log.warn("Fallback triggered for assignEmployeesToDepartment. Error: {}", ex.getMessage());
        log.info("Creating local assignment records only (employee service unavailable)");

        for (Long employeeId : request.getEmployeeIds()) {
            if (departmentEmployeeRepository.findByDepartmentIdAndEmployeeId(
                    request.getDepartmentId(), employeeId).isPresent()) {
                continue;
            }

            // Create assignment locally
            DepartmentEmployee assignment = new DepartmentEmployee();
            assignment.setDepartmentId(request.getDepartmentId());
            assignment.setEmployeeId(employeeId);
            departmentEmployeeRepository.save(assignment);
        }
        log.warn("Assignments created locally. Manual sync required when employee service is available.");
    }

    @Override
    @Transactional
    @CircuitBreaker(name = "employeeService", fallbackMethod = "removeEmployeeFromDepartmentFallback")
    @Retry(name = "employeeService")
    public void removeEmployeeFromDepartment(Long departmentId, Long employeeId) {
        log.info("Removing employee {} from department {}", employeeId, departmentId);
        DepartmentEmployee assignment = departmentEmployeeRepository
                .findByDepartmentIdAndEmployeeId(departmentId, employeeId)
                .orElseThrow(() -> new RuntimeException("Employee assignment not found"));

        departmentEmployeeRepository.delete(assignment);

        // Update employee's department to null
        EmployeeDTO employee = employeeClient.getEmployeeById(employeeId);
        employee.setDepartmentId(null);
        employeeClient.updateEmployee(employeeId, employee);
        log.info("Employee removed successfully");
    }

    // Fallback method for removeEmployeeFromDepartment
    public void removeEmployeeFromDepartmentFallback(Long departmentId, Long employeeId, Exception ex) {
        log.warn("Fallback triggered for removeEmployeeFromDepartment. Error: {}", ex.getMessage());
        DepartmentEmployee assignment = departmentEmployeeRepository
                .findByDepartmentIdAndEmployeeId(departmentId, employeeId)
                .orElseThrow(() -> new RuntimeException("Employee assignment not found"));

        departmentEmployeeRepository.delete(assignment);
        log.warn("Employee removed locally. Manual sync required when employee service is available.");
    }

    @Override
    @CircuitBreaker(name = "employeeService", fallbackMethod = "getAvailableEmployeesFallback")
    @Retry(name = "employeeService")
    @Bulkhead(name = "employeeService")
    public List<EmployeeDTO> getAvailableEmployees() {
        log.info("Fetching available employees");

        List<EmployeeDTO> allEmployees =
                employeeClient.getAllEmployeesWithOutPagination();  // ✅ CORRECT METHOD

        List<EmployeeDTO> available = allEmployees.stream()
                .filter(emp -> emp.getDepartmentId() == null)
                .toList();

        log.info("Found {} available employees", available.size());
        log.info("Total employees fetched: {}", allEmployees.size());

        return available;
    }


    // Fallback method for getAvailableEmployees
    public List<EmployeeDTO> getAvailableEmployeesFallback(Exception ex) {
        log.warn("Fallback triggered for getAvailableEmployees. Error: {}", ex.getMessage());
        return Collections.emptyList();
    }

    @Override
    @CircuitBreaker(name = "employeeService", fallbackMethod = "getDepartmentsByStatusFallback")
    public List<DepartmentDTO> getDepartmentsByStatus(String status) {
        log.info("Fetching departments with status: {}", status);
        return departmentRepository.findByStatus(status).stream()
                .map(department -> {
                    DepartmentDTO dto = mapToDTO(department);
                    try {
                        dto.setEmployeeCount(departmentEmployeeRepository.countByDepartmentId(department.getId()).intValue());
                    } catch (Exception e) {
                        log.error("Error getting employee count: {}", e.getMessage());
                        dto.setEmployeeCount(0);
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // Fallback method for getDepartmentsByStatus
    public List<DepartmentDTO> getDepartmentsByStatusFallback(String status, Exception ex) {
        log.warn("Fallback triggered for getDepartmentsByStatus. Error: {}", ex.getMessage());
        return departmentRepository.findByStatus(status).stream()
                .map(department -> {
                    DepartmentDTO dto = mapToDTO(department);
                    dto.setEmployeeCount(0);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private DepartmentDTO mapToDTO(Department department) {
        DepartmentDTO dto = new DepartmentDTO();
        dto.setId(department.getId());
        dto.setDepartmentName(department.getDepartmentName());
        dto.setDepartmentCode(department.getDepartmentCode());
        return dto;
    }

    private Department mapToEntity(DepartmentDTO dto) {
        Department department = new Department();
        department.setId(dto.getId());
        department.setDepartmentName(dto.getDepartmentName());
        department.setDepartmentCode(dto.getDepartmentCode());
        return department;
    }
}