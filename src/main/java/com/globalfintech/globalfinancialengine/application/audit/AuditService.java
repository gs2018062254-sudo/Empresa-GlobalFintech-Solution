package com.globalfintech.globalfinancialengine.application.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuditService {
    private static final Logger log = LoggerFactory.getLogger(AuditService.class);

    public void record(String action, String reportId, String result) {
        log.info("AUDIT action={} reportId={} result={}",
                safe(action), safe(reportId), safe(result));
    }

    private String safe(String value) {
        if (value == null) return "N/A";
        return value.replaceAll("[\\r\\n\\t]", "_");
    }
}
