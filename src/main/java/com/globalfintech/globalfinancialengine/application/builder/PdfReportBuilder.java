package com.globalfintech.globalfinancialengine.application.builder;

import com.globalfintech.globalfinancialengine.application.ReportExportService;
import com.globalfintech.globalfinancialengine.domain.builder.ConcreteFinancialReportBuilder;
import com.globalfintech.globalfinancialengine.domain.builder.ReportBuilder;
import com.globalfintech.globalfinancialengine.domain.model.FinancialReport;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.List;

@Component
public class PdfReportBuilder implements ReportBuilder {
    private final ConcreteFinancialReportBuilder delegate =
            new ConcreteFinancialReportBuilder();
    private final ReportExportService exportService;

    public PdfReportBuilder(ReportExportService exportService) {
        this.exportService = exportService;
    }

    public byte[] generate(FinancialReport report) throws IOException {
        return exportService.exportPdf(report);
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
    public ReportBuilder setSections(List<String> sections) {
        delegate.setSections(sections);
        return this;
    }

    @Override
    public ReportBuilder addSection(String section) {
        delegate.addSection(section);
        return this;
    }

    @Override
    public FinancialReport build() {
        return delegate.build();
    }
}
