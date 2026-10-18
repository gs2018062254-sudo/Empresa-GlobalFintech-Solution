package com.globalfintech.globalfinancialengine.domain.builder;

import com.globalfintech.globalfinancialengine.domain.factory.ReportComponentFactory;
import com.globalfintech.globalfinancialengine.domain.model.FinancialReport;
import com.globalfintech.globalfinancialengine.domain.prototype.FinancialReportTemplate;

public interface ReportBuilder {
    ReportBuilder reset();
    ReportBuilder setMetadata(String id, String title);
    ReportBuilder applyTemplate(FinancialReportTemplate template);
    ReportBuilder setComponents(ReportComponentFactory factory);
    ReportBuilder addSection(String section);
    ReportBuilder setId(String id);
    ReportBuilder setTitle(String title);
    ReportBuilder setHeader(String header);
    ReportBuilder setFooter(String footer);
    FinancialReport build();
}
