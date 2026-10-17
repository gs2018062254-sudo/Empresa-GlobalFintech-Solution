package com.globalfintech.globalfinancialengine.domain.model;

import java.util.ArrayList;
import java.util.List;

public class FinancialReport {
    private String id;
    private String title;
    private String headerContent;
    private String footerContent;
    private String watermark;
    private List<String> sections = new ArrayList<>();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getHeaderContent() { return headerContent; }
    public void setHeaderContent(String headerContent) {
        this.headerContent = headerContent;
    }

    public String getFooterContent() { return footerContent; }
    public void setFooterContent(String footerContent) {
        this.footerContent = footerContent;
    }

    public String getWatermark() { return watermark; }
    public void setWatermark(String watermark) { this.watermark = watermark; }

    public List<String> getSections() { return sections; }
    public void setSections(List<String> sections) {
        this.sections = sections == null
                ? new ArrayList<>()
                : new ArrayList<>(sections);
    }
}

