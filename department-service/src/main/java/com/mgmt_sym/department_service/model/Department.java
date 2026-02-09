package com.mgmt_sym.department_service.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "departments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String departmentName;

    @Column(unique = true, nullable = false)
    private String departmentCode;

    @Column(length = 500)
    private String description;

    private String departmentHead;

    private String status = "ACTIVE"; // ACTIVE, INACTIVE



}