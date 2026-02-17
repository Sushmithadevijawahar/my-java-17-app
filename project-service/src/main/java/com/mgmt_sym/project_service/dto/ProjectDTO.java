package com.mgmt_sym.project_service.dto;

import com.mgmt_sym.project_service.model.Project.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class ProjectDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateProjectRequest {
        @NotBlank(message = "Project name is required")
        private String name;

        private String description;

        @NotNull(message = "Department ID is required")
        private Long departmentId;


    }

    @Data
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    public static class ProjectResponse {
        private Long id;
        private String name;
        private String description;
        private Long departmentId;
        private DepartmentDTO department;
        private ProjectStatus status;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private List<Long> employeeIds;
        private List<EmployeeDTO> employees;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssignEmployeesRequest {
        @NotNull(message = "Employee IDs are required")
        private List<Long> employeeIds;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateStatusRequest {
        @NotNull(message = "Status is required")
        private ProjectStatus status;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RemoveEmployeesRequest {
        @NotEmpty
        private List<Long> employeeIds;
    }

}