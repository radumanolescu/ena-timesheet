package com.ena.timesheet.controller;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import com.ena.timesheet.util.Text;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.io.InputStream;
import java.time.format.DateTimeFormatter;

@Controller
public class UploadEnaTimesheet extends EnaUploadFlow {
    //private static final Logger logger = LoggerFactory.getLogger(UploadEnaTimesheet.class);

    private final DynamoDbClient dynamoDBClient;

    @Autowired
    public UploadEnaTimesheet(DynamoDbClient dynamoDBClient) {
        this.dynamoDBClient = dynamoDBClient;
    }

    @PostMapping(value = "/ena/upload/ena-timesheet")
    public String uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("date") String dateStr, Model model) {
        try (InputStream inputStream = file.getInputStream()) {
            saveEnaUpdatePhd(dateStr, inputStream, dynamoDBClient);
            prepareModelForDisplay(dateStr, model);
            return "download";
        } catch (Exception e) {
            //logger.error("Error uploading file", e);
            System.out.println("Error uploading file");
            e.printStackTrace();
            String stackTrace = Text.stackFormatter2(e);
            model.addAttribute("stackTrace", stackTrace);
            return "error-ena";
        }
    }

    private void prepareModelForDisplay(String dateStr, Model model) {
        model.addAttribute("monthYear", getMonthYear(dateStr));
        model.addAttribute("yyyyMM", getYearMonth(dateStr));
    }

}
