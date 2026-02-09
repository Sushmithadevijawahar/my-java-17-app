package com.mgmt_sym.employee_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "employee")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    private String designation;

    private String phoneNumber;

    private LocalDate joiningDate;

    private Double salary;

    @Column(name = "department_id")
    private Long departmentId;

    private String status = "ACTIVE"; // ACTIVE, INACTIVE
}