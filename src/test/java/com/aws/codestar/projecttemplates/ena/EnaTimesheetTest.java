package com.aws.codestar.projecttemplates.ena;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

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
            System.out.println("--------------------");
            List<EnaTsProjectEntry> projectEntries = enaTimesheet.getProjectEntries();
            for (EnaTsProjectEntry projectEntry : projectEntries) {
                System.out.println(projectEntry.toString());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
