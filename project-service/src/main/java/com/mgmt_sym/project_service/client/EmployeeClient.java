package com.mgmt_sym.project_service.client;

import com.mgmt_sym.project_service.client.EmployeeClientFallback;
import com.mgmt_sym.project_service.dto.EmployeeDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(
        name = "employee-service",
        url = "${external-services.employee-service.url}",
        fallback = EmployeeClientFallback.class
)
public interface EmployeeClient {

    @GetMapping("/api/employees")
    @CircuitBreaker(name = "employeeService", fallbackMethod = "getAllEmployeesFallback")
    List<EmployeeDTO> getAllEmployees();

    @GetMapping("/api/employees/{id}")
    @CircuitBreaker(name = "employeeService", fallbackMethod = "getEmployeeByIdFallback")
    EmployeeDTO getEmployeeById(@PathVariable("id") Long id);

    @GetMapping("/api/employees/by-ids")
    @CircuitBreaker(name = "employeeService", fallbackMethod = "getEmployeesByIdsFallback")
    List<EmployeeDTO> getEmployeesByIds(@RequestParam("ids") List<Long> ids);

    @PostMapping("/api/employees/validate")
    void validateEmployees(@RequestBody List<Long> employeeIds);
}