package com.monitoring.microservicesmonitoring.dto;

public record SimulationResultDTO(
        int totalExecutions,
        int successfulExecutions,
        int failedExecutions,
        long simulatedDurationMs,
        String message
) {
}
