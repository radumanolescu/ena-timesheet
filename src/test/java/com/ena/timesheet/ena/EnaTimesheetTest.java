package com.ena.timesheet.ena;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class EnaTimesheetTest {
    //@Test
    @DisplayName("Parse an ENA timesheet")
    void parse() {
        String filePath = "./src/main/resources/PHD 03 - Mar 2023.xlsx";
        try (InputStream inputStream = new FileInputStream(filePath)) {
            EnaTimesheet enaTimesheet = new EnaTimesheet(LocalDate.of(2023, 3, 15), inputStream);
            List<EnaTsEntry> entries = enaTimesheet.getEntries();
            for (EnaTsEntry entry : entries) {
                System.out.println(entry.toString());
            }
            // assert entries.size() == 60;
            System.out.println("---------- Project entries ----------");
            EnaTimesheet tsWithTotals = enaTimesheet.withTotals();
            List<EnaTsProjectEntry> projectEntries = tsWithTotals.getProjectEntries();
            for (EnaTsProjectEntry projectEntry : projectEntries) {
                System.out.println(projectEntry.toString());
            }
            System.out.println("---------- Effort By Client#Task and Day ----------");
            Map<String, Map<Integer, Double>> effortByClientTaskDay = enaTimesheet.totalHoursByClientTaskDay();
            prettyPrint(effortByClientTaskDay);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    private void prettyPrint(Map<String, Map<Integer, Double>> map) {
        for (Map.Entry<String, Map<Integer, Double>> outerEntry : map.entrySet()) {
            String outerKey = outerEntry.getKey();
            Map<Integer, Double> innerMap = outerEntry.getValue();
            System.out.println(outerKey + ":");
            for (Map.Entry<Integer, Double> innerEntry : innerMap.entrySet()) {
                Integer innerKey = innerEntry.getKey();
                Double value = innerEntry.getValue();
                System.out.println("  " + innerKey + ": " + value);
            }
        }
    }
}
