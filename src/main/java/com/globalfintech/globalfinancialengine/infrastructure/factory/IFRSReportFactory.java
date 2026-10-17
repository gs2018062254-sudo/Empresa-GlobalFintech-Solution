package com.globalfintech.globalfinancialengine.infrastructure.factory;

import com.globalfintech.globalfinancialengine.domain.factory.ReportComponentFactory;
import com.globalfintech.globalfinancialengine.domain.factory.ReportHeader;
import com.globalfintech.globalfinancialengine.domain.factory.ReportFooter;

public class IFRSReportFactory implements ReportComponentFactory {
    @Override
    public ReportHeader createHeader() {
        return () -> "INFORME FINANCIERO - NORMATIVA IFRS";
    }

    @Override
    public ReportFooter createFooter() {
        return () -> "Generado conforme a las normas IFRS.";
    }
}