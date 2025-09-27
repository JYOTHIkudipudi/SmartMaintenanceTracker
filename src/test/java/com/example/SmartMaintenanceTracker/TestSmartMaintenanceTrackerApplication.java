package com.example.SmartMaintenanceTracker;

import org.springframework.boot.SpringApplication;

public class TestSmartMaintenanceTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.from(SmartMaintenanceTrackerApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
