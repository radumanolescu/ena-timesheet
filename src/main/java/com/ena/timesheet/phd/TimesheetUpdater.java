package com.ena.timesheet.phd;

import com.ena.timesheet.ena.EnaTimesheet;

public class TimesheetUpdater {
    private final PhdTemplate phdTemplate;

    public TimesheetUpdater(PhdTemplate phdTemplate) {
        this.phdTemplate = phdTemplate;
    }

    public PhdTemplate update(EnaTimesheet enaTimesheet) {
        // ToDo: update phdTemplate with enaTimesheet
        return phdTemplate;
    }
}
