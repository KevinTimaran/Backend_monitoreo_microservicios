package com.monitoring.microservicesmonitoring.dto;

import java.util.List;

public record MetricsSummaryDTO(
        long totalCalls,
        double globalErrorRate,
        List<ServiceMetricDTO> services
) {
}
