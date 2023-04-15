package com.aws.codestar.projecttemplates.ena;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

public class XTimesheetTest {
    @Test
    @DisplayName("Parse an ENA timesheet")
    void parse() {
        String filePath = "./src/main/resources/PHD 03 - Mar 2023.xlsx";
        try (InputStream inputStream = new FileInputStream(filePath)) {
            XTimesheet xTimesheet = new XTimesheet(LocalDate.of(2023, 3, 15), inputStream);
            List<XEntry> entries = xTimesheet.getEntries();
            for (XEntry entry : entries) {
                System.out.println(entry.toString());
            }
            // assert entries.size() == 60;
            System.out.println("--------------------");
            List<XProjectEntry> projectEntries = xTimesheet.getProjectEntries();
            for (XProjectEntry projectEntry : projectEntries) {
                System.out.println(projectEntry.toString());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
