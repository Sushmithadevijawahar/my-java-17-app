package com.mgmt_sym.gtwy_api_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;

@Configuration
@SpringBootApplication
@EnableDiscoveryClient
public class GtwyApiServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GtwyApiServiceApplication.class, args);
	}

}
