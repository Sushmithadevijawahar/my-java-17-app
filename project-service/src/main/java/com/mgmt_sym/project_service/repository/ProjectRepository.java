package com.mgmt_sym.project_service.repository;


import com.mgmt_sym.project_service.dto.ProjectDTO;
import com.mgmt_sym.project_service.model.Project;
import com.mgmt_sym.project_service.model.Project.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByDepartmentId(Long departmentId);

    List<Project> findByStatus(ProjectStatus status);

    List<Project> findByEmployeeIdsContaining(Long employeeId);
    ProjectDTO.ProjectResponse removeEmployeesFromProject(
            Long projectId,
            ProjectDTO.RemoveEmployeesRequest request
    );
}