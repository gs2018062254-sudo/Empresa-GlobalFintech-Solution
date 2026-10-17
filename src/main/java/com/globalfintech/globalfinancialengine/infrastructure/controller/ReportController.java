package com.globalfintech.globalfinancialengine.infrastructure.controller;

import com.globalfintech.globalfinancialengine.application.GenerateReportUseCase;
import com.globalfintech.globalfinancialengine.domain.model.FinancialReport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final GenerateReportUseCase generateReportUseCase;

    public ReportController(GenerateReportUseCase generateReportUseCase) {
        this.generateReportUseCase = generateReportUseCase;
    }

    @PostMapping
    public ResponseEntity<FinancialReport> generate(
            @RequestBody Map<String, Object> request) {

        String standard = (String) request.get("standard");
        String title = (String) request.get("title");
        String watermark = (String) request.getOrDefault(
                "watermark", "CONFIDENCIAL");

        Object sectionsObject = request.get("sections");
        List<String> sections = sectionsObject instanceof List<?> list
                ? list.stream().map(String::valueOf).toList()
                : List.of();

        FinancialReport report = generateReportUseCase.execute(
                standard, title, sections, watermark);

        return ResponseEntity.status(HttpStatus.CREATED).body(report);
    }
}
