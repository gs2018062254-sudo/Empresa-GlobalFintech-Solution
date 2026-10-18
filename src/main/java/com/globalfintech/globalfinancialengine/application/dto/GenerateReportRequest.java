package com.globalfintech.globalfinancialengine.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.List;

public record GenerateReportRequest(
        String reportId,

        @NotBlank
        @Pattern(regexp = "(?i)IFRS|USGAAP|EU_TAX")
        String standard,

        @NotBlank
        String title,

        @NotBlank
        String templateKey,

        List<@NotBlank String> sections,

        String watermark
) {}
