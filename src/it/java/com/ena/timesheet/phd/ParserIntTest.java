package com.ena.timesheet.phd;

import com.ena.timesheet.IntTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParserIntTest extends IntTest {
    public static final String invoiceMonth = "202303";
    public static final String enaFilePath = "PHD 03 - Mar 2023.xlsx";
    public static final String phdFilePath = "PHD Timesheet 2023-03.xlsx";
    public static final LocalDate march2023 = LocalDate.of(2023, 3, 15);

    @Test
    @DisplayName("Parse a PHD timesheet")
    void parse() throws URISyntaxException {
        File phdFile = new File(findFile(phdFilePath));
        try (InputStream inputStream = new FileInputStream(phdFile)) {
            List<PhdTemplateEntry> entries = new Parser().parseEntries(inputStream);
            for (PhdTemplateEntry entry : entries) {
//                System.out.println(entry.toJson());
                assertEquals(entries.size(), 93);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
