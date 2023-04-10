package com.aws.codestar.projecttemplates.phd;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ParserTest {
    //@Test
    @DisplayName("Parse a PHD timesheet")
    void parse() {
        String filePath = "./src/main/resources/PHD Timesheet 2023-03.xlsx";
        try (InputStream inputStream = new FileInputStream(filePath)) {
            List<PhdTemplateEntry> entries = new Parser().parse(inputStream);
            for (PhdTemplateEntry entry : entries) {
                System.out.println(entry.toJson());
                assert entries.size() == 93;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
