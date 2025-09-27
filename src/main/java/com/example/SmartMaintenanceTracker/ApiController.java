package com.example.SmartMaintenanceTracker;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ApiController {

    private final DeviceService deviceService;

    public ApiController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @GetMapping("/api/devices")
    public Object getDevices() {
        return deviceService.getAllStatus();
    }

    @GetMapping("/api/device/{id}")
    public Object getDevice(@PathVariable("id") String id) {
        return deviceService.getDeviceData(id);
    }

    @GetMapping("/export/{id}")
    public ResponseEntity<ByteArrayResource> exportDevice(@PathVariable("id") String id) {
        byte[] bytes = deviceService.exportDeviceCsv(id);
        ByteArrayResource resource = new ByteArrayResource(bytes);
        String filename = id + "_history.csv";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.TEXT_PLAIN)
                .contentLength(bytes.length)
                .body(resource);
    }

    @GetMapping("/export_csv")
    public ResponseEntity<ByteArrayResource> exportAll() {
        byte[] bytes = deviceService.exportAllCsv();
        ByteArrayResource resource = new ByteArrayResource(bytes);
        String filename = "all_devices_history.csv";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.TEXT_PLAIN)
                .contentLength(bytes.length)
                .body(resource);
    }
}
