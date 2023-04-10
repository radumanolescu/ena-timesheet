package com.aws.codestar.projecttemplates.phd;

import com.aws.codestar.projecttemplates.json.JsonMapped;
import com.aws.codestar.projecttemplates.util.Text;

import java.beans.Transient;
import java.util.Objects;

public class PhdTemplateEntry extends JsonMapped<PhdTemplateEntry> {

    public PhdTemplateEntry() {
    }

    private String client;
    private String task;

    public PhdTemplateEntry(String client, String task) {
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
    public String getClientTask() {
        return "\"" + client + "\",\"" + task + "\"";
    }

    /**
     * A concatenation of `client#task`, stripped of all quotes, separated by #
     */
    @Transient
    public String getClientConcatTask() {
        return Text.unquote(client) + "#" + Text.unquote(task);
    }
}
