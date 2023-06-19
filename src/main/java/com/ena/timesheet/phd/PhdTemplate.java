package com.ena.timesheet.phd;

import com.ena.timesheet.ena.EnaTimesheet;
import com.ena.timesheet.ena.EnaTsEntry;
import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.ena.timesheet.util.IOUtil.getBytes;

public class PhdTemplate {
    public PhdTemplate(String yearMonth, List<PhdTemplateEntry> entries) {
        this.yearMonth = yearMonth;
        this.entries = entries;
    }

    public PhdTemplate(String yearMonth, InputStream inputStream) throws IOException {
        this.yearMonth = yearMonth;
        this.xlsxBytes = getBytes(inputStream);
        Parser parser = new Parser();
        this.entries = parser.parseBytes(xlsxBytes);
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
            this.xlsxBytes = Files.readAllBytes(phdTemplateFile.toPath());
        }
    }

    public List<PhdTemplateEntry> getEntries() {
        return entries;
    }

    public byte[] dropdowns() {
        StringBuilder sb = new StringBuilder();
        for (String clientTask : clientTasks()) {
            sb.append(clientTask).append("\r\n");
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
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
        updateXlsx(0);
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

    // In the spreadsheet, the column for day 1 of the month is column 3 in Excel ie row.getCell(2)
    public static final int colOffset = 1;

    public void updateXlsx(int index) {
        if (xlsxBytes == null) return;
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(xlsxBytes)) {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(index);
            int rowId = 0;
            int numEntries = entries.size();
            for (Row row : sheet) {
                if (rowId >= numEntries) break;
                eraseEffort(rowId, row);
                PhdTemplateEntry entry = entries.get(rowId);
                for (Map.Entry<Integer, Double> dayEffort : entry.getEffort().entrySet()) {
                    int day = dayEffort.getKey();
                    double effort = dayEffort.getValue();
                    Cell cell = row.getCell(colOffset + day);
                    if (cell == null) {
                        cell = row.createCell(colOffset + day);
                    }
                    cell.setCellValue(effort);
                }
                rowId++;
            }
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            evaluator.evaluateAll();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            xlsxBytes = outputStream.toByteArray();
            outputStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Erase the effort for the row
    private void eraseEffort(int rowId, Row row) {
        // Skip the first row (header)
        if (rowId > 0) {
            // Erase the row to make sure we don't have any old data
            for (int colId = colOffset + 1; colId <= colOffset + 31; colId++) {
                Cell cell = row.getCell(colId);
                if (cell != null) {
                    cell.setBlank();
                }
            }
        }
    }

    public void writeFile(File output) throws IOException {
        Files.write(output.toPath(), xlsxBytes);
    }

}
