package com.ena.timesheet.ena;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class EnaTimesheetIntTest {
    public static final String filePath = "PHD 03 - Mar 2023.xlsx";
    public static final LocalDate march2023 = LocalDate.of(2023, 3, 15);

    @Test
    @DisplayName("Parse an ENA timesheet")
    void parse() throws URISyntaxException {
        File file = new File(findFile(filePath));
        assertTrue(file.exists());
        try (InputStream inputStream = new FileInputStream(file)) {
            EnaTimesheet enaTimesheet = new EnaTimesheet(march2023, inputStream);
            Assertions.assertEquals(60, enaTimesheet.getEntries().size());
            Assertions.assertEquals(77, enaTimesheet.getEntriesWithTotals().size());
            Map<String, Map<Integer, Double>> hoursByCTD = enaTimesheet.totalHoursByClientTaskDay();
            // prettyPrint(hoursByCTD);
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

    private URI findFile(String fileName) throws URISyntaxException {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(fileName);
        return resource.toURI();
    }
}

