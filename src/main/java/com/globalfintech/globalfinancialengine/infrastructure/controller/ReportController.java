package com.globalfintech.globalfinancialengine.infrastructure.controller;

import com.globalfintech.globalfinancialengine.application.GenerateReportUseCase;
import com.globalfintech.globalfinancialengine.application.ReportExportService;
import com.globalfintech.globalfinancialengine.application.audit.AuditService;
import com.globalfintech.globalfinancialengine.application.security.DigitalSignatureService;
import com.globalfintech.globalfinancialengine.application.dto.GenerateReportRequest;
import com.globalfintech.globalfinancialengine.domain.model.FinancialReport;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private final GenerateReportUseCase useCase;
    private final ReportExportService exportService;
    private final DigitalSignatureService signatureService;
    private final AuditService auditService;

    public ReportController(
            GenerateReportUseCase useCase,
            ReportExportService exportService,
            DigitalSignatureService signatureService,
            AuditService auditService) {
        this.useCase = useCase;
        this.exportService = exportService;
        this.signatureService = signatureService;
        this.auditService = auditService;
    }

    @PostMapping
    public ResponseEntity<FinancialReport> generate(
            @Valid @RequestBody GenerateReportRequest request) {
        FinancialReport report = create(request);
        auditService.record("GENERATE_JSON", report.getId(), "SUCCESS");
        return ResponseEntity.status(201).body(report);
    }

    @PostMapping(value = "/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> pdf(
            @Valid @RequestBody GenerateReportRequest request) throws IOException {
        FinancialReport report = create(request);
        byte[] file = exportService.exportPdf(report);
        String signature = signatureService.sign(reportContent(report));

        auditService.record("EXPORT_PDF", report.getId(), "SUCCESS");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"informe.pdf\"")
                .header("X-Document-Signature", signature)
                .contentType(MediaType.APPLICATION_PDF)
                .body(file);
    }

    @PostMapping(value = "/excel",
            produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> excel(
            @Valid @RequestBody GenerateReportRequest request) throws IOException {
        FinancialReport report = create(request);
        byte[] file = exportService.exportExcel(report);
        String signature = signatureService.sign(reportContent(report));

        auditService.record("EXPORT_EXCEL", report.getId(), "SUCCESS");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"informe.xlsx\"")
                .header("X-Document-Signature", signature)
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(file);
    }

    @PostMapping("/batch")
    public CompletableFuture<List<FinancialReport>> batch(
            @RequestBody List<@Valid GenerateReportRequest> requests) {
        auditService.record("BATCH", null, "STARTED");
        return CompletableFuture.supplyAsync(() -> {
            List<FinancialReport> reports =
                    requests.stream().map(this::create).toList();
            auditService.record("BATCH", null, "SUCCESS");
            return reports;
        });
    }

    private FinancialReport create(GenerateReportRequest request) {
        return useCase.execute(
                request.standard(),
                request.title(),
                request.sections(),
                request.watermark());
    }

    private String reportContent(FinancialReport report) {
        return String.join("|",
                safe(report.getId()),
                safe(report.getTitle()),
                safe(report.getHeaderContent()),
                safe(report.getFooterContent()),
                safe(report.getWatermark()),
                String.join("|", report.getSections()));
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
