package com.example.SmartMaintenanceTracker;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;

/**
 * In-memory device manager + simulator
 */
public class DeviceService {

    private final ConcurrentMap<String, Device> devices = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Deque<Snapshot>> history = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Snapshot> current = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final Random rnd = new Random();
    private final int HISTORY_MAX = 240;

    public DeviceService() {
        initDefaultDevices();
    }

    public void startSimulator() {
        scheduler.scheduleAtFixedRate(this::tick, 0, 2, TimeUnit.SECONDS);
    }

    public void stopSimulator() {
        scheduler.shutdownNow();
    }

    private void initDefaultDevices() {
        // create a few default devices (like your python list)
        addDevice(new Device("edge-server-1", "Edge Server 1", "fa-server", "server"));
        addDevice(new Device("iot-node-3", "IoT Sensor Node 3", "fa-microchip", "iot"));
        addDevice(new Device("factory-robot-2", "Factory Robot 2", "fa-robot", "robot"));
        addDevice(new Device("power-module-1", "Power Module", "fa-bolt", "power"));
        addDevice(new Device("workstation-7", "Workstation 7", "fa-desktop", "workstation"));
        addDevice(new Device("router-5", "Router 5", "fa-wifi", "network"));
        addDevice(new Device("gateway-1", "Gateway Alpha", "fa-globe", "gateway"));
        addDevice(new Device("cnc-4", "CNC Machine 4", "fa-cog", "cnc"));
        addDevice(new Device("traffic-ctrl", "Traffic Controller", "fa-traffic-light", "traffic"));
        addDevice(new Device("plant-hub", "Smart Plant Hub", "fa-industry", "plant"));
    }

    public void addDevice(Device d) {
        if (d == null || d.getId() == null) return;
        devices.putIfAbsent(d.getId(), d);
        history.putIfAbsent(d.getId(), new ArrayDeque<>(HISTORY_MAX));
        Snapshot init = initialSnapshot();
        current.putIfAbsent(d.getId(), init);
        history.get(d.getId()).add(init);
    }

    public void addDeviceByIdName(String id, String name) {
        // simple icon selection
        String icon = "fa-cloud";
        addDevice(new Device(id, name, icon, "custom"));
    }

    private Snapshot initialSnapshot() {
        double t = 30 + rnd.nextDouble() * 25;
        double m = 20 + rnd.nextDouble() * 60;
        double v = 3.2 + rnd.nextDouble() * 1.6;
        double cpu = 10 + rnd.nextDouble() * 50;
        double io = 1 + rnd.nextDouble() * 20;
        String status = statusFrom(t, m, v, cpu);

        return new Snapshot(Instant.now().toString(), round(t), round(m), round(v), round(cpu), round(io), status);
    }

    private double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    private String statusFrom(double t, double m, double v, double cpu) {
        if (t > 85 || m > 92 || v < 2.8 || cpu > 95) return "ALERT";
        if (t > 75 || m > 85 || v < 3.1 || cpu > 85) return "WARN";
        return "OK";
    }

    private Snapshot simulateNext(String deviceId) {
        Snapshot prev = current.get(deviceId);
        if (prev == null) prev = initialSnapshot();

        double t = prev.getTemperature() + (-1.8 + rnd.nextDouble() * 4.6);
        double m = prev.getMemory() + (-4 + rnd.nextDouble() * 9);
        double v = prev.getVoltage() + (-0.06 + rnd.nextDouble() * 0.12);
        double cpu = prev.getCpu() + (-4 + rnd.nextDouble() * 9);
        double io = prev.getIo() + (-1 + rnd.nextDouble() * 2.5);

        // random spikes
        if (rnd.nextDouble() < 0.06) t += 8 + rnd.nextDouble() * 10;
        if (rnd.nextDouble() < 0.05) v -= 0.15 + rnd.nextDouble() * 0.45;
        if (rnd.nextDouble() < 0.05) m += 8 + rnd.nextDouble() * 25;
        if (rnd.nextDouble() < 0.05) cpu += 10 + rnd.nextDouble() * 30;

        t = clamp(t, 10, 120);
        m = clamp(m, 0, 100);
        v = clamp(v, 1.5, 6.0);
        cpu = clamp(cpu, 0, 100);
        io = clamp(io, 0, 100);

        String status = statusFrom(t, m, v, cpu);
        return new Snapshot(Instant.now().toString(), round(t), round(m), round(v), round(cpu), round(io), status);
    }

