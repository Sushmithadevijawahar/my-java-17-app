package com.mgmt_sym.department_service.controller;
import com.mgmt_sym.department_service.dto.AssignEmployeeRequest;
import com.mgmt_sym.department_service.dto.DepartmentDTO;
import com.mgmt_sym.department_service.dto.DepartmentDetailsDTO;
import com.mgmt_sym.department_service.dto.EmployeeDTO;
import com.mgmt_sym.department_service.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping
    public ResponseEntity<DepartmentDTO> createDepartment(@RequestBody DepartmentDTO departmentDTO) {
        DepartmentDTO createdDepartment = departmentService.createDepartment(departmentDTO);
        return new ResponseEntity<>(createdDepartment, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentDTO> getDepartmentById(@PathVariable Long id) {
        DepartmentDTO department = departmentService.getDepartmentById(id);
        return ResponseEntity.ok(department);
    }

    @GetMapping
    public ResponseEntity<List<DepartmentDTO>> getAllDepartments() {
        List<DepartmentDTO> departments = departmentService.getAllDepartments();
        return ResponseEntity.ok(departments);
    }

    @GetMapping("/{id}/noofemp")
    public ResponseEntity<DepartmentDetailsDTO> getDepartmentWithEmployees(@PathVariable Long id) {
        DepartmentDetailsDTO departmentDetails = departmentService.getDepartmentWithEmployees(id);
        return ResponseEntity.ok(departmentDetails);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<DepartmentDTO>> getDepartmentsByStatus(@PathVariable String status) {
        List<DepartmentDTO> departments = departmentService.getDepartmentsByStatus(status);
        return ResponseEntity.ok(departments);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartmentDTO> updateDepartment(@PathVariable Long id, @RequestBody DepartmentDTO departmentDTO) {
        DepartmentDTO updatedDepartment = departmentService.updateDepartment(id, departmentDTO);
        return ResponseEntity.ok(updatedDepartment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/employees")
    public ResponseEntity<Void> assignEmployeesToDepartment(@RequestBody AssignEmployeeRequest request) {
        departmentService.assignEmployeesToDepartment(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{departmentId}/employees/{employeeId}")
    public ResponseEntity<Void> removeEmployeeFromDepartment(
            @PathVariable Long departmentId,
            @PathVariable Long employeeId) {
        departmentService.removeEmployeeFromDepartment(departmentId, employeeId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/employees/available")
    public ResponseEntity<List<EmployeeDTO>> getAvailableEmployees() {
        List<EmployeeDTO> employees = departmentService.getAvailableEmployees();
        return ResponseEntity.ok(employees);
    }
}