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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;

@Service
public class GenerateReportUseCase {

    private final ReportWatermark watermark = new ReportWatermark();
    private final Map<String, FinancialReportTemplate> prototypeRegistry =
            new ConcurrentHashMap<>();

    public GenerateReportUseCase() {
        initPrototypeRegistry();
    }

    public FinancialReport execute(
            String reportId,
            String title,
            String standard,
            String templateKey,
            List<String> additionalSections,
            String watermarkText) {

        ReportComponentFactory factory = selectFactory(standard);
        FinancialReportTemplate template = getTemplate(templateKey);
        FinancialReportTemplate clonedTemplate = template.clonePrototype();
        Map<String, String> dynamicParameters = new HashMap<>();
        dynamicParameters.put("title", title);
        dynamicParameters.put("standard", standard);
        dynamicParameters.put(
                "reportId",
                reportId == null ? "" : reportId);
        clonedTemplate.applyDynamicParameters(dynamicParameters);

        FinancialReport report =
                new ConcreteFinancialReportBuilder()
                        .reset()
                        .setMetadata(resolveReportId(reportId), title)
                        .applyTemplate(clonedTemplate)
                        .setComponents(factory)
                        .build();

        if (additionalSections != null) {
            additionalSections.stream()
                    .filter(section -> section != null && !section.isBlank())
                    .forEach(section -> report.getSections().add(section));
        }

        return watermark.apply(report, watermarkText);
    }

    public FinancialReport execute(
            String standard,
            String title,
            List<String> sections,
            String watermarkText) {
        return execute(
                null,
                title,
                standard,
                "MONTHLY_STD",
                sections,
                watermarkText);
    }

    public void registerCustomTemplate(
            String key,
            FinancialReportTemplate template) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException(
                    "La clave de la plantilla es obligatoria");
        }
        if (template == null) {
            throw new IllegalArgumentException(
                    "La plantilla no puede ser nula");
        }
        prototypeRegistry.put(key.trim().toUpperCase(), template.clonePrototype());
    }

    private void initPrototypeRegistry() {
        prototypeRegistry.put(
                "MONTHLY_STD",
                new FinancialReportTemplate(
                        "MONTHLY_STD",
                        List.of(
                                "Resumen Ejecutivo",
                                "Balance General",
                                "Estado de Resultados"),
                        Map.of(
                                "theme", "DARK_BLUE",
                                "fontSize", "11pt",
                                "standard", "${standard}")));
    }

    private FinancialReportTemplate getTemplate(String templateKey) {
        String resolvedKey = templateKey == null || templateKey.isBlank()
                ? "MONTHLY_STD"
                : templateKey.trim().toUpperCase();
        FinancialReportTemplate template = prototypeRegistry.get(resolvedKey);
        if (template == null) {
            throw new IllegalArgumentException(
                    "Plantilla no encontrada: " + resolvedKey);
        }
        return template;
    }

    private String resolveReportId(String reportId) {
        return reportId == null || reportId.isBlank()
                ? UUID.randomUUID().toString()
                : reportId.trim();
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
