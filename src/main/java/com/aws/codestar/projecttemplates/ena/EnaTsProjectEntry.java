package com.aws.codestar.projecttemplates.ena;

import java.text.DecimalFormat;
import java.util.Objects;

public class EnaTsProjectEntry implements Comparable<EnaTsProjectEntry>{

    public EnaTsProjectEntry(String projectId, String activity, Float hours) {
        this.projectId = projectId;
        this.activity = activity;
        this.hours = hours;
    }

    private static final DecimalFormat decimalFormat = new DecimalFormat("#.##");

    private String projectId = "";
    private String activity = "";

    protected Float hours;

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getHours() {
        return decimalFormat.format(hours);
    }

    public void setHours(Float hours) {
        this.hours = hours;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnaTsProjectEntry that = (EnaTsProjectEntry) o;
        return projectId.equals(that.projectId) && activity.equals(that.activity) && hours.equals(that.hours);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, activity, hours);
    }

    private String projectActivity() {
        return projectId + "#" + activity;
    }

    @Override
    public int compareTo(EnaTsProjectEntry that) {
        return this.projectActivity().compareTo(that.projectActivity());
    }

    @Override
    public String toString() {
        return projectId + ", " + activity + ", " + hours;
    }
}
