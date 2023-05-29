package com.ena.timesheet.phd;

import com.ena.timesheet.json.JsonMapped;
import com.ena.timesheet.util.Text;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.beans.Transient;
import java.util.Map;
import java.util.Objects;

public class PhdTemplateEntry extends JsonMapped<PhdTemplateEntry> {

    public PhdTemplateEntry() {
        // used in ser/de test
    }

    private int rowNum;
    private String client;
    private String task;

    public PhdTemplateEntry(int rowNum, String client, String task) {
        this.rowNum = rowNum;
        this.client = client;
        this.task = task;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhdTemplateEntry that = (PhdTemplateEntry) o;
        return client.equals(that.client) && task.equals(that.task);
    }

    @Override
    public int hashCode() {
        return Objects.hash(client, task);
    }

    /**
     * Valid CSV format requires quotes when string contains commas
     */
    @Transient
    public String clientCommaTask() {
        return "\"" + clean(client) + "\",\"" + clean(task) + "\"";
    }

    /**
     * A concatenation of `client#task`, stripped of all quotes, separated by #
     */
    @Transient
    public String clientHashTask() {
        return Text.unquote(client) + "#" + Text.unquote(task);
    }

    private String clean(String s) {
        return Text.unquote(s).replaceAll(",", "");
    }

    private Map<Integer, Double> effort = Map.of();

    public Map<Integer, Double> getEffort() {
        return effort;
    }

    public void setEffort(Map<Integer, Double> effort) {
        this.effort = effort;
    }

    public double totalHours() {
        return effort.values().stream().reduce(0.0d, Double::sum);
    }

    public int getRowNum() {
        return rowNum;
    }

    public void setRowNum(int rowNum) {
        this.rowNum = rowNum;
    }

    @JsonIgnore
    public boolean isBlank() {
        return Text.isBlank(client) && Text.isBlank(task);
    }

    public String entryType(){
        String cl = Text.isBlank(client) ? "null" : "Client";
        String tk = Text.isBlank(task) ? "null" : "Task";
        return cl + "_" + tk;
    }
}
