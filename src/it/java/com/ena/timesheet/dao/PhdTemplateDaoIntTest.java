package com.ena.timesheet.dao;

import com.ena.timesheet.IntTest;
import com.ena.timesheet.config.AppConfig;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PhdTemplateDaoIntTest extends IntTest {
    public static final String invoiceMonth = "202303";
    public static final String enaFilePath = "PHD 03 - Mar 2023.xlsx";
    public static final String phdFilePath = "PHD Timesheet 2023-03.xlsx";
    public static final LocalDate march2023 = LocalDate.of(2023, 3, 15);

    AppConfig appConfig = new AppConfig();
    DynamoDbClient dynamoDBClient = appConfig.getClient("dev/local");
    PhdTemplateDao phdTemplateDao = new PhdTemplateDao(dynamoDBClient);

    public PhdTemplateDaoIntTest() throws URISyntaxException {
    }

    @Test
    public void testUploadDownload() throws URISyntaxException, IOException {
        File phdFile = new File(findFile(phdFilePath));
        byte[] bytesUpld = upload(phdFile, invoiceMonth, phdTemplateDao);
        byte[] bytesDnld = download(invoiceMonth, phdTemplateDao);
        assertTrue(Arrays.equals(bytesUpld, bytesDnld));
    }

    private byte[] download(String s, PhdTemplateDao phdTemplateDao) {
        return phdTemplateDao.getItem(s);
    }

    private byte[] upload(File file, String yyyyMM, PhdTemplateDao phdTemplateDao) throws IOException {
        byte[] fileBytes = Files.readAllBytes(file.toPath());
        phdTemplateDao.putItem(yyyyMM, fileBytes);
        return fileBytes;
    }

}
