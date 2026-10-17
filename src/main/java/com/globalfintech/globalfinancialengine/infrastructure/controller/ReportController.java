package com.globalfintech.globalfinancialengine.infrastructure.controller;

import com.globalfintech.globalfinancialengine.application.GenerateReportUseCase;
import com.globalfintech.globalfinancialengine.application.dto.GenerateReportRequest;
import com.globalfintech.globalfinancialengine.domain.model.FinancialReport;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final GenerateReportUseCase useCase;

    public ReportController(GenerateReportUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FinancialReport generate(
            @Valid @RequestBody GenerateReportRequest request) {

        return useCase.execute(
                request.standard(),
                request.title(),
                request.sections(),
                request.watermark());
    }
}
