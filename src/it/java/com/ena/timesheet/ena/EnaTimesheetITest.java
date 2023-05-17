package com.ena.timesheet.ena;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingDouble;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EnaTimesheetITest {
    public static String filePath = "./src/it/resources/PHD 03 - Mar 2023.xlsx";

    @Test
    @DisplayName("Parse an ENA timesheet")
    void parse() {
        File file = new File(filePath);
        assertTrue(file.exists());
        LocalDate invoiceMonth = LocalDate.of(2023, 3, 15);
        try (InputStream inputStream = new FileInputStream(filePath)) {
            EnaTimesheet enaTimesheet = new EnaTimesheet(invoiceMonth, inputStream);
            assertEquals(enaTimesheet.getEntries().size(), 60);
            Map<String, Map<Integer, Double>> hoursByCTD = totalHoursByClientTaskDay(enaTimesheet.getEntries());
            prettyPrint(hoursByCTD);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, Map<Integer, Double>> totalHoursByClientTaskDay(List<EnaTsEntry> enaTsEntries) {
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

