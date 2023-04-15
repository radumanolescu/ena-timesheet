package com.aws.codestar.projecttemplates.ena;

/**
 * An entry that represents a blank line in the table
 */
public class XWeekBlankEntry extends XEntry {

    public XWeekBlankEntry(float entryId) {
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
    public String getHours() {
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
