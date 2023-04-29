package com.ena.timesheet.controller;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.ena.timesheet.dao.PhdTemplateDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@Controller
public class UploadPhdTemplate {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter yyyyMM = DateTimeFormatter.ofPattern("yyyyMM");

    private final AmazonDynamoDB dynamoDBClient;

    @Autowired
    public UploadPhdTemplate(AmazonDynamoDB dynamoDBClient) {
        this.dynamoDBClient = dynamoDBClient;
    }

    @PostMapping(value = "/phd/upload/phd-template")
    public String uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("date") String dateStr, Model model) {
        try (InputStream inputStream = file.getInputStream()) {
            System.out.println("Controller:Upload " + dateStr + ", " + file.getOriginalFilename() + ", " + file.getSize() + " bytes");
            int yearMonth = getYearMonth(dateStr);
            byte[] fileBytes = getBytes(inputStream);
            PhdTemplateDao dao = new PhdTemplateDao();
            dao.setYyyyMM(yearMonth);
            dao.setFile_bytes(fileBytes);
            dao.save();
            System.out.println("File saved to DynamoDB, bytes: " + fileBytes.length);
        } catch (Exception e) {
            //logger.error("Error uploading file", e);
            System.out.println("Error uploading file");
        }
        return "instructions";
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

    private int getYearMonth(String dateStr) {
        LocalDate tsMonth = LocalDate.parse(dateStr, formatter);
        return Integer.parseInt(yyyyMM.format(tsMonth));
    }
}
