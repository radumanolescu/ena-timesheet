package com.ena.timesheet.phd;

import com.ena.timesheet.ena.EnaTimesheet;
import com.ena.timesheet.ena.EnaTsEntry;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PhdTemplate {
    public PhdTemplate(String yearMonth, List<PhdTemplateEntry> entries) {
        this.yearMonth = yearMonth;
        this.entries = entries;
    }

    public PhdTemplate(String yearMonth, InputStream inputStream) throws IOException {
        this.yearMonth = yearMonth;
        Parser parser = new Parser();
        this.entries = parser.parseEntries(inputStream);
    }

    public PhdTemplate(String yearMonth, byte[] bytes) throws IOException {
        this.yearMonth = yearMonth;
        Parser parser = new Parser();
        this.entries = parser.parseBytes(bytes);
        this.xlsxBytes = bytes;
    }

    public PhdTemplate(String yearMonth, File phdTemplateFile) throws IOException {
        this.yearMonth = yearMonth;
        try (InputStream inputStream = new FileInputStream(phdTemplateFile)) {
            Parser parser = new Parser();
            this.entries = parser.parseEntries(inputStream);
        } catch (Exception e) {
            throw e;
        }
    }

    public List<PhdTemplateEntry> getEntries() {
        return entries;
    }

    public byte[] dropdowns() {
        StringBuilder sb = new StringBuilder();
        for (String clientTask : clientTasks()) {
            sb.append(clientTask + "\r\n");
        }
        byte[] bytes = sb.toString().getBytes(Charset.forName("UTF-8"));
        return bytes;
    }

    public List<String> clientTasks() {
        return entries.stream()
                .filter(entry -> !entry.getTask().equals("TASK"))
                .map(PhdTemplateEntry::clientHashTask)
                .collect(Collectors.toList());
    }

    public Set<String> clientTaskSet() {
        return new HashSet<>(clientTasks());
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

    public double totalHours() {
        return entries.stream().mapToDouble(PhdTemplateEntry::totalHours).sum();
    }

    private byte[] xlsxBytes;

    public byte[] getXlsxBytes() {
        return xlsxBytes;
    }

    public void setXlsxBytes(byte[] xlsxBytes) {
        this.xlsxBytes = xlsxBytes;
    }

    public void update(EnaTimesheet enaTimesheet) {
        checkTasks(enaTimesheet);
        Map<String, Map<Integer, Double>> enaEffort = enaTimesheet.totalHoursByClientTaskDay();
        for (PhdTemplateEntry phdEntry : entries) {
            Map<Integer, Double> enaEntry = enaEffort.get(phdEntry.clientHashTask());
            if (enaEntry != null) {
                phdEntry.setEffort(enaEntry);
            }
        }
        double enaTotalHours = enaTimesheet.totalHours();
        double phdTotalHours = totalHours();
        if (enaTotalHours != phdTotalHours) {
            throw new RuntimeException("Total hours mismatch: ENA=" + enaTotalHours + " PHD=" + phdTotalHours);
        }
    }

    public void checkTasks(EnaTimesheet enaTimesheet) {
        Set<String> clientTaskSet = clientTaskSet();
        for (EnaTsEntry enaEntry : enaTimesheet.getEntries()) {
            String matchKey = enaEntry.projectActivity();
            if (!clientTaskSet.contains(matchKey)) {
                throw new RuntimeException("Unknown ENA task: " + matchKey);
            }
        }
    }

    public void updateXlsx(int index) {
        if (xlsxBytes == null) return;
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(xlsxBytes)) {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(index);
            for (Row row : sheet) {
                //row.getCell()
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
