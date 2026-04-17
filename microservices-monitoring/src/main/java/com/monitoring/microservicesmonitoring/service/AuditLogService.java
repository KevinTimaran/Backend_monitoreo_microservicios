package com.monitoring.microservicesmonitoring.service;

import com.monitoring.microservicesmonitoring.enums.LogStatus;
import com.monitoring.microservicesmonitoring.model.LogEntry;
import com.monitoring.microservicesmonitoring.store.LogStore;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
public class AuditLogService {

    private final LogStore logStore;

    public AuditLogService(LogStore logStore) {
        this.logStore = logStore;
    }

    public void saveSuccessLog(
            String serviceId,
            String operation,
            String requestId,
            Long durationMs,
            LocalDateTime timestamp,
            String inputParams,
            String responseBody
    ) {
        LogEntry entry = LogEntry.builder()
                .serviceId(serviceId)
                .operation(operation)
                .requestId(requestId)
                .durationMs(durationMs)
                .timestamp(timestamp)
                .inputParams(inputParams)
                .responseBody(responseBody)
                .status(LogStatus.SUCCESS)
                .build();

        logStore.save(entry);
    }

    public void saveErrorLog(
            String serviceId,
            String operation,
            String requestId,
            Long durationMs,
            LocalDateTime timestamp,
            String inputParams,
            String errorMessage,
            String stackTraceSummary
    ) {
        LogEntry entry = LogEntry.builder()
                .serviceId(serviceId)
                .operation(operation)
                .requestId(requestId)
                .durationMs(durationMs)
                .timestamp(timestamp)
                .inputParams(inputParams)
                .errorMessage(errorMessage)
                .stackTraceSummary(stackTraceSummary)
                .status(LogStatus.ERROR)
                .build();

        logStore.save(entry);
    }

    public String summarizeStackTrace(Exception ex) {
        if (ex == null || ex.getStackTrace() == null) {
            return "No stack trace available";
        }

        return Arrays.stream(ex.getStackTrace())
                .limit(3)
                .map(frame -> frame.getClassName() + "." + frame.getMethodName() + ":" + frame.getLineNumber())
                .collect(Collectors.joining(" | "));
    }
}
