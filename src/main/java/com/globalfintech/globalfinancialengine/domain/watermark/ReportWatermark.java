package com.globalfintech.globalfinancialengine.domain.watermark;

import com.globalfintech.globalfinancialengine.domain.model.FinancialReport;

public class ReportWatermark {

    public FinancialReport apply(FinancialReport report, String watermark) {
        if (report == null) {
            throw new IllegalArgumentException("El reporte no puede ser nulo");
        }

        String mark = watermark == null ? "CONFIDENCIAL" : watermark;

        String currentTitle = report.getTitle();
        report.setTitle("[" + mark + "] " +
                (currentTitle == null ? "" : currentTitle));

        return report;
    }
}
