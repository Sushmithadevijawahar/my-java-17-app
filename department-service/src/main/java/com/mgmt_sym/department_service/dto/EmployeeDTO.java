package com.mgmt_sym.department_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String designation;
    private String phoneNumber;
    private LocalDate joiningDate;
    private Double salary;
    private Long departmentId;
    private String status;

}