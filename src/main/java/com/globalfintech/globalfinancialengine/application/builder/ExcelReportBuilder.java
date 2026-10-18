package com.globalfintech.globalfinancialengine.application.builder;

import com.globalfintech.globalfinancialengine.application.ReportExportService;
import com.globalfintech.globalfinancialengine.domain.builder.ConcreteFinancialReportBuilder;
import com.globalfintech.globalfinancialengine.domain.builder.ReportBuilder;
import com.globalfintech.globalfinancialengine.domain.factory.ReportComponentFactory;
import com.globalfintech.globalfinancialengine.domain.model.FinancialReport;
import com.globalfintech.globalfinancialengine.domain.prototype.FinancialReportTemplate;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class ExcelReportBuilder implements ReportBuilder {
    private final ConcreteFinancialReportBuilder delegate =
            new ConcreteFinancialReportBuilder();
    private final ReportExportService exportService;

    public ExcelReportBuilder(ReportExportService exportService) {
        this.exportService = exportService;
    }

    public byte[] generate(FinancialReport report) throws IOException {
        return exportService.exportExcel(report);
    }

    @Override
    public ReportBuilder reset() {
        delegate.reset();
        return this;
    }

    @Override
    public ReportBuilder setMetadata(String id, String title) {
        delegate.setMetadata(id, title);
        return this;
    }

    @Override
    public ReportBuilder applyTemplate(FinancialReportTemplate template) {
        delegate.applyTemplate(template);
        return this;
    }

    @Override
    public ReportBuilder setComponents(ReportComponentFactory factory) {
        delegate.setComponents(factory);
        return this;
    }

    @Override
    public ReportBuilder addSection(String section) {
        delegate.addSection(section);
        return this;
    }

    @Override
    public ReportBuilder setId(String id) {
        delegate.setId(id);
        return this;
    }

    @Override
    public ReportBuilder setTitle(String title) {
        delegate.setTitle(title);
        return this;
    }

    @Override
    public ReportBuilder setHeader(String header) {
        delegate.setHeader(header);
        return this;
    }

    @Override
    public ReportBuilder setFooter(String footer) {
        delegate.setFooter(footer);
        return this;
    }

    @Override
    public FinancialReport build() {
        return delegate.build();
    }
}
