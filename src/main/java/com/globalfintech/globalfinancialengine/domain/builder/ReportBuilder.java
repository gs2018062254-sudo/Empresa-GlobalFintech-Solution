package com.globalfintech.globalfinancialengine.domain.builder;

import com.globalfintech.globalfinancialengine.domain.model.FinancialReport;
import java.util.List;

public interface ReportBuilder {
    ReportBuilder setId(String id);
    ReportBuilder setTitle(String title);
    ReportBuilder setHeader(String header);
    ReportBuilder setFooter(String footer);
    ReportBuilder setSections(List<String> sections);
    ReportBuilder addSection(String section);
    FinancialReport build();
}
