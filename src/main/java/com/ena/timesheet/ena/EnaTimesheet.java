package com.ena.timesheet.ena;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class EnaTimesheet {

    public EnaTimesheet(LocalDate timesheetMonth) {
        this.timesheetMonth = timesheetMonth;
    }

    public EnaTimesheet(LocalDate timesheetMonth, InputStream inputStream) {
        this.timesheetMonth = timesheetMonth;
        int sheetIndex = 0; // Assume it's the first sheet
        List<EnaTsEntry> inputEntries = parseEntries(inputStream, sheetIndex);
        sortByDayProjectId(inputEntries);
        reindexEntries(inputEntries);
        List<EnaTsEntry> totalEntries = weeklyTotals(inputEntries);
        this.enaTsEntries.addAll(inputEntries);
        this.enaTsEntries.addAll(totalEntries);
        sortByEntryId(this.enaTsEntries);
        this.projectEntries.addAll(getProjectEntries(inputEntries));
    }

    /**
     * Creates an enriched timesheet with the original entries and total entries (by week, and overall).
     * The original (input) entries are shared between the original and the enriched timesheet.
     * This should be ok, as the original entries are not modified except for the entryId.
     * The enriched timesheet contains the original entries and the total entries.
     * */
    public EnaTimesheet withTotals() {
//        EnaTimesheet enaTimesheet = new EnaTimesheet(this.timesheetMonth);
//        enaTimesheet.enaTsEntries.addAll(this.enaTsEntries);
//        sortByDayProjectId(enaTimesheet.enaTsEntries);
//        reindexEntries(enaTimesheet.enaTsEntries);
//        List<EnaTsEntry> totalEntries = weeklyTotals(enaTimesheet.enaTsEntries);
//        List<EnaTsProjectEntry> projectEntries = getProjectEntries(enaTimesheet.enaTsEntries);
//        enaTimesheet.enaTsEntries.addAll(totalEntries);
//        sortByEntryId(enaTimesheet.enaTsEntries);
//        enaTimesheet.projectEntries.addAll(projectEntries);
        return this; // enaTimesheet; // ToDo: return enaTimesheet
    }

    private final LocalDate timesheetMonth;

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
        Collections.sort(entries, Comparator.comparing(EnaTsEntry::sortKey));
    }

    private void sortByEntryId(List<EnaTsEntry> entries) {
        Collections.sort(entries, Comparator.comparing(o -> o.entryId));
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
            EnaTsWeekTotalEntry weekTotal = new EnaTsWeekTotalEntry(timesheetMonth, maxEntryId + 0.1f,
                    "Total hours: ", totalHours,
                    "Total for week:", totalCharge);
            hoursForMonth += totalHours;
            chargeForMonth += totalCharge;
            maxEntryIdForMonth = Math.max(maxEntryIdForMonth, maxEntryId);
            totalsByWeek.add(weekTotal);
            totalsByWeek.add(new EnaTsWeekBlankEntry(timesheetMonth, maxEntryId + 0.2f));
            totalsByWeek.add(new EnaTsWeekBlankEntry(timesheetMonth, maxEntryId + 0.3f));
        }
        totalsByWeek.add(new EnaTsWeekTotalEntry(timesheetMonth, maxEntryIdForMonth + 0.4f,
                "Monthly hours:", hoursForMonth,
                "Total consulting fees for month:", chargeForMonth));
        totalsByWeek.add(new EnaTsWeekBlankEntry(timesheetMonth, maxEntryIdForMonth + 0.5f));
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
            String projectActivity = entry.matchKey();
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


    public Map<String, Map<Integer, Double>> totalHoursByClientTaskDay() {
        return enaTsEntries.stream().collect(
                Collectors.groupingBy(
                        EnaTsEntry::matchKey,
                        Collectors.groupingBy(
                                EnaTsEntry::getDay,
                                Collectors.summingDouble(effort -> effort.getHours())
                        )
                )
        );
    }
    /*


    public Map<String, Map<Integer, Float>> getTotalHours(){
        return enaTsEntries.stream()
                .collect(Collectors.groupingBy(EnaTsEntry::matchKey, Collectors.groupingBy(EnaTsEntry::getDay, Collectors.summingDouble(effort -> effort.getHours()))));
    }

    public Map<String, Map<Integer, Double>> getTotalHours(){
        return enaTsEntries.stream()
                .collect(Collectors.groupingBy(EnaTsEntry::matchKey),
                        Collectors.summingDouble(EnaTsEntry::getHours)));
    }
    */

}
