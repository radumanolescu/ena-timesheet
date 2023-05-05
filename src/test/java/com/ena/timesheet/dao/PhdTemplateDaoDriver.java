package com.ena.timesheet.dao;

import com.ena.timesheet.config.AppConfig;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.io.InputStream;
import java.net.URISyntaxException;

public class PhdTemplateDaoDriver {

    private static final String PHD_TEMPLATE_FILE = "PHD Timesheet 2023-03.xlsx";
    private static final String PHD_TEMPLATE_MONTH = "202303";

    private final PhdTemplateDao phdTemplateDao;

    public PhdTemplateDaoDriver(PhdTemplateDao phdTemplateDao) {
        this.phdTemplateDao = phdTemplateDao;
    }

    public static void main(String[] args) throws URISyntaxException {
        AppConfig appConfig = new AppConfig();
        DynamoDbClient dynamoDBClient = appConfig.getClient("dev/local");
        PhdTemplateDao phdTemplateDao = new PhdTemplateDao(dynamoDBClient);
        PhdTemplateDaoDriver driver = new PhdTemplateDaoDriver(phdTemplateDao);
        driver.uploadTemplate(PHD_TEMPLATE_FILE, PHD_TEMPLATE_MONTH, phdTemplateDao);
        byte[] fileBytes = driver.downloadTemplate(PHD_TEMPLATE_MONTH, phdTemplateDao);
        System.out.println("File retrieved from DynamoDB, bytes: " + fileBytes.length);
    }

    private byte[] downloadTemplate(String s, PhdTemplateDao phdTemplateDao) {
        return phdTemplateDao.getItem(s);
    }

    private void uploadTemplate(String fileName, String yyyyMM, PhdTemplateDao phdTemplateDao) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName)) {
            byte[] fileBytes = getBytes(inputStream);
            phdTemplateDao.putItem(yyyyMM, fileBytes);
            System.out.println("File saved to DynamoDB, bytes: " + fileBytes.length);
        } catch (Exception e) {
            //logger.error("Error uploading file", e);
            System.out.println("Error uploading file");
            e.printStackTrace();
        }
    }

    private byte[] getBytes(InputStream inputStream) {
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
