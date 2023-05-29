package com.ena.timesheet.controller;


import com.ena.timesheet.IntTest;
import com.ena.timesheet.config.AppConfig;
import com.ena.timesheet.dao.PhdTemplateDao;
import com.ena.timesheet.phd.PhdTemplate;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test application back-end flow (functionality) without the front-end.
 */
public class UploadFlowDriver extends IntTest {

    public static final String dateStr = "2023-03-15";
    public static final String yyyyMM = "202303";
    ;
    public static final String enaTimesheetFileName = "PHD 03 - Mar 2023.xlsx";
    public static final LocalDate march2023 = LocalDate.of(2023, 3, 15);
    AppConfig appConfig = new AppConfig();
    DynamoDbClient dynamoDBClient = appConfig.getClient("dev/local");
    PhdTemplateDao phdTemplateDao = new PhdTemplateDao(dynamoDBClient);

    public UploadFlowDriver() throws URISyntaxException {
    }

    public static void main(String[] args) throws URISyntaxException, IOException {
        UploadFlowDriver uploadFlowDriver = new UploadFlowDriver();
        uploadFlowDriver.testFlow();
    }

    public void testFlow() throws URISyntaxException {
        File enaTimesheetFile = new File(findFile(enaTimesheetFileName));
        EnaUploadFlow enaUploadFlow = new EnaUploadFlow();
        try (InputStream inputStream = new FileInputStream(enaTimesheetFile)) {
            enaUploadFlow.saveEnaUpdatePhd(dateStr, inputStream, dynamoDBClient);
            byte[] bytes = phdTemplateDao.getItem(yyyyMM);
            PhdTemplate phdTemplate = new PhdTemplate(yyyyMM, bytes);
            double totalHours = phdTemplate.totalHours();
            assertEquals(120.75, totalHours);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
