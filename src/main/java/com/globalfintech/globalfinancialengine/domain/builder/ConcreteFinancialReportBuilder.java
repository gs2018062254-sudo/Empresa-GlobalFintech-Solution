package com.globalfintech.globalfinancialengine.domain.builder;

import com.globalfintech.globalfinancialengine.domain.model.FinancialReport;
import java.util.ArrayList;
import java.util.List;

public class ConcreteFinancialReportBuilder implements ReportBuilder {

    private String id;
    private String title;
    private String header;
    private String footer;
    private List<String> sections = new ArrayList<>();

    @Override
    public ReportBuilder setId(String id) {
        this.id = id;
        return this;
    }

    @Override
    public ReportBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    @Override
    public ReportBuilder setHeader(String header) {
        this.header = header;
        return this;
    }

    @Override
    public ReportBuilder setFooter(String footer) {
        this.footer = footer;
        return this;
    }

    @Override
    public ReportBuilder setSections(List<String> sections) {
        this.sections = sections == null
                ? new ArrayList<>()
                : new ArrayList<>(sections);
        return this;
    }

    @Override
    public ReportBuilder addSection(String section) {
        this.sections.add(section);
        return this;
    }

    @Override
    public FinancialReport build() {
        FinancialReport report = new FinancialReport();
        report.setId(id);
        report.setTitle(title);
        report.setHeaderContent(header);
        report.setFooterContent(footer);
        report.setSections(sections);
        return report;
    }
}
