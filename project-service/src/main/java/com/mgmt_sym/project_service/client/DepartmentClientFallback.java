package com.mgmt_sym.project_service.client;

import com.mgmt_sym.project_service.dto.DepartmentDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DepartmentClientFallback implements DepartmentClient {

    @Override
    public DepartmentDTO getDepartmentById(Long id) {
        log.warn("Fallback triggered for getDepartmentById with id: {}", id);
        DepartmentDTO fallbackDept = new DepartmentDTO();
        fallbackDept.setId(id);
        fallbackDept.setName("Department Service Unavailable");
        fallbackDept.setCode("N/A");
        fallbackDept.setDescription("Service temporarily unavailable");
        return fallbackDept;
    }
}