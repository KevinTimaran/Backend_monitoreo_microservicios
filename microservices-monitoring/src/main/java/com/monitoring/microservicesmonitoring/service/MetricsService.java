package com.monitoring.microservicesmonitoring.service;

import com.monitoring.microservicesmonitoring.dto.LogEntryDTO;
import com.monitoring.microservicesmonitoring.dto.MetricsSummaryDTO;
import com.monitoring.microservicesmonitoring.dto.PageResponseDTO;
import com.monitoring.microservicesmonitoring.dto.ServiceMetricDTO;
import com.monitoring.microservicesmonitoring.enums.LogStatus;
import com.monitoring.microservicesmonitoring.model.LogEntry;
import com.monitoring.microservicesmonitoring.store.LogStore;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class MetricsService {

    private static final List<String> MONITORED_SERVICES = List.of("inventory", "orders", "payments");
    private final LogStore logStore;

    public MetricsService(LogStore logStore) {
        this.logStore = logStore;
    }

    public MetricsSummaryDTO getSummary() {
        List<LogEntry> allLogs = logStore.findAll();
        long totalCalls = allLogs.size();
        long totalErrors = allLogs.stream().filter(log -> log.getStatus() == LogStatus.ERROR).count();
        double globalErrorRate = totalCalls == 0 ? 0.0 : (totalErrors * 100.0) / totalCalls;

        List<ServiceMetricDTO> serviceMetrics = MONITORED_SERVICES.stream()
                .map(this::buildServiceMetric)
                .toList();

        return new MetricsSummaryDTO(totalCalls, globalErrorRate, serviceMetrics);
    }

    public PageResponseDTO<LogEntryDTO> getLogs(
            String service,
            String status,
            LocalDateTime from,
            LocalDateTime to,
            int page,
            int size
    ) {
        int safePage = Math.max(0, page);
        int safeSize = Math.max(1, size);

        List<LogEntry> filtered = logStore.filter(service, status, from, to).stream()
                .sorted(Comparator.comparing(LogEntry::getTimestamp, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .toList();

        int start = Math.min(safePage * safeSize, filtered.size());
        int end = Math.min(start + safeSize, filtered.size());
        List<LogEntryDTO> content = filtered.subList(start, end).stream()
                .map(this::toDto)
                .toList();

        int totalPages = (int) Math.ceil(filtered.size() / (double) safeSize);
        return new PageResponseDTO<>(content, safePage, safeSize, filtered.size(), totalPages);
    }

    private ServiceMetricDTO buildServiceMetric(String serviceId) {
        List<LogEntry> serviceLogs = logStore.filter(serviceId, null, null, null);
        long totalCalls = serviceLogs.size();
        long successCalls = serviceLogs.stream().filter(log -> log.getStatus() == LogStatus.SUCCESS).count();
        long errorCalls = serviceLogs.stream().filter(log -> log.getStatus() == LogStatus.ERROR).count();

        double successRate = totalCalls == 0 ? 0.0 : (successCalls * 100.0) / totalCalls;
        double errorRate = totalCalls == 0 ? 0.0 : (errorCalls * 100.0) / totalCalls;
        long averageDurationMs = totalCalls == 0
                ? 0L
                : Math.round(serviceLogs.stream().mapToLong(log -> log.getDurationMs() == null ? 0L : log.getDurationMs()).average().orElse(0.0));

        List<Long> last20Durations = logStore.findTop20ByService(serviceId).stream()
                .map(log -> log.getDurationMs() == null ? 0L : log.getDurationMs())
                .toList();

        return new ServiceMetricDTO(serviceId, totalCalls, successRate, errorRate, averageDurationMs, last20Durations);
    }

    private LogEntryDTO toDto(LogEntry entry) {
        return new LogEntryDTO(
                entry.getRequestId(),
                entry.getServiceId(),
                entry.getOperation(),
                entry.getDurationMs(),
                entry.getStatus(),
                entry.getTimestamp(),
                entry.getInputParams(),
                entry.getResponseBody(),
                entry.getErrorMessage(),
                entry.getStackTraceSummary()
        );
    }
}
