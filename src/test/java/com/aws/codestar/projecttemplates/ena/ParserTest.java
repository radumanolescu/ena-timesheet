package com.aws.codestar.projecttemplates.ena;

import org.junit.jupiter.api.DisplayName;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

public class ParserTest {
    //@Test
    @DisplayName("Parse an ENA timesheet")
    void parse() {
        String filePath = "./src/main/resources/PHD 03 - Mar 2023.xlsx";
        try (InputStream inputStream = new FileInputStream(filePath)) {
            List<EnaTimesheetEntry> entries = new Parser(LocalDate.now()).parse(inputStream);
            for (EnaTimesheetEntry entry : entries) {
                System.out.println(entry.toJson());
                assert entries.size() == 60;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
