package com.aws.codestar.projecttemplates.phd;

import java.util.List;

public class PhdTemplate {
    public PhdTemplate(List<PhdTemplateEntry> entries) {
        this.entries = entries;
    }

    public List<PhdTemplateEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<PhdTemplateEntry> entries) {
        this.entries = entries;
    }

    private List<PhdTemplateEntry> entries;
}
