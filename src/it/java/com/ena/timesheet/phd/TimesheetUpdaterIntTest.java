package com.ena.timesheet.phd;

import com.ena.timesheet.ena.EnaTimesheet;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit test for the timesheet updater functionality.
 */
public class TimesheetUpdaterIntTest {
    public static final String invoiceMonth = "202303";
    public static final String enaFilePath = "PHD 03 - Mar 2023.xlsx";
    public static final String phdFilePath = "PHD Timesheet 2023-03.xlsx";
    public static final LocalDate march2023 = LocalDate.of(2023, 3, 15);

    @Test
    public void testUpdate() throws IOException, URISyntaxException {
        File phdFile = new File(findFile(phdFilePath));
        PhdTemplate phdTemplate = new PhdTemplate(invoiceMonth, phdFile);
        File enaFile = new File(findFile(enaFilePath));
        EnaTimesheet enaTimesheet = new EnaTimesheet(march2023, enaFile);
        phdTemplate.update(enaTimesheet);
        double enaTotalHours = enaTimesheet.totalHours();
        double phdTotalHours = phdTemplate.totalHours();
        assertEquals(enaTotalHours, phdTotalHours);
        File tempFile = new File("/tmp/PhdTimesheet-WithEffort.xlsx");
        Files.write(tempFile.toPath(), phdTemplate.getXlsxBytes());
    }

    private URI findFile(String fileName) throws URISyntaxException {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(fileName);
        return resource.toURI();
    }

}