    private double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

    private void tick() {
        try {
            for (String id : devices.keySet()) {
                Snapshot snap = simulateNext(id);
                current.put(id, snap);
                Deque<Snapshot> dq = history.get(id);
                if (dq == null) {
                    dq = new ArrayDeque<>(HISTORY_MAX);
                    history.put(id, dq);
                }
                synchronized (dq) {
                    if (dq.size() >= HISTORY_MAX) dq.removeFirst();
                    dq.addLast(snap);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // API-friendly responses

    public List<Map<String, Object>> getAllStatus() {
        List<Map<String,Object>> out = new ArrayList<>();
        for (String id : devices.keySet()) {
            Device d = devices.get(id);
            Snapshot cur = current.get(id);
            Map<String,Object> m = new HashMap<>();
            m.put("id", d.getId());
            m.put("name", d.getName());
            m.put("icon", d.getIcon());
            if (cur != null) {
                m.put("temperature", cur.getTemperature());
                m.put("memory", cur.getMemory());
                m.put("voltage", cur.getVoltage());
                m.put("cpu", cur.getCpu());
                m.put("io", cur.getIo());
                m.put("status", cur.getStatus());
                m.put("timestamp", cur.getTimestamp());
            } else {
                m.put("temperature", 0);
                m.put("memory", 0);
                m.put("voltage", 0);
                m.put("cpu", 0);
                m.put("io", 0);
                m.put("status", "UNKNOWN");
                m.put("timestamp", Instant.now().toString());
            }
            out.add(m);
        }
        return out;
    }

    public Map<String,Object> getDeviceData(String deviceId) {
        Device d = devices.get(deviceId);
        if (d == null) {
            Map<String,Object> e = new HashMap<>();
            e.put("error", "not found");
            return e;
        }
        Map<String,Object> res = new HashMap<>();
        res.put("meta", d);
        res.put("current", current.get(deviceId));
        Deque<Snapshot> dq = history.get(deviceId);
        List<Snapshot> hist = new ArrayList<>();
        if (dq != null) {
            synchronized (dq) {
                hist.addAll(dq);
            }
        }
        res.put("history", hist);
        return res;
    }

    // export CSV bytes for device
    public byte[] exportDeviceCsv(String deviceId) {
        Deque<Snapshot> dq = history.get(deviceId);
        StringBuilder sb = new StringBuilder();
        sb.append("timestamp,temperature,memory,voltage,cpu,io,status\n");
        if (dq != null) {
            synchronized (dq) {
                for (Snapshot s : dq) {
                    sb.append(String.format("%s,%.2f,%.2f,%.2f,%.2f,%.2f,%s\n",
                            s.getTimestamp(),
                            s.getTemperature(),
                            s.getMemory(),
                            s.getVoltage(),
                            s.getCpu(),
                            s.getIo(),
                            s.getStatus()));
                }
            }
        }
        return sb.toString().getBytes();
    }

    public byte[] exportAllCsv() {
        StringBuilder sb = new StringBuilder();
        sb.append("timestamp,device_id,temperature,memory,voltage,cpu,io,status\n");
        for (String id : devices.keySet()) {
            Deque<Snapshot> dq = history.get(id);
            if (dq != null) {
                synchronized (dq) {
                    for (Snapshot s : dq) {
                        sb.append(String.format("%s,%s,%.2f,%.2f,%.2f,%.2f,%.2f,%s\n",
                                s.getTimestamp(),
                                id,
                                s.getTemperature(),
                                s.getMemory(),
                                s.getVoltage(),
                                s.getCpu(),
                                s.getIo(),
                                s.getStatus()));
                    }
                }
            }
        }
        return sb.toString().getBytes();
    }
}
