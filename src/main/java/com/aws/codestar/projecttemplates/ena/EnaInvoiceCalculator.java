package com.aws.codestar.projecttemplates.ena;

import java.io.InputStream;
import java.util.List;

public class EnaInvoiceCalculator {
    public static Invoice calculateInvoice(InputStream inputStream) {
        Invoice invoice = new Invoice();
        Parser parser = new Parser();
        try {
            List<EnaTimesheetEntry> timesheetEntries = parser.parse(202303, inputStream);
        }
        catch (Exception e) {
            System.out.println("Error uploading file");
            e.printStackTrace();
        }
        return invoice;
    }
}
