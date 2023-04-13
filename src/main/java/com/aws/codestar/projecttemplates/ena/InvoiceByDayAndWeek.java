package com.aws.codestar.projecttemplates.ena;

import java.util.ArrayList;
import java.util.List;

public class InvoiceByDayAndWeek {

    public InvoiceByDayAndWeek() {
        entries = new ArrayList<>();
    }

    public InvoiceByDayAndWeek(List<InvoiceActivityEntry> entries) {
        this.entries = entries;
    }

    private List<InvoiceActivityEntry> entries;
    
    public List<InvoiceActivityEntry> getEntries() {
        return entries;
    }
    
    public void setEntries(List<InvoiceActivityEntry> entries) {
        this.entries = entries;
    }
    
    public void addEntry(InvoiceActivityEntry entry) {
        entries.add(entry);
    }

}
