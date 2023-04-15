package com.aws.codestar.projecttemplates.ena;

/**
 * An entry that represents a total for a week
 */
public class XWeekTotalEntry extends XEntry {

    public XWeekTotalEntry(float entryId, String hoursLabel, Float totalHours, String chargeLabel, Float totalCharge) {
        this.hoursLabel = hoursLabel;
        this.chargeLabel = chargeLabel;
        this.setEntryId(entryId);
        this.setProjectId("");
        this.setActivity("");
        this.setHours(totalHours);
        this.setDescription(chargeLabel);
        this.setCharge(totalCharge);
    }

    private String hoursLabel;

    private String chargeLabel;

    /**
     * A hack to get the total hours entry to show up in the table
     */
    @Override
    public String getDate() {
        return hoursLabel;
    }

    @Override
    public String getCharge() {
        return "";
    }

}
