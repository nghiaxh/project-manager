package com.pm.project_manager.controller;

import com.pm.project_manager.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {
    private final StatisticsService statisticsService;

    @GetMapping("/projects/{id}")
    public Map<String, Long> getProjectStatistics(@PathVariable Long id) {
        return statisticsService.getProjectStatistics(id);
    }
}