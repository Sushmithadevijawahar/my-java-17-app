package com.mgmt_sym.department_service.repository;

import com.mgmt_sym.department_service.model.DepartmentEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentEmployeeRepository extends JpaRepository<DepartmentEmployee, Long> {
    List<DepartmentEmployee> findByDepartmentId(Long departmentId);
    List<DepartmentEmployee> findByEmployeeId(Long employeeId);
    Optional<DepartmentEmployee> findByDepartmentIdAndEmployeeId(Long departmentId, Long employeeId);
    void deleteByDepartmentIdAndEmployeeId(Long departmentId, Long employeeId);
    Long countByDepartmentId(Long departmentId);
}