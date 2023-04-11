package com.aws.codestar.projecttemplates.ena;


import java.util.List;

public class EnaTimesheet {
    public EnaTimesheet(List<EnaTimesheetEntry> entries) {
        this.entries = entries;
    }
    public List<EnaTimesheetEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<EnaTimesheetEntry> entries) {
        this.entries = entries;
    }

    private List<EnaTimesheetEntry> entries;

}
