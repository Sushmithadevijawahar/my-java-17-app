package com.mgmt_sym.project_service.controller;


import com.mgmt_sym.project_service.dto.ProjectDTO;
import com.mgmt_sym.project_service.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Slf4j
public class ProjectController {

    private final ProjectService projectService;

    /**
     * Create a new project
     * POST /projects
     */
    @PostMapping
    public ResponseEntity<ProjectDTO.ProjectResponse> createProject(
            @Valid @RequestBody ProjectDTO.CreateProjectRequest request) {
        log.info("REST request to create project: {}", request.getName());
        ProjectDTO.ProjectResponse response = projectService.createProject(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * View all projects
     * GET /projects
     */
    @GetMapping
    public ResponseEntity<List<ProjectDTO.ProjectResponse>> getAllProjects() {
        log.info("REST request to get all projects");
        List<ProjectDTO.ProjectResponse> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }

    /**
     * Get project details by ID
     * GET /projects/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProjectDTO.ProjectResponse> getProjectById(@PathVariable Long id) {
        log.info("REST request to get project with ID: {}", id);
        ProjectDTO.ProjectResponse project = projectService.getProjectById(id);
        return ResponseEntity.ok(project);
    }

    /**
     * Assign employees to a project
     * POST /projects/{id}/employees
     */
    @PostMapping("/{id}/employees")
    public ResponseEntity<ProjectDTO.ProjectResponse> assignEmployees(
            @PathVariable Long id,
            @Valid @RequestBody ProjectDTO.AssignEmployeesRequest request) {
        log.info("REST request to assign employees to project ID: {}", id);
        ProjectDTO.ProjectResponse response = projectService.assignEmployees(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Remove employees from a project
     * DELETE /projects/{id}/employees
     */
    @DeleteMapping("/{id}/employees")
    public ResponseEntity<ProjectDTO.ProjectResponse> removeEmployees(
            @PathVariable Long id,
            @Valid @RequestBody ProjectDTO.RemoveEmployeesRequest request) {

        log.info("REST request to remove employees {} from project ID: {}",
                request.getEmployeeIds(), id);

        ProjectDTO.ProjectResponse response =
                projectService.removeEmployeesFromProject(id, request);

        return ResponseEntity.ok(response);
    }







    /**
     * Update project status
     * PATCH /projects/{id}/status
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ProjectDTO.ProjectResponse> updateProjectStatus(
            @PathVariable Long id,
            @Valid @RequestBody ProjectDTO.UpdateStatusRequest request) {
        log.info("REST request to update project status for ID: {} to {}", id, request.getStatus());
        ProjectDTO.ProjectResponse response = projectService.updateProjectStatus(id, request);
        return ResponseEntity.ok(response);
    }
}