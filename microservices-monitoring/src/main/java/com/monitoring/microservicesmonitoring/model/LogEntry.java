package com.monitoring.microservicesmonitoring.model;

import com.monitoring.microservicesmonitoring.enums.LogStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogEntry {

    private String requestId;
    private String serviceId;
    private String operation;
    private Long durationMs;
    private LogStatus status;
    private LocalDateTime timestamp;
    private String inputParams;
    private String responseBody;
    private String errorMessage;
    private String stackTraceSummary;

    public LogEntry(
            String requestId,
            String serviceId,
            String operation,
            LogStatus status,
            long executionTimeMs,
            String message,
            Instant timestamp
    ) {
        this.requestId = requestId;
        this.serviceId = serviceId;
        this.operation = operation;
        this.durationMs = executionTimeMs;
        this.status = status;
        this.timestamp = timestamp == null
                ? LocalDateTime.now()
                : LocalDateTime.ofInstant(timestamp, ZoneId.systemDefault());
        this.responseBody = message;
        this.errorMessage = status == LogStatus.ERROR ? message : null;
    }

    public long getExecutionTimeMs() {
        return durationMs == null ? 0L : durationMs;
    }

    public void setExecutionTimeMs(long executionTimeMs) {
        this.durationMs = executionTimeMs;
    }

    public String getMessage() {
        return responseBody != null ? responseBody : errorMessage;
    }

    public void setMessage(String message) {
        this.responseBody = message;
    }
}
