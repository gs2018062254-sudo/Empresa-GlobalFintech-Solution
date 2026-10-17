package com.globalfintech.globalfinancialengine.domain.watermark;

import com.globalfintech.globalfinancialengine.domain.model.FinancialReport;

public class ReportWatermark {

    public FinancialReport apply(FinancialReport report, String watermark) {
        if (report == null) {
            throw new IllegalArgumentException("El reporte no puede ser nulo");
        }

        report.setWatermark(
                watermark == null || watermark.isBlank()
                        ? "CONFIDENCIAL"
                        : watermark.trim());

        return report;
    }
}
