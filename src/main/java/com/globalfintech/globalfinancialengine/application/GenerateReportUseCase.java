package com.globalfintech.globalfinancialengine.application;

import com.globalfintech.globalfinancialengine.domain.builder.ConcreteFinancialReportBuilder;
import com.globalfintech.globalfinancialengine.domain.factory.ReportComponentFactory;
import com.globalfintech.globalfinancialengine.domain.model.FinancialReport;
import com.globalfintech.globalfinancialengine.domain.prototype.FinancialReportTemplate;
import com.globalfintech.globalfinancialengine.domain.watermark.ReportWatermark;
import com.globalfintech.globalfinancialengine.infrastructure.factory.EUTAXReportFactory;
import com.globalfintech.globalfinancialengine.infrastructure.factory.IFRSReportFactory;
import com.globalfintech.globalfinancialengine.infrastructure.factory.USGAAPReportFactory;
import com.globalfintech.globalfinancialengine.infrastructure.persistence.RedisPrototypeRegistry;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final RedisPrototypeRegistry redisPrototypeRegistry;

    public GenerateReportUseCase() {
        this(null);
    }

    @Autowired
    public GenerateReportUseCase(RedisPrototypeRegistry redisPrototypeRegistry) {
        this.redisPrototypeRegistry = redisPrototypeRegistry;
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
        String resolvedKey = key.trim().toUpperCase();
        FinancialReportTemplate clonedTemplate = template.clonePrototype();
        prototypeRegistry.put(resolvedKey, clonedTemplate);
        saveTemplateToDistributedCache(resolvedKey, clonedTemplate);
    }

    private void initPrototypeRegistry() {
        FinancialReportTemplate monthlyTemplate =
                new FinancialReportTemplate(
                        "MONTHLY_STD",
                        List.of(
                                "Resumen Ejecutivo",
                                "Balance General",
                                "Estado de Resultados"),
                        Map.of(
                                "theme", "DARK_BLUE",
                                "fontSize", "11pt",
                                "standard", "${standard}"));
        prototypeRegistry.put("MONTHLY_STD", monthlyTemplate);
        saveTemplateToDistributedCache("MONTHLY_STD", monthlyTemplate);
    }

    private FinancialReportTemplate getTemplate(String templateKey) {
        String resolvedKey = templateKey == null || templateKey.isBlank()
                ? "MONTHLY_STD"
                : templateKey.trim().toUpperCase();
        FinancialReportTemplate template = prototypeRegistry.get(resolvedKey);
        if (template == null) {
            template = getTemplateFromDistributedCache(resolvedKey);
            if (template != null) {
                prototypeRegistry.put(resolvedKey, template);
            }
        }
        if (template == null) {
            throw new IllegalArgumentException(
                    "Plantilla no encontrada: " + resolvedKey);
        }
        return template;
    }

    private FinancialReportTemplate getTemplateFromDistributedCache(String key) {
        if (redisPrototypeRegistry == null) {
            return null;
        }

        try {
            return redisPrototypeRegistry
                    .find(key, FinancialReportTemplate.class)
                    .orElse(null);
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    private void saveTemplateToDistributedCache(
            String key,
            FinancialReportTemplate template) {
        if (redisPrototypeRegistry == null) {
            return;
        }

        try {
            redisPrototypeRegistry.register(key, template);
        } catch (RuntimeException ignored) {
            // Fallback silencioso a cache en memoria cuando Redis no esta disponible.
        }
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
