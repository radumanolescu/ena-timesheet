package com.ena.timesheet.controller;

import com.ena.timesheet.dao.PhdDropdownsDao;
import com.ena.timesheet.dao.PhdTemplateDao;
import com.ena.timesheet.phd.PhdTemplate;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.io.IOException;
import java.io.InputStream;

public class PhdUploadFlow extends ControllerBase {
    protected void saveTemplateAndDropdowns(String yearMonth, InputStream inputStream, DynamoDbClient dynamoDBClient) throws IOException {
        byte[] fileBytes = getBytes(inputStream);
        PhdTemplateDao dao = new PhdTemplateDao(dynamoDBClient);
        dao.putItem(yearMonth, fileBytes);
        System.out.println("File saved to DynamoDB, bytes: " + fileBytes.length);
        PhdTemplate phdTemplate = new PhdTemplate(yearMonth, fileBytes);
        byte[] dropdownBytes = phdTemplate.dropdowns();
        PhdDropdownsDao ddDao = new PhdDropdownsDao(dynamoDBClient);
        ddDao.putItem(yearMonth, dropdownBytes);
        System.out.println("Dropdowns saved to DynamoDB, bytes: " + dropdownBytes.length);
    }
}
