package com.ena.timesheet.controller;

import com.ena.timesheet.dao.EnaTimesheetDao;
import com.ena.timesheet.dao.PhdTemplateDao;
import com.ena.timesheet.ena.EnaTimesheet;
import com.ena.timesheet.phd.PhdTemplate;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;

public class EnaUploadFlow extends ControllerBase {
    protected void saveEnaUpdatePhd(String dateStr, InputStream inputStream, DynamoDbClient dynamoDBClient) throws IOException {
        LocalDate tsMonth = LocalDate.parse(dateStr, yyyyMMdd);
        byte[] fileBytes = getBytes(inputStream);
        EnaTimesheet enaTimesheet = new EnaTimesheet(tsMonth, fileBytes);
        saveEnaTimesheet(tsMonth, fileBytes, dynamoDBClient);
        updatePhdTemplate(tsMonth, enaTimesheet, dynamoDBClient);
    }

    protected void saveEnaTimesheet(LocalDate tsMonth, byte[] enaTimesheet, DynamoDbClient dynamoDBClient) {
        String invoiceMonth = yyyyMM.format(tsMonth);
        EnaTimesheetDao enaTimesheetDao = new EnaTimesheetDao(dynamoDBClient);
        enaTimesheetDao.putItem(invoiceMonth, enaTimesheet);
    }

    protected void updatePhdTemplate(LocalDate tsMonth, EnaTimesheet enaTimesheet, DynamoDbClient dynamoDBClient) throws IOException {
        PhdTemplateDao phdTemplateDao = new PhdTemplateDao(dynamoDBClient);
        String invoiceMonth = yyyyMM.format(tsMonth);
        byte[] phdBytes = phdTemplateDao.getItem(invoiceMonth);
        if (phdBytes != null) {
            PhdTemplate phdTemplate = new PhdTemplate(invoiceMonth, phdBytes);
            phdTemplate.update(enaTimesheet);
            phdTemplateDao.putItem(invoiceMonth, phdTemplate.getXlsxBytes());
        }
    }

}
