package com.example.SmartMaintenanceTracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SmartMaintenanceTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartMaintenanceTrackerApplication.class, args);
    }

    // create and start the simulator service on startup
    @Bean
    public DeviceService deviceService() {
        DeviceService s = new DeviceService();
        s.startSimulator();
        return s;
    }
}

