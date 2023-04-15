package com.aws.codestar.projecttemplates.ena;

import com.aws.codestar.projecttemplates.xl.ExcelParser;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;

public class XTimesheet extends ExcelParser {
    public XTimesheet(LocalDate timesheetMonth, InputStream inputStream) {
        this.timesheetMonth = timesheetMonth;
        int sheetIndex = 0; // Assume it's the first sheet
        List<XEntry> inputEntries = parseEntries(inputStream, sheetIndex);
        List<XEntry> totalEntries = weeklyTotals(inputEntries);
        this.entries.addAll(inputEntries);
        this.entries.addAll(totalEntries);
        Collections.sort(this.entries);
        this.projectEntries.addAll(getProjectEntries(inputEntries));
    }

    private LocalDate timesheetMonth;

    public List<XEntry> getEntries() {
        return entries;
    }

    private List<XEntry> entries = new ArrayList<>();

    public List<XProjectEntry> getProjectEntries() {
        return projectEntries;
    }

    private List<XProjectEntry> projectEntries = new ArrayList<>();

    private List<XEntry> parseEntries(InputStream inputStream, int sheetIndex) {
        List<XEntry> enaEntries = new ArrayList<>();
        try {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(sheetIndex);
            int lineId = 0;
            for (Row row : sheet) {
                XEntry entry = new XEntry(lineId++, timesheetMonth, row);
                if (entry.getValidCells() > 5) {
                    enaEntries.add(entry);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return enaEntries;
    }

    private List<XEntry> weeklyTotals(List<XEntry> entries) {
        List<XEntry> totalsByWeek = new ArrayList<>();
        Map<Integer, WeeklySummary> weeklySummaries = weeklySummary(entries);
        float hoursForMonth = 0.0f;
        float chargeForMonth = 0.0f;
        float maxEntryIdForMonth = 0.0f;
        for (Map.Entry<Integer, WeeklySummary> entry : weeklySummaries.entrySet()) {
            float totalHours = entry.getValue().totalHours;
            float maxEntryId = entry.getValue().maxEntryId;
            float totalCharge = entry.getValue().totalCharge;
            XWeekTotalEntry weekTotal = new XWeekTotalEntry(maxEntryId + 0.1f,
                    "Total hours: ", totalHours,
                    "Total for week:", totalCharge);
            hoursForMonth += totalHours;
            chargeForMonth += totalCharge;
            maxEntryIdForMonth = Math.max(maxEntryIdForMonth, maxEntryId);
            totalsByWeek.add(weekTotal);
            totalsByWeek.add(new XWeekBlankEntry(maxEntryId + 0.2f));
            totalsByWeek.add(new XWeekBlankEntry(maxEntryId + 0.3f));
        }
        totalsByWeek.add(new XWeekTotalEntry(maxEntryIdForMonth + 0.4f,
                "Monthly hours:", hoursForMonth,
                "Total consulting fees for month:", chargeForMonth));
        return totalsByWeek;
    }

    /**
     * maxEntryId: for each week, compute the maximum entryId for the week. This is used to know how to add a total entry right after the last entry of the week.
     */
    private Map<Integer, WeeklySummary> weeklySummary(List<XEntry> entries) {
        Map<Integer, WeeklySummary> summaryByWeek = new HashMap<>();
        for (XEntry entry : entries) {
            int week = entry.getWeekOfMonth();
            WeeklySummary summary = summaryByWeek.getOrDefault(week, new WeeklySummary());
            summary.totalHours += entry.hours;
            summary.maxEntryId = Math.max(summary.maxEntryId, entry.entryId);
            summary.totalCharge += entry.charge;
            summaryByWeek.put(week, summary);
        }
        return summaryByWeek;
    }

    class WeeklySummary {
        Float totalHours = 0.0f;
        Float maxEntryId = 0.0f;
        Float totalCharge = 0.0f;
    }

    private List<XProjectEntry> getProjectEntries(List<XEntry> entries) {
        List<XProjectEntry> projectEntries = new ArrayList<>();
        float totalHours = 0.0f;
        Map<String, XProjectEntry> projectEntryMap = new HashMap<>();
        for (XEntry entry : entries) {
            String projectActivity = entry.getProjectId() + "#" + entry.getActivity();
            XProjectEntry projectEntry = projectEntryMap.getOrDefault(projectActivity, new XProjectEntry(entry.getProjectId(), entry.getActivity(), 0.0f));
            projectEntry.setHours(projectEntry.hours + entry.hours);
            totalHours += entry.hours;
            projectEntryMap.put(projectActivity, projectEntry);
        }
        for (Map.Entry<String, XProjectEntry> entry : projectEntryMap.entrySet()) {
            projectEntries.add(entry.getValue());
        }
        Collections.sort(projectEntries);
        projectEntries.add(new XProjectEntry("Total hours:", "", totalHours));
        return projectEntries;
    }
}
