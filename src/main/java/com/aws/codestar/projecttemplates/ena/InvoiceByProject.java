package com.aws.codestar.projecttemplates.ena;

import java.util.ArrayList;
import java.util.List;

public class InvoiceByProject {
    public InvoiceByProject() {
        entries = new ArrayList<>();
    }

    public InvoiceByProject(List<InvoiceByProjectEntry> entries) {
        this.entries = entries;
    }

    private List<InvoiceByProjectEntry> entries;

    public List<InvoiceByProjectEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<InvoiceByProjectEntry> entries) {
        this.entries = entries;
    }

    public void addEntry(InvoiceByProjectEntry entry) {
        entries.add(entry);
    }
}
