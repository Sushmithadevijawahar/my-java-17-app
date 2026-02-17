package com.mgmt_sym.project_service.client;

import com.mgmt_sym.project_service.dto.DepartmentDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "department-service",
        url = "${external-services.department-service.url}",
        fallback = DepartmentClientFallback.class
)
public interface DepartmentClient {

    @GetMapping("/api/departments/{id}")
    @CircuitBreaker(name = "departmentService", fallbackMethod = "getDepartmentFallback")
    DepartmentDTO getDepartmentById(@PathVariable("id") Long id);
}