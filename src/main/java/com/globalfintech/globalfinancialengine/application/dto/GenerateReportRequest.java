package com.globalfintech.globalfinancialengine.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import java.util.List;

public record GenerateReportRequest(
        @NotBlank
        @Pattern(regexp = "(?i)IFRS|USGAAP|EU_TAX")
        String standard,

        @NotBlank
        String title,

        @NotEmpty
        List<@NotBlank String> sections,

        String watermark
) {}
