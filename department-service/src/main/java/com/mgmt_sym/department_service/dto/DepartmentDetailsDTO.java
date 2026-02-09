package com.mgmt_sym.department_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDetailsDTO {
    private Long id;
    private String departmentName;
    private String departmentCode;
    private List<EmployeeDTO> employees;
}