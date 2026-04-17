package com.monitoring.microservicesmonitoring.proxy;

import com.monitoring.microservicesmonitoring.service.AuditLogService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

public class LoggingProxy<T> implements MicroserviceProxy<T> {

    private final String serviceId;
    private final ExecutableService<T> target;
    private final AuditLogService auditLogService;

    public LoggingProxy(String serviceId, ExecutableService<T> target, AuditLogService auditLogService) {
        this.serviceId = serviceId;
        this.target = target;
        this.auditLogService = auditLogService;
    }

    @Override
    public T execute(String operation, Object... params) {
        String requestId = UUID.randomUUID().toString();
        LocalDateTime startedAt = LocalDateTime.now();
        String readableParams = Arrays.deepToString(params == null ? new Object[0] : params);

        try {
            T result = target.execute(operation, params);
            long durationMs = Duration.between(startedAt, LocalDateTime.now()).toMillis();
            auditLogService.saveSuccessLog(
                    serviceId,
                    operation,
                    requestId,
                    durationMs,
                    startedAt,
                    readableParams,
                    String.valueOf(result)
            );
            return result;
        } catch (RuntimeException ex) {
            long durationMs = Duration.between(startedAt, LocalDateTime.now()).toMillis();
            auditLogService.saveErrorLog(
                    serviceId,
                    operation,
                    requestId,
                    durationMs,
                    startedAt,
                    readableParams,
                    ex.getMessage(),
                    auditLogService.summarizeStackTrace(ex)
            );
            throw ex;
        }
    }
}
