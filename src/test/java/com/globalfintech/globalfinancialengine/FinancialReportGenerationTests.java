package com.globalfintech.globalfinancialengine;

import com.globalfintech.globalfinancialengine.application.GenerateReportUseCase;
import com.globalfintech.globalfinancialengine.domain.builder.ConcreteFinancialReportBuilder;
import com.globalfintech.globalfinancialengine.domain.model.FinancialReport;
import com.globalfintech.globalfinancialengine.domain.prototype.FinancialReportTemplate;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FinancialReportGenerationTests {

    private final GenerateReportUseCase useCase = new GenerateReportUseCase();

    @Test
    void shouldGenerateIFRSReport() {
        FinancialReport report = useCase.execute(
                "REP-001",
                "Balance general",
                "IFRS",
                "MONTHLY_STD",
                List.of("Activos", "Pasivos"),
                "CONFIDENCIAL");

        assertEquals("REP-001", report.getId());
        assertEquals("Balance general", report.getTitle());
        assertTrue(report.getHeaderContent().contains("IFRS"));
        assertEquals("Generado conforme a las normas IFRS.",
                report.getFooterContent());
        assertEquals("CONFIDENCIAL", report.getWatermark());
        assertTrue(report.getSections().contains("Resumen Ejecutivo"));
        assertTrue(report.getSections().contains("Activos"));
    }

    @Test
    void shouldGenerateUSGAAPReport() {
        FinancialReport report = useCase.execute(
                "USGAAP", "Financial statement",
                List.of("Assets"), "INTERNAL");

        assertEquals("FINANCIAL REPORT - US GAAP",
                report.getHeaderContent());
        assertEquals("INTERNAL", report.getWatermark());
    }

    @Test
    void shouldGenerateEUTAXReport() {
        FinancialReport report = useCase.execute(
                "EU_TAX", "Tax report",
                List.of("Income"), null);

        assertEquals("INFORME FISCAL - UNION EUROPEA",
                report.getHeaderContent());
        assertEquals("CONFIDENCIAL", report.getWatermark());
    }

    @Test
    void shouldRejectUnsupportedStandard() {
        assertThrows(IllegalArgumentException.class, () ->
                useCase.execute("REP-404", "Report", "UNKNOWN",
                        "MONTHLY_STD", List.of("Section"), null));
    }

    @Test
    void shouldRejectUnknownTemplate() {
        assertThrows(IllegalArgumentException.class, () ->
                useCase.execute("REP-405", "Report", "IFRS",
                        "MISSING_TEMPLATE", List.of("Section"), null));
    }

    @Test
    void shouldRegisterCustomTemplate() {
        useCase.registerCustomTemplate(
                "WEEKLY_STD",
                new FinancialReportTemplate(
                        "WEEKLY_STD",
                        List.of("Resumen semanal"),
                        Map.of("theme", "LIGHT")));

        FinancialReport report = useCase.execute(
                "REP-002",
                "Reporte semanal",
                "IFRS",
                "WEEKLY_STD",
                List.of("Ingresos"),
                null);

        assertTrue(report.getSections().contains("Resumen semanal"));
        assertTrue(report.getSections().contains("Ingresos"));
    }

    @Test
    void builderShouldCreateReportWithSections() {
        FinancialReport report = new ConcreteFinancialReportBuilder()
                .reset()
                .setMetadata("TEST-001", "Test report")
                .setHeader("Header")
                .setFooter("Footer")
                .addSection("Section 1")
                .build();

        assertEquals("TEST-001", report.getId());
        assertEquals("Test report", report.getTitle());
        assertEquals(List.of("Section 1"), report.getSections());
    }
}
