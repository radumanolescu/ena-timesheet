package com.ena.timesheet.controller;


//import com.ena.timesheet.IntTest;

import com.ena.timesheet.config.AppConfig;
import com.ena.timesheet.dao.PhdTemplateDao;
import com.ena.timesheet.phd.PhdTemplate;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test application back-end flow (functionality) without the front-end.
 */
public class UploadFlowDriver {

    public static final String dateStr = "2023-03-15";
    public static final String yyyyMM = "202303";
    public static final String phdTemplateFileName = "PHD Timesheet 2023-03.xlsx";
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
        File phdTemplateFile = new File(findFile(phdTemplateFileName));
        phdTemplateDao.deleteItem(yyyyMM);
        try (InputStream inputStream = new FileInputStream(phdTemplateFile)) {
            byte[] fileBytes = getBytes(inputStream);
            phdTemplateDao.putItem(yyyyMM, fileBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        File enaTimesheetFile = new File(findFile(enaTimesheetFileName));
        EnaUploadFlow enaUploadFlow = new EnaUploadFlow();
        try (InputStream inputStream = new FileInputStream(enaTimesheetFile)) {
            enaUploadFlow.saveEnaUpdatePhd(dateStr, inputStream, dynamoDBClient);
            byte[] bytes = phdTemplateDao.getItem(yyyyMM);
            PhdTemplate phdTemplate = new PhdTemplate(yyyyMM, bytes);
            double totalHours = phdTemplate.totalHours();
            assertEquals(120.75, totalHours);
            File tempFile = new File("//HIPPIE/SharedByRadu/-/ena-out/PHD Timesheet 2023-03-fromTest.xlsx");
            Files.write(tempFile.toPath(), phdTemplate.getXlsxBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public URI findFile(String fileName) throws URISyntaxException {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(fileName);
        return resource.toURI();
    }

    public byte[] getBytes(InputStream inputStream) {
        byte[] bytes = null;
        try {
            bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
        } catch (Exception e) {
            System.out.println("Error converting InputStream to byte[]");
        }
        return bytes;
    }

}
