package com.globalfintech.globalfinancialengine.infrastructure.factory;

import com.globalfintech.globalfinancialengine.domain.factory.ReportComponentFactory;
import com.globalfintech.globalfinancialengine.domain.factory.ReportHeader;
import com.globalfintech.globalfinancialengine.domain.factory.ReportFooter;

public class EUTAXReportFactory implements ReportComponentFactory {
    @Override
    public ReportHeader createHeader() {
        return () -> "INFORME FISCAL - UNION EUROPEA";
    }

    @Override
    public ReportFooter createFooter() {
        return () -> "Informe preparado para el contexto fiscal de la UE.";
    }
}