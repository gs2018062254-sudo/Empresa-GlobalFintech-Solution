package com.globalfintech.globalfinancialengine.infrastructure.factory;

import com.globalfintech.globalfinancialengine.domain.factory.ReportComponentFactory;
import com.globalfintech.globalfinancialengine.domain.factory.ReportHeader;
import com.globalfintech.globalfinancialengine.domain.factory.ReportFooter;

public class USGAAPReportFactory implements ReportComponentFactory {
    @Override
    public ReportHeader createHeader() {
        return () -> "FINANCIAL REPORT - US GAAP";
    }

    @Override
    public ReportFooter createFooter() {
        return () -> "Prepared under US GAAP accounting standards.";
    }
}