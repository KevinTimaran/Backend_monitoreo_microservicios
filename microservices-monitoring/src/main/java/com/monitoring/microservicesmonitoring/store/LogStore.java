package com.monitoring.microservicesmonitoring.store;

import com.monitoring.microservicesmonitoring.model.LogEntry;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class LogStore {

    private final List<LogEntry> logs = new CopyOnWriteArrayList<>();

    public void save(LogEntry logEntry) {
        if (logEntry == null) {
            throw new IllegalArgumentException("Log entry is required");
        }
        logs.add(logEntry);
    }

    // Backward-compatible alias while services are migrated.
    public void add(LogEntry logEntry) {
        save(logEntry);
    }

    public List<LogEntry> findAll() {
        return new ArrayList<>(logs);
    }

    public List<LogEntry> filter(String service, String status, LocalDateTime from, LocalDateTime to) {
        return logs.stream()
                .filter(entry -> service == null || service.isBlank() || service.equalsIgnoreCase(entry.getServiceId()))
                .filter(entry -> status == null || status.isBlank() || status.equalsIgnoreCase(entry.getStatus().name()))
                .filter(entry -> from == null || (entry.getTimestamp() != null && !entry.getTimestamp().isBefore(from)))
                .filter(entry -> to == null || (entry.getTimestamp() != null && !entry.getTimestamp().isAfter(to)))
                .toList();
    }

    public List<LogEntry> findTop20ByService(String serviceId) {
        if (serviceId == null || serviceId.isBlank()) {
            return List.of();
        }

        return logs.stream()
                .filter(entry -> serviceId.equalsIgnoreCase(entry.getServiceId()))
                .sorted(Comparator.comparing(LogEntry::getTimestamp, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .limit(20)
                .toList();
    }
}
