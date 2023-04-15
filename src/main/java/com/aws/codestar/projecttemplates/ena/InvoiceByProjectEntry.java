package com.aws.codestar.projecttemplates.ena;

import java.util.Objects;

public class InvoiceByProjectEntry {
    public InvoiceByProjectEntry() {
    }

    public InvoiceByProjectEntry(String project, String activity, Float hours) {
        this.project = project;
        this.activity = activity;
        this.hours = hours;
    }

    private String project;
    private String activity;
    private Float hours;

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

    public String getHours() {
        return "" + hours;
    }

    public void setHours(Float hours) {
        this.hours = hours;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InvoiceByProjectEntry that = (InvoiceByProjectEntry) o;
        return project.equals(that.project) && activity.equals(that.activity) && hours.equals(that.hours);
    }

    @Override
    public int hashCode() {
        return Objects.hash(project, activity, hours);
    }
}
