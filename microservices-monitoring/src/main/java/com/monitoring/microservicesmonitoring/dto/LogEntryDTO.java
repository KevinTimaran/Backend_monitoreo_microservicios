package com.monitoring.microservicesmonitoring.dto;

import com.monitoring.microservicesmonitoring.enums.LogStatus;

import java.time.LocalDateTime;

public record LogEntryDTO(
        String requestId,
        String serviceId,
        String operation,
        Long durationMs,
        LogStatus status,
        LocalDateTime timestamp,
        String inputParams,
        String responseBody,
        String errorMessage,
        String stackTraceSummary
) {
}
