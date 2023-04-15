package com.aws.codestar.projecttemplates.ena;

import java.util.ArrayList;
import java.util.List;

public class InvoiceByDayAndWeek {

    public InvoiceByDayAndWeek() {
        entries = new ArrayList<>();
    }

    public InvoiceByDayAndWeek(List<InvoiceByDayAndWeekEntry> entries) {
        this.entries = entries;
    }

    private List<InvoiceByDayAndWeekEntry> entries;
    
    public List<InvoiceByDayAndWeekEntry> getEntries() {
        return entries;
    }
    
    public void setEntries(List<InvoiceByDayAndWeekEntry> entries) {
        this.entries = entries;
    }
    
    public void addEntry(InvoiceByDayAndWeekEntry entry) {
        entries.add(entry);
    }

}
