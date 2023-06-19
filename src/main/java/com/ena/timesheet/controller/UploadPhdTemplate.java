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

import static com.ena.timesheet.util.Time.getMonthYear;
import static com.ena.timesheet.util.Time.getYearMonth;

@Controller
public class UploadPhdTemplate extends PhdUploadFlow {

    private final DynamoDbClient dynamoDBClient;

    @Autowired
    public UploadPhdTemplate(DynamoDbClient dynamoDBClient) {
        this.dynamoDBClient = dynamoDBClient;
    }

    @PostMapping(value = "/phd/upload/phd-template")
    public String uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("date") String dateStr, Model model) {
        try (InputStream inputStream = file.getInputStream()) {
            String yyyyMM = getYearMonth(dateStr);
            saveTemplateAndDropdowns(yyyyMM, inputStream, dynamoDBClient);
            prepareModelForDisplay(dateStr, model);
            return "download";
        } catch (Exception e) {
            //logger.error("Error uploading file", e);
            System.out.println("Error uploading file");
            e.printStackTrace();
            String stackTrace = Text.stackFormatter2(e);
            model.addAttribute("stackTrace", stackTrace);
            return "error-phd";
        }
    }

    private void prepareModelForDisplay(String dateStr, Model model) {
        model.addAttribute("monthYear", getMonthYear(dateStr));
        model.addAttribute("yyyyMM", getYearMonth(dateStr));
    }
}
