package com.ena.timesheet.phd;

import com.ena.timesheet.ena.EnaTimesheet;
import com.ena.timesheet.ena.EnaTsEntry;

import java.util.Map;
import java.util.stream.Collectors;

public class TimesheetUpdater {
    private final PhdTemplate phdTemplate;

    public TimesheetUpdater(PhdTemplate phdTemplate) {
        this.phdTemplate = phdTemplate;
    }

    /*
    public PhdTemplate update(EnaTimesheet enaTimesheet) {
        // For each PHD entry, find the corresponding ENA entries, grouped by day and update the total effort
        Map<String, EnaTsEntry> enaEntries = enaTimesheet.getEntries()
                .stream().collect(Collectors.toMap(e -> e.matchKey(), e -> e));
        for (PhdTemplateEntry phdEntry : phdTemplate.getEntries()) {
            EnaTsEntry enaEntry = enaEntries.get(phdEntry.clientHashTask());
            if (enaEntry != null) {
                enaEntry.effort.put(phdEntry.effort);
            }
        }
        return phdTemplate;
    }
    */
}
