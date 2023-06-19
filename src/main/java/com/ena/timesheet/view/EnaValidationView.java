package com.ena.timesheet.view;

import com.ena.timesheet.dao.PhdTemplateDao;
import com.ena.timesheet.ena.EnaTimesheet;
import com.ena.timesheet.ena.EnaTsEntry;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static com.ena.timesheet.util.Time.mmyyFmt;
import static com.ena.timesheet.util.Time.yyyyMMFmt;

public class EnaValidationView {
    public EnaValidationView(DynamoDbClient dynamoDBClient, EnaTimesheet enaTimesheet) {
        this.enaTimesheet = enaTimesheet;
        this.timesheetMonth = enaTimesheet.getTimesheetMonth();
        String invoiceMonth = yyyyMMFmt.format(timesheetMonth);
        PhdTemplateDao phdTemplateDao = new PhdTemplateDao(dynamoDBClient);
        this.phdBytes = phdTemplateDao.getItem(invoiceMonth);
    }

    private EnaTimesheet enaTimesheet;
    private byte[] phdBytes;
    private LocalDate timesheetMonth;

    public String getTitle() {
        String pattern = "ENA Timesheet Validation for %s";
        String monthYear = mmyyFmt.format(timesheetMonth);
        return String.format(pattern, monthYear);
    }

    public String getPhdTemplateStatus() {
        String pattern = "PHD Template:%s available";
        String status = (phdBytes == null) ? "not" : "";
        return String.format(pattern, status);
    }

    public String getEnaTimesheetStatus() {
        String pattern = "ENA Timesheet: received, has %d validation errors";
        int status = enaTimesheet.numValidationErrors();
        return String.format(pattern, status);
    }

    public boolean isValid() {
        return enaTimesheet.isValid();
    }

    public String buttonToShow() {
        String buttonToShow = "";
        if (enaTimesheet.isValid()) {
            // The ENA timesheet is valid.
            // If the PHD template is available, go to the downloads page; otherwise to the upload page.
            return (phdBytes == null) ? "home" : "download";
        } else {
            // The ENA timesheet is not valid. Go to the upload page and upload a corrected version.
            return "upload";
        }
    }

    public List<EnaTsEntry> invalidEntries() {
        return enaTimesheet.getEntries().stream()
                .filter(e -> !e.getError().isEmpty())
                .collect(Collectors.toList());
    }
}
