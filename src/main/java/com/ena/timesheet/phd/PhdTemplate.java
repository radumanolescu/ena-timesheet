package com.ena.timesheet.phd;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

public class PhdTemplate {
    public PhdTemplate(String yearMonth, List<PhdTemplateEntry> entries) {
        this.yearMonth = yearMonth;
        this.entries = entries;
    }

    public PhdTemplate(String yearMonth, byte[] bytes) throws IOException {
        this.yearMonth = yearMonth;
        Parser parser = new Parser();
        this.entries = parser.parseBytes(bytes);
        this.xlsxBytes = bytes;
    }

    public List<PhdTemplateEntry> getEntries() {
        return entries;
    }

    public byte[] dropdowns() {
        StringBuilder sb = new StringBuilder();
        for (PhdTemplateEntry entry : entries) {
            if (!entry.getTask().equals("TASK")) {
                sb.append(entry.clientHashTask() + "\r\n");
            }
        }
        byte[] bytes = sb.toString().getBytes(Charset.forName("UTF-8"));
        return bytes;
    }

    public void setEntries(List<PhdTemplateEntry> entries) {
        this.entries = entries;
    }

    private List<PhdTemplateEntry> entries;

    private String yearMonth; // yyyyMM

    public String getYearMonth() {
        return yearMonth;
    }

    public void setYearMonth(String yearMonth) {
        this.yearMonth = yearMonth;
    }

    private byte[] xlsxBytes;

    public byte[] getXlsxBytes() {
        return xlsxBytes;
    }

    public void setXlsxBytes(byte[] xlsxBytes) {
        this.xlsxBytes = xlsxBytes;
    }
}
