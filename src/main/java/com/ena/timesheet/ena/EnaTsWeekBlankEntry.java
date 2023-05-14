package com.ena.timesheet.ena;

import java.time.LocalDate;

/**
 * An entry that represents a blank line in the table
 */
public class EnaTsWeekBlankEntry extends EnaTsEntry {

    public EnaTsWeekBlankEntry(LocalDate month, float entryId) {
        super(month);
        this.setEntryId(entryId);
        this.setProjectId("");
        this.setActivity("");
        this.setDescription("");
    }

    @Override
    public String getDate() {
        return "";
    }

    @Override
    public String formattedHours() {
        return "";
    }

    @Override
    public String getRate() {
        return "";
    }

    @Override
    public String getCharge() {
        return "";
    }
}
