package com.monitoring.microservicesmonitoring.dto;

public record ServiceMetricDTO(
        String serviceId,
        long totalCalls,
        double successRate,
        double errorRate,
        long averageDurationMs,
        java.util.List<Long> last20Durations
) {
}
