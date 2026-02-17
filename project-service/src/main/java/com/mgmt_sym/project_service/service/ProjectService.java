package com.mgmt_sym.project_service.service;

import com.mgmt_sym.project_service.client.DepartmentClient;
import com.mgmt_sym.project_service.client.EmployeeClient;
import com.mgmt_sym.project_service.dto.DepartmentDTO;
import com.mgmt_sym.project_service.dto.EmployeeDTO;
import com.mgmt_sym.project_service.dto.ProjectDTO;
import com.mgmt_sym.project_service.exception.ResourceNotFoundException;
import com.mgmt_sym.project_service.model.Project;
import com.mgmt_sym.project_service.model.Project.ProjectStatus;
import com.mgmt_sym.project_service.repository.ProjectRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final DepartmentClient departmentClient;
    private final EmployeeClient employeeClient;

    @Transactional
    public ProjectDTO.ProjectResponse createProject(ProjectDTO.CreateProjectRequest request) {
        log.info("Creating project: {}", request.getName());

        // Validate department exists
        DepartmentDTO department = getDepartmentWithCircuitBreaker(request.getDepartmentId());

        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setDepartmentId(request.getDepartmentId());
        project.setStatus(ProjectStatus.PLANNING);


        Project savedProject = projectRepository.save(project);
        log.info("Project created successfully with ID: {}", savedProject.getId());

        return mapToResponse(savedProject, department, new ArrayList<>());
    }

    @Transactional(readOnly = true)
    public List<ProjectDTO.ProjectResponse> getAllProjects() {
        log.info("Fetching all projects");
        List<Project> projects = projectRepository.findAll();

        return projects.stream()
                .map(this::enrichProjectWithExternalData)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProjectDTO.ProjectResponse getProjectById(Long id) {
        log.info("Fetching project with ID: {}", id);
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + id));

        return enrichProjectWithExternalData(project);
    }

    @Transactional
    public ProjectDTO.ProjectResponse assignEmployees(Long projectId, ProjectDTO.AssignEmployeesRequest request) {
        log.info("Assigning employees to project ID: {}", projectId);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));

        // Validate employees exist
        List<EmployeeDTO> employees = getEmployeesWithCircuitBreaker(request.getEmployeeIds());

        // Add new employee IDs (avoid duplicates)
        for (Long employeeId : request.getEmployeeIds()) {
            if (!project.getEmployeeIds().contains(employeeId)) {
                project.getEmployeeIds().add(employeeId);
            }
        }

        Project updatedProject = projectRepository.save(project);
        log.info("Employees assigned successfully to project ID: {}", projectId);

        DepartmentDTO department = getDepartmentWithCircuitBreaker(project.getDepartmentId());
        return mapToResponse(updatedProject, department, employees);
    }

    @Transactional
    public ProjectDTO.ProjectResponse updateProjectStatus(Long projectId, ProjectDTO.UpdateStatusRequest request) {
        log.info("Updating project status for ID: {} to {}", projectId, request.getStatus());

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));

        project.setStatus(request.getStatus());
        Project updatedProject = projectRepository.save(project);

        log.info("Project status updated successfully for ID: {}", projectId);
        return enrichProjectWithExternalData(updatedProject);
    }

    @CircuitBreaker(name = "departmentService", fallbackMethod = "getDepartmentFallback")
    @Retry(name = "departmentService")
    private DepartmentDTO getDepartmentWithCircuitBreaker(Long departmentId) {
        log.debug("Calling Department Service for ID: {}", departmentId);
        return departmentClient.getDepartmentById(departmentId);
    }

    @CircuitBreaker(name = "employeeService", fallbackMethod = "getEmployeesFallback")
    @Retry(name = "employeeService")
    private List<EmployeeDTO> getEmployeesWithCircuitBreaker(List<Long> employeeIds) {
        log.debug("Calling Employee Service for IDs: {}", employeeIds);
        if (employeeIds == null || employeeIds.isEmpty()) {
            return new ArrayList<>();
        }
        return employeeClient.getEmployeesByIds(employeeIds);
    }

    private ProjectDTO.ProjectResponse enrichProjectWithExternalData(Project project) {
        DepartmentDTO department = getDepartmentWithCircuitBreaker(project.getDepartmentId());
        List<EmployeeDTO> employees = getEmployeesWithCircuitBreaker(project.getEmployeeIds());
        return mapToResponse(project, department, employees);
    }

    private ProjectDTO.ProjectResponse mapToResponse(Project project, DepartmentDTO department, List<EmployeeDTO> employees) {
        ProjectDTO.ProjectResponse response = new ProjectDTO.ProjectResponse();
        response.setId(project.getId());
        response.setName(project.getName());
        response.setDescription(project.getDescription());
        response.setDepartmentId(project.getDepartmentId());
        response.setDepartment(department);
        response.setStatus(project.getStatus());
        response.setStartDate(project.getStartDate());
        response.setEndDate(project.getEndDate());
        response.setEmployeeIds(project.getEmployeeIds());
        response.setEmployees(employees);
        response.setCreatedAt(project.getCreatedAt());
        response.setUpdatedAt(project.getUpdatedAt());
        return response;
    }

    // Fallback methods
    private DepartmentDTO getDepartmentFallback(Long departmentId, Exception ex) {
        log.error("Fallback triggered for getDepartmentWithCircuitBreaker. Department ID: {}, Error: {}",
                departmentId, ex.getMessage());
        DepartmentDTO fallback = new DepartmentDTO();
        fallback.setId(departmentId);
        fallback.setName("Department Service Unavailable");
        fallback.setCode("N/A");
        return fallback;
    }

    private List<EmployeeDTO> getEmployeesFallback(List<Long> employeeIds, Exception ex) {
        log.error("Fallback triggered for getEmployeesWithCircuitBreaker. Employee IDs: {}, Error: {}",
                employeeIds, ex.getMessage());
        return new ArrayList<>();
    }



    @CircuitBreaker(name = "employeeService", fallbackMethod = "removeEmployeesFallback")
    public ProjectDTO.ProjectResponse removeEmployeesFromProject(
            Long projectId,
            ProjectDTO.RemoveEmployeesRequest request) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // Validate employees exist (remote call)
        //employeeClient.validateEmployees(request.getEmployeeIds());

        // Remove employees
        project.getEmployeeIds().removeAll(request.getEmployeeIds());

        Project savedProject = projectRepository.save(project);

        log.info("Employees {} removed from project {}",
                request.getEmployeeIds(), projectId);

        return ProjectMapper.toResponse(savedProject);
    }

    /**
     * Circuit breaker fallback
     */
    public ProjectDTO.ProjectResponse removeEmployeesFallback(
            Long projectId,
            ProjectDTO.RemoveEmployeesRequest request,
            Throwable ex) {

        log.error("Employee service unavailable. Cannot remove employees. Reason: {}",
                ex.getMessage());

        throw new RuntimeException(
                "Employee service unavailable. Try again later."
        );
    }
}