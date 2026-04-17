package com.monitoring.microservicesmonitoring.controller;

import com.monitoring.microservicesmonitoring.dto.LogEntryDTO;
import com.monitoring.microservicesmonitoring.dto.MetricsSummaryDTO;
import com.monitoring.microservicesmonitoring.dto.PageResponseDTO;
import com.monitoring.microservicesmonitoring.dto.SimulationResultDTO;
import com.monitoring.microservicesmonitoring.service.LoadSimulationService;
import com.monitoring.microservicesmonitoring.service.MetricsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/metrics")
public class MetricsController {

    private final MetricsService metricsService;
    private final LoadSimulationService loadSimulationService;

    public MetricsController(MetricsService metricsService, LoadSimulationService loadSimulationService) {
        this.metricsService = metricsService;
        this.loadSimulationService = loadSimulationService;
    }

    @GetMapping("/summary")
    public ResponseEntity<MetricsSummaryDTO> getSummary() {
        return ResponseEntity.ok(metricsService.getSummary());
    }

    @GetMapping("/logs")
    public ResponseEntity<PageResponseDTO<LogEntryDTO>> getLogs(
            @RequestParam(required = false) String service,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(metricsService.getLogs(service, status, from, to, page, size));
    }

    @PostMapping("/simulate-load")
    public ResponseEntity<SimulationResultDTO> simulateLoad() {
        return ResponseEntity.ok(loadSimulationService.simulateLoad());
    }
}
