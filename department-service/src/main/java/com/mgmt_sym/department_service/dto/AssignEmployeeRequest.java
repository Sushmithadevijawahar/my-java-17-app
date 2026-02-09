package com.mgmt_sym.department_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignEmployeeRequest {
    private Long departmentId;
    private List<Long> employeeIds;
}