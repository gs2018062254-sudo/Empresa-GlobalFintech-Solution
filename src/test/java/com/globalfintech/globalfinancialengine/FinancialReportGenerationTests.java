package com.globalfintech.globalfinancialengine;

import com.globalfintech.globalfinancialengine.application.GenerateReportUseCase;
import com.globalfintech.globalfinancialengine.domain.builder.ConcreteFinancialReportBuilder;
import com.globalfintech.globalfinancialengine.domain.model.FinancialReport;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FinancialReportGenerationTests {

    private final GenerateReportUseCase useCase = new GenerateReportUseCase();

    @Test
    void shouldGenerateIFRSReport() {
        FinancialReport report = useCase.execute(
                "IFRS", "Balance general",
                List.of("Activos", "Pasivos"), "CONFIDENCIAL");

        assertNotNull(report.getId());
        assertEquals("Balance general", report.getTitle());
        assertTrue(report.getHeaderContent().contains("IFRS"));
        assertEquals("Generado conforme a las normas IFRS.",
                report.getFooterContent());
        assertEquals("CONFIDENCIAL", report.getWatermark());
        assertEquals(2, report.getSections().size());
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
                useCase.execute("UNKNOWN", "Report",
                        List.of("Section"), null));
    }

    @Test
    void builderShouldCreateReportWithSections() {
        FinancialReport report = new ConcreteFinancialReportBuilder()
                .setId("TEST-001")
                .setTitle("Test report")
                .setHeader("Header")
                .setFooter("Footer")
                .addSection("Section 1")
                .build();

        assertEquals("TEST-001", report.getId());
        assertEquals("Test report", report.getTitle());
        assertEquals(List.of("Section 1"), report.getSections());
    }
}
