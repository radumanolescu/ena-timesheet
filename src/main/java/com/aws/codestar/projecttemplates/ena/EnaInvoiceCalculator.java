package com.aws.codestar.projecttemplates.ena;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

public class EnaInvoiceCalculator {
    public static Invoice calculateInvoice(LocalDate tsMonth, InputStream inputStream) {
        Invoice invoice = new Invoice();
        Parser parser = new Parser(tsMonth);
        try {
            List<EnaTimesheetEntry> timesheetEntries = parser.parse(inputStream);
        }
        catch (Exception e) {
            System.out.println("Error uploading file");
            e.printStackTrace();
        }
        return invoice;
    }
}
