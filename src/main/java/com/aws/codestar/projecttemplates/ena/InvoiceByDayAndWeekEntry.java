package com.aws.codestar.projecttemplates.ena;

import com.aws.codestar.projecttemplates.json.JsonMapped;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

public class InvoiceByDayAndWeekEntry extends JsonMapped<InvoiceByDayAndWeekEntry> {
    private static DateTimeFormatter mmddyy = DateTimeFormatter.ofPattern("MM/dd/yy");
    private static Locale locale = Locale.US;
    private NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);

    public InvoiceByDayAndWeekEntry() {
    }

    private String project;
    private String activity;
    private LocalDate date;
    private Float hours;
    private Float rate;
    private String description;
    private Float charge;

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getDate() {
        return date.format(mmddyy);
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getHours() {
        return "" + hours;
    }

    public void setHours(Float hours) {
        this.hours = hours;
    }

    public String getRate() {
        return String.format("$%.0f/hr", rate);
    }

    public void setRate(Float rate) {
        this.rate = rate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCharge() {
        return String.format("$%.2f", charge);
    }

    public void setCharge(Float charge) {
        this.charge = charge;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InvoiceByDayAndWeekEntry that = (InvoiceByDayAndWeekEntry) o;
        return project.equals(that.project) && activity.equals(that.activity) && date.equals(that.date) && hours.equals(that.hours) && rate.equals(that.rate) && description.equals(that.description) && charge.equals(that.charge);
    }

    @Override
    public int hashCode() {
        return Objects.hash(project, activity, date, hours, rate, description, charge);
    }
}
