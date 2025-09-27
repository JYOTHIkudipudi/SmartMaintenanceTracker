package com.example.SmartMaintenanceTracker;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class DashboardController {

    private final DeviceService deviceService;

    public DashboardController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("devices", deviceService.getAllStatus());
        return "index"; // dashboard template
    }

    @GetMapping("/device/{id}")
    public String devicePage(@PathVariable("id") String id, Model model) {
        model.addAttribute("deviceId", id);
        return "device";
    }
}

