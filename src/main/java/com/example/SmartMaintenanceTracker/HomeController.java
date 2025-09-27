package com.example.SmartMaintenanceTracker;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "home";  // looks for home.html inside templates/
    }

    @GetMapping("/livechart")
    public String liveChart() {
        return "livechart"; // livechart.html
    }

    @GetMapping("/devices")
    public String devices() {
        return "devices"; // devices.html
    }

    @GetMapping("/alerts")
    public String alerts() {
        return "alerts"; // alerts.html
    }

    @GetMapping("/export")
    public String exportReports() {
        return "export"; // export.html
    }
}
