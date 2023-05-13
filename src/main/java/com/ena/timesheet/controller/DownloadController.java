package com.ena.timesheet.controller;

import com.ena.timesheet.dao.PhdDropdownsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.io.ByteArrayInputStream;

@Controller
public class DownloadController {
    private static final String dropdownsFilename = "ena_dropdown.txt";
    private final DynamoDbClient dynamoDBClient;

    @Autowired
    public DownloadController(DynamoDbClient dynamoDBClient) {
        this.dynamoDBClient = dynamoDBClient;
    }

    @GetMapping("/download-phd-dropdowns")
    public ResponseEntity<Resource> phdDropdowns(@RequestParam("yyyyMM") String yyyyMM) {
        try {
            System.out.println("Controller:DownloadDropdowns for " + yyyyMM);
            PhdDropdownsDao ddDao = new PhdDropdownsDao(dynamoDBClient);
            byte[] dropdownBytes = ddDao.getItem(yyyyMM);
            ByteArrayInputStream bais = new ByteArrayInputStream(dropdownBytes);
            Resource resource = new InputStreamResource(bais);
            String attachment = ContentDisposition.attachment().filename(dropdownsFilename).build().toString();
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .header(HttpHeaders.CONTENT_DISPOSITION, attachment)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
