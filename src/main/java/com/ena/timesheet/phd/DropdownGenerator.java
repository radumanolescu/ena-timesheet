package com.ena.timesheet.phd;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class DropdownGenerator {

    private PhdTemplate phdTemplate;

    public DropdownGenerator(PhdTemplate phdTemplate) {
        this.phdTemplate = phdTemplate;
    }

    public byte[] generateDropdown() {
        StringBuilder sb = new StringBuilder();
        for (PhdTemplateEntry entry : phdTemplate.getEntries()) {
            if (!entry.getTask().equals("TASK")) {
                sb.append(entry.getClientConcatTask() + "\r\n");
            }
        }
        byte[] bytes = sb.toString().getBytes(Charset.forName("UTF-8"));
        return bytes;
    }

    public PhdTemplate getPhdTemplate() {
        return phdTemplate;
    }

    public void setPhdTemplate(PhdTemplate phdTemplate) {
        this.phdTemplate = phdTemplate;
    }
}
