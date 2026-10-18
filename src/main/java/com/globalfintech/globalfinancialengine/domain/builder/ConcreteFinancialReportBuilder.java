package com.globalfintech.globalfinancialengine.domain.builder;

import com.globalfintech.globalfinancialengine.domain.factory.ReportComponentFactory;
import com.globalfintech.globalfinancialengine.domain.model.FinancialReport;
import com.globalfintech.globalfinancialengine.domain.prototype.FinancialReportTemplate;

public class ConcreteFinancialReportBuilder implements ReportBuilder {

    private FinancialReport report;

    public ConcreteFinancialReportBuilder() {
        reset();
    }

    @Override
    public ReportBuilder reset() {
        this.report = new FinancialReport();
        return this;
    }

    @Override
    public ReportBuilder setMetadata(String id, String title) {
        this.report.setId(id);
        this.report.setTitle(title);
        return this;
    }

    @Override
    public ReportBuilder applyTemplate(FinancialReportTemplate template) {
        if (template != null) {
            this.report.setSections(template.getDefaultSections());
        }
        return this;
    }

    @Override
    public ReportBuilder setComponents(ReportComponentFactory factory) {
        if (factory != null) {
            this.report.setHeaderContent(factory.createHeader().createHeader());
            this.report.setFooterContent(factory.createFooter().createFooter());
        }
        return this;
    }

    @Override
    public ReportBuilder setId(String id) {
        this.report.setId(id);
        return this;
    }

    @Override
    public ReportBuilder setTitle(String title) {
        this.report.setTitle(title);
        return this;
    }

    @Override
    public ReportBuilder setHeader(String header) {
        this.report.setHeaderContent(header);
        return this;
    }

    @Override
    public ReportBuilder setFooter(String footer) {
        this.report.setFooterContent(footer);
        return this;
    }

    @Override
    public ReportBuilder addSection(String section) {
        this.report.getSections().add(section);
        return this;
    }

    @Override
    public FinancialReport build() {
        FinancialReport result = this.report;
        reset();
        return result;
    }
}
