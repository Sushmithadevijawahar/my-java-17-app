package com.mgmt_sym.department_service.circuit_brkr;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/circuit-breaker")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CB_Monitor {

    private final CircuitBreakerRegistry circuitBreakerRegistry;

    /**
     * Get status of all circuit breakers
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getCircuitBreakerStatus() {
        Map<String, Object> response = new HashMap<>();

        circuitBreakerRegistry.getAllCircuitBreakers().forEach(cb -> {
            Map<String, Object> cbInfo = new HashMap<>();
            cbInfo.put("state", cb.getState().toString());
            cbInfo.put("failureRate", cb.getMetrics().getFailureRate());
            cbInfo.put("slowCallRate", cb.getMetrics().getSlowCallRate());
            cbInfo.put("numberOfSuccessfulCalls", cb.getMetrics().getNumberOfSuccessfulCalls());
            cbInfo.put("numberOfFailedCalls", cb.getMetrics().getNumberOfFailedCalls());
            cbInfo.put("numberOfSlowCalls", cb.getMetrics().getNumberOfSlowCalls());
            cbInfo.put("numberOfNotPermittedCalls", cb.getMetrics().getNumberOfNotPermittedCalls());

            response.put(cb.getName(), cbInfo);
        });

        return ResponseEntity.ok(response);
    }

    /**
     * Get status of a specific circuit breaker
     */
    @GetMapping("/status/{name}")
    public ResponseEntity<Map<String, Object>> getCircuitBreakerStatusByName(@PathVariable String name) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(name);

        Map<String, Object> cbInfo = new HashMap<>();
        cbInfo.put("name", circuitBreaker.getName());
        cbInfo.put("state", circuitBreaker.getState().toString());
        cbInfo.put("failureRate", circuitBreaker.getMetrics().getFailureRate());
        cbInfo.put("slowCallRate", circuitBreaker.getMetrics().getSlowCallRate());
        cbInfo.put("numberOfSuccessfulCalls", circuitBreaker.getMetrics().getNumberOfSuccessfulCalls());
        cbInfo.put("numberOfFailedCalls", circuitBreaker.getMetrics().getNumberOfFailedCalls());
        cbInfo.put("numberOfSlowCalls", circuitBreaker.getMetrics().getNumberOfSlowCalls());
        cbInfo.put("numberOfNotPermittedCalls", circuitBreaker.getMetrics().getNumberOfNotPermittedCalls());
        cbInfo.put("numberOfBufferedCalls", circuitBreaker.getMetrics().getNumberOfBufferedCalls());

        return ResponseEntity.ok(cbInfo);
    }

    /**
     * Manually transition circuit breaker to open state
     */
    @PostMapping("/open/{name}")
    public ResponseEntity<String> openCircuitBreaker(@PathVariable String name) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(name);
        circuitBreaker.transitionToOpenState();
        return ResponseEntity.ok("Circuit breaker '" + name + "' transitioned to OPEN state");
    }

    /**
     * Manually transition circuit breaker to closed state
     */
    @PostMapping("/close/{name}")
    public ResponseEntity<String> closeCircuitBreaker(@PathVariable String name) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(name);
        circuitBreaker.transitionToClosedState();
        return ResponseEntity.ok("Circuit breaker '" + name + "' transitioned to CLOSED state");
    }

    /**
     * Manually transition circuit breaker to half-open state
     */
    @PostMapping("/half-open/{name}")
    public ResponseEntity<String> halfOpenCircuitBreaker(@PathVariable String name) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(name);
        circuitBreaker.transitionToHalfOpenState();
        return ResponseEntity.ok("Circuit breaker '" + name + "' transitioned to HALF_OPEN state");
    }

    /**
     * Reset circuit breaker metrics
     */
    @PostMapping("/reset/{name}")
    public ResponseEntity<String> resetCircuitBreaker(@PathVariable String name) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(name);
        circuitBreaker.reset();
        return ResponseEntity.ok("Circuit breaker '" + name + "' has been reset");
    }
}