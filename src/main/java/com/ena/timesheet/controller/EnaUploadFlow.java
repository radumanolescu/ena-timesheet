package com.ena.timesheet.controller;

import com.ena.timesheet.dao.EnaTimesheetDao;
import com.ena.timesheet.dao.PhdTemplateDao;
import com.ena.timesheet.ena.EnaTimesheet;
import com.ena.timesheet.phd.PhdTemplate;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Set;

import static com.ena.timesheet.util.IOUtil.getBytes;
import static com.ena.timesheet.util.Time.isoLocalDateFmt;
import static com.ena.timesheet.util.Time.yyyyMMFmt;

public class EnaUploadFlow  {

    /**
     * @param dateStr        - Date string in format yyyy-MM-dd
     * @param inputStream    - InputStream of an ENA Timesheet xlsx file to be uploaded
     * @param dynamoDBClient - DynamoDB client
     */
    protected EnaTimesheet saveEnaUpdatePhd(String dateStr, InputStream inputStream, DynamoDbClient dynamoDBClient) throws IOException {
        byte[] enaBytes = getBytes(inputStream);
        return saveEnaUpdatePhd(dateStr, enaBytes, dynamoDBClient);
    }

    protected EnaTimesheet saveEnaUpdatePhd(String dateStr, byte[] enaBytes, DynamoDbClient dynamoDBClient) throws IOException {
        LocalDate tsMonth = LocalDate.parse(dateStr, isoLocalDateFmt);
        String invoiceMonth = yyyyMMFmt.format(tsMonth);
        EnaTimesheet enaTimesheet = new EnaTimesheet(tsMonth, enaBytes);
        // Find the PHD template for the same month and update it, if the ENA timesheet is valid
        PhdTemplateDao phdTemplateDao = new PhdTemplateDao(dynamoDBClient);
        byte[] phdBytes = phdTemplateDao.getItem(invoiceMonth);
        if (phdBytes != null) {
            PhdTemplate phdTemplate = new PhdTemplate(invoiceMonth, phdBytes);
            Set<String> clientTaskSet = phdTemplate.clientTaskSet();
            if (enaTimesheet.isValidAllClientTasks(clientTaskSet)) {
                phdTemplate.update(enaTimesheet);
                phdTemplateDao.putItem(invoiceMonth, phdTemplate.getXlsxBytes());
            }
        }
        EnaTimesheetDao enaTimesheetDao = new EnaTimesheetDao(dynamoDBClient);
        enaTimesheetDao.putItem(invoiceMonth, enaTimesheet.getXlsxBytes());
        return enaTimesheet;
    }

}
