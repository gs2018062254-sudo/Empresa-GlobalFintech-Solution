package com.globalfintech.globalfinancialengine.domain.prototype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FinancialReportTemplate
        implements Prototype<FinancialReportTemplate> {

    private String templateId;
    private List<String> defaultSections = new ArrayList<>();
    private Map<String, String> stylingConfig = new HashMap<>();

    public FinancialReportTemplate() {
    }

    public FinancialReportTemplate(
            String templateId,
            List<String> defaultSections,
            Map<String, String> stylingConfig) {
        this.templateId = templateId;
        setDefaultSections(defaultSections);
        setStylingConfig(stylingConfig);
    }

    @Override
    public FinancialReportTemplate clonePrototype() {
        return new FinancialReportTemplate(
                templateId, defaultSections, stylingConfig);
    }

    public void applyDynamicParameters(Map<String, String> parameters) {
        if (parameters == null || parameters.isEmpty()) {
            return;
        }

        for (int i = 0; i < defaultSections.size(); i++) {
            String section = defaultSections.get(i);
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                section = section.replace(
                        "${" + entry.getKey() + "}",
                        entry.getValue() == null ? "" : entry.getValue());
            }
            defaultSections.set(i, section);
        }

        for (Map.Entry<String, String> style : stylingConfig.entrySet()) {
            String value = style.getValue();
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                value = value.replace(
                        "${" + entry.getKey() + "}",
                        entry.getValue() == null ? "" : entry.getValue());
            }
            style.setValue(value);
        }
    }

    public String getTemplateId() { return templateId; }
    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public List<String> getDefaultSections() {
        return new ArrayList<>(defaultSections);
    }
    public void setDefaultSections(List<String> sections) {
        this.defaultSections = sections == null
                ? new ArrayList<>()
                : new ArrayList<>(sections);
    }

    public Map<String, String> getStylingConfig() {
        return new HashMap<>(stylingConfig);
    }
    public void setStylingConfig(Map<String, String> config) {
        this.stylingConfig = config == null
                ? new HashMap<>()
                : new HashMap<>(config);
    }
}
