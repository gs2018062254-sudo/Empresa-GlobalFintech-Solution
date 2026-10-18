package com.globalfintech.globalfinancialengine.application;

import com.globalfintech.globalfinancialengine.domain.builder.ConcreteFinancialReportBuilder;
import com.globalfintech.globalfinancialengine.domain.factory.ReportComponentFactory;
import com.globalfintech.globalfinancialengine.domain.model.FinancialReport;
import com.globalfintech.globalfinancialengine.domain.prototype.FinancialReportTemplate;
import com.globalfintech.globalfinancialengine.domain.watermark.ReportWatermark;
import com.globalfintech.globalfinancialengine.infrastructure.factory.EUTAXReportFactory;
import com.globalfintech.globalfinancialengine.infrastructure.factory.IFRSReportFactory;
import com.globalfintech.globalfinancialengine.infrastructure.factory.USGAAPReportFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class GenerateReportUseCase {

    private final ReportWatermark watermark = new ReportWatermark();

    public FinancialReport execute(
            String standard,
            String title,
            List<String> sections,
            String watermarkText) {

        ReportComponentFactory factory = selectFactory(standard);

        FinancialReportTemplate template =
                new FinancialReportTemplate(
                        standard,
                        sections,
                        Map.of("standard", standard));

        FinancialReport report =
                new ConcreteFinancialReportBuilder()
                        .setId(UUID.randomUUID().toString())
                        .setTitle(title)
                        .setHeader(factory.createHeader().createHeader())
                        .setFooter(factory.createFooter().createFooter())
                        .setSections(template.clonePrototype().getDefaultSections())
                        .build();

        return watermark.apply(report, watermarkText);
    }

    private ReportComponentFactory selectFactory(String standard) {
        if (standard == null) {
            throw new IllegalArgumentException(
                    "Debe especificar una normativa");
        }

        return switch (standard.trim().toUpperCase()) {
            case "IFRS" -> new IFRSReportFactory();
            case "USGAAP" -> new USGAAPReportFactory();
            case "EU_TAX" -> new EUTAXReportFactory();
            default -> throw new IllegalArgumentException(
                    "Normativa no soportada: " + standard);
        };
    }
}
