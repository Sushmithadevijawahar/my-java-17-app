package com.mgmt_sym.department_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "department_employees")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentEmployee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "department_id", nullable = false)
    private Long departmentId;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Column(name = "effective_date")
    private LocalDate effectiveDate;

    @Column(name = "role_in_department")
    private String roleInDepartment; // Manager, Staff, etc.

    @Column(name = "assigned_at")
    private LocalDate assignedAt;

    @PrePersist
    protected void onCreate() {
        if (assignedAt == null) {
            assignedAt = LocalDate.now();
        }
        if (effectiveDate == null) {
            effectiveDate = LocalDate.now();
        }
    }
}