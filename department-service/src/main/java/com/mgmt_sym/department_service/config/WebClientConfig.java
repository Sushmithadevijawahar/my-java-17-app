package com.mgmt_sym.department_service.config;

import com.mgmt_sym.department_service.client.EmployeeClient;
import jakarta.ws.rs.core.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.reactive.LoadBalancedExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class WebClientConfig {
    @Autowired
private LoadBalancedExchangeFilterFunction loadBalancedExchangeFilterFunction;

@Bean
    public WebClient employeeWebClient(){
        return WebClient.builder()
                .baseUrl("http://employee-service")
                .filter(loadBalancedExchangeFilterFunction)
                .build();
    }

    @Bean
    public EmployeeClient employeeClient(){

        return HttpServiceProxyFactory.builderFor(
                WebClientAdapter.create(employeeWebClient())
        ).build().createClient(EmployeeClient.class);
    }
}
