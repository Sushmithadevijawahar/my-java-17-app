package com.mgmt_sym.project_service.service;

import com.mgmt_sym.project_service.dto.ProjectDTO;
import com.mgmt_sym.project_service.model.Project;

public class ProjectMapper {

        private ProjectMapper() {}

        public static ProjectDTO.ProjectResponse toResponse(Project project) {
            return ProjectDTO.ProjectResponse.builder()
                    .id(project.getId())
                    .name(project.getName())
                    .status(project.getStatus())
                    .employeeIds(project.getEmployeeIds())
                    .createdAt(project.getCreatedAt())
                    .updatedAt(project.getUpdatedAt())
                    .build();
        }
    }


