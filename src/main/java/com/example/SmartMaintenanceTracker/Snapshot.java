package com.example.SmartMaintenanceTracker;

public class Snapshot {
    private String timestamp; // ISO
    private double temperature;
    private double memory;
    private double voltage;
    private double cpu;
    private double io;
    private String status;

    public Snapshot() {}

    public Snapshot(String timestamp, double temperature, double memory, double voltage, double cpu, double io, String status) {
        this.timestamp = timestamp;
        this.temperature = temperature;
        this.memory = memory;
        this.voltage = voltage;
        this.cpu = cpu;
        this.io = io;
        this.status = status;
    }

    // getters & setters
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }

    public double getMemory() { return memory; }
    public void setMemory(double memory) { this.memory = memory; }

    public double getVoltage() { return voltage; }
    public void setVoltage(double voltage) { this.voltage = voltage; }

    public double getCpu() { return cpu; }
    public void setCpu(double cpu) { this.cpu = cpu; }

    public double getIo() { return io; }
    public void setIo(double io) { this.io = io; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
