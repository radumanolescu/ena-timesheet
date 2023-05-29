package com.ena.timesheet.ena;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingDouble;

public class EnaTimesheet {
    public EnaTimesheet(LocalDate timesheetMonth, InputStream inputStream) {
        this.timesheetMonth = timesheetMonth;
        parseSortReindex(inputStream);
    }

    public EnaTimesheet(LocalDate timesheetMonth, File enaTimesheetFile) throws IOException {
        this.timesheetMonth = timesheetMonth;
        try (InputStream inputStream = new FileInputStream(enaTimesheetFile)) {
            parseSortReindex(inputStream);
        } catch (Exception e) {
            throw e;
        }
    }

    public EnaTimesheet(LocalDate timesheetMonth, byte[] fileBytes) throws IOException {
        this.timesheetMonth = timesheetMonth;
        try (ByteArrayInputStream bis = new ByteArrayInputStream(fileBytes)) {
            parseSortReindex(bis);
        } catch (Exception e) {
            throw e;
        }
    }

    private void parseSortReindex(InputStream inputStream){
        int sheetIndex = 0; // Assume it's the first sheet
        List<EnaTsEntry> inputEntries = parseEntries(inputStream, sheetIndex);
        sortByDayProjectId(inputEntries);
        reindexEntries(inputEntries);
        this.enaTsEntries.addAll(inputEntries);
        this.projectEntries.addAll(getProjectEntries(inputEntries));
    }

    /**
     * Add pseudo-entries to a separate list of entries to represent the weekly totals,
     * plus empty lines for invoice formatting.
     */
    public List<EnaTsEntry> getEntriesWithTotals() {
        List<EnaTsEntry> entriesWithTotals = new ArrayList<>(enaTsEntries);
        List<EnaTsEntry> totalEntries = weeklyTotals(enaTsEntries);
        entriesWithTotals.addAll(totalEntries);
        sortByEntryId(entriesWithTotals);
        return entriesWithTotals;
    }

    private final LocalDate timesheetMonth;

    /**
     * Entries parsed from the ENA timesheet, without any totals.
     */
    public List<EnaTsEntry> getEntries() {
        return enaTsEntries;
    }

    private final List<EnaTsEntry> enaTsEntries = new ArrayList<>();

    public List<EnaTsProjectEntry> getProjectEntries() {
        return projectEntries;
    }

    private final List<EnaTsProjectEntry> projectEntries = new ArrayList<>();

    private List<EnaTsEntry> parseEntries(InputStream inputStream, int sheetIndex) {
        List<EnaTsEntry> enaEntries = new ArrayList<>();
        try {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(sheetIndex);
            int lineId = 0;
            for (Row row : sheet) {
                EnaTsEntry entry = new EnaTsEntry(lineId++, timesheetMonth, row);
                if (entry.getValidCells() > 5) {
                    enaEntries.add(entry);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return enaEntries;
    }

    private void reindexEntries(List<EnaTsEntry> entries) {
        int lineId = 0;
        for (EnaTsEntry entry : entries) {
            entry.setEntryId((float) lineId++);
        }
    }

    private void sortByDayProjectId(List<EnaTsEntry> entries) {
        entries.sort(Comparator.comparing(EnaTsEntry::sortKey));
    }

    private void sortByEntryId(List<EnaTsEntry> entries) {
        entries.sort(Comparator.comparing(o -> o.entryId));
    }

    private List<EnaTsEntry> weeklyTotals(List<EnaTsEntry> entries) {
        List<EnaTsEntry> totalsByWeek = new ArrayList<>();
        Map<Integer, WeeklySummary> weeklySummaries = weeklySummary(entries);
        float hoursForMonth = 0.0f;
        float chargeForMonth = 0.0f;
        float maxEntryIdForMonth = 0.0f;
        for (Map.Entry<Integer, WeeklySummary> entry : weeklySummaries.entrySet()) {
            float totalHours = entry.getValue().totalHours;
            float maxEntryId = entry.getValue().maxEntryId;
            float totalCharge = entry.getValue().totalCharge;
            EnaTsWeekTotalEntry weekTotal = new EnaTsWeekTotalEntry(maxEntryId + 0.1f,
                    "Total hours: ", totalHours,
                    "Total for week:", totalCharge);
            hoursForMonth += totalHours;
            chargeForMonth += totalCharge;
            maxEntryIdForMonth = Math.max(maxEntryIdForMonth, maxEntryId);
            totalsByWeek.add(weekTotal);
            totalsByWeek.add(new EnaTsWeekBlankEntry(maxEntryId + 0.2f));
            totalsByWeek.add(new EnaTsWeekBlankEntry(maxEntryId + 0.3f));
        }
        totalsByWeek.add(new EnaTsWeekTotalEntry(maxEntryIdForMonth + 0.4f,
                "Monthly hours:", hoursForMonth,
                "Total consulting fees for month:", chargeForMonth));
        totalsByWeek.add(new EnaTsWeekBlankEntry(maxEntryIdForMonth + 0.5f));
        return totalsByWeek;
    }

    /**
     * maxEntryId: for each week, compute the maximum entryId for the week. This is used to know how to add a total entry right after the last entry of the week.
     */
    private Map<Integer, WeeklySummary> weeklySummary(List<EnaTsEntry> entries) {
        Map<Integer, WeeklySummary> summaryByWeek = new HashMap<>();
        for (EnaTsEntry entry : entries) {
            int week = entry.getWeekOfMonth();
            WeeklySummary summary = summaryByWeek.getOrDefault(week, new WeeklySummary());
            summary.totalHours += entry.hours;
            summary.maxEntryId = Math.max(summary.maxEntryId, entry.entryId);
            summary.totalCharge += entry.charge;
            summaryByWeek.put(week, summary);
        }
        return summaryByWeek;
    }

    static class WeeklySummary {
        Float totalHours = 0.0f;
        Float maxEntryId = 0.0f;
        Float totalCharge = 0.0f;
    }

    private List<EnaTsProjectEntry> getProjectEntries(List<EnaTsEntry> entries) {
        List<EnaTsProjectEntry> projectEntries = new ArrayList<>();
        float totalHours = 0.0f;
        Map<String, EnaTsProjectEntry> projectEntryMap = new HashMap<>();
        for (EnaTsEntry entry : entries) {
            String projectActivity = entry.projectActivity();
            EnaTsProjectEntry projectEntry = projectEntryMap.getOrDefault(projectActivity, new EnaTsProjectEntry(entry.getProjectId(), entry.getActivity(), 0.0f));
            projectEntry.setHours(projectEntry.hours + entry.hours);
            totalHours += entry.hours;
            projectEntryMap.put(projectActivity, projectEntry);
        }
        for (Map.Entry<String, EnaTsProjectEntry> entry : projectEntryMap.entrySet()) {
            projectEntries.add(entry.getValue());
        }
        Collections.sort(projectEntries);
        projectEntries.add(new EnaTsProjectEntry("Total hours:", "", totalHours));
        return projectEntries;
    }

    public double totalHours() {
        return enaTsEntries.stream().mapToDouble(EnaTsEntry::getHours).sum();
    }

    public Map<String, Map<Integer, Double>> totalHoursByClientTaskDay() {
        return enaTsEntries.stream().collect(
                groupingBy(
                        EnaTsEntry::projectActivity,
                        groupingBy(
                                EnaTsEntry::getDay,
                                summingDouble(EnaTsEntry -> EnaTsEntry.hours)
                        )
                )
        );
    }
}
