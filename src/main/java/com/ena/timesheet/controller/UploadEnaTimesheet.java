package com.ena.timesheet.controller;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import com.ena.timesheet.ena.EnaTimesheet;
import com.ena.timesheet.ena.EnaTsEntry;
import com.ena.timesheet.ena.EnaTsProjectEntry;
import com.ena.timesheet.util.Text;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class UploadEnaTimesheet extends EnaUploadFlow {
    //private static final Logger logger = LoggerFactory.getLogger(UploadEnaTimesheet.class);
    private static final DateTimeFormatter mmyyFmt = DateTimeFormatter.ofPattern("MMMM yyyy");
    private static final DateTimeFormatter mdyFmt = DateTimeFormatter.ofPattern("MMMM d, yyyy");

    private final DynamoDbClient dynamoDBClient;

    @Autowired
    public UploadEnaTimesheet(DynamoDbClient dynamoDBClient) {
        this.dynamoDBClient = dynamoDBClient;
    }

    @PostMapping(value = "/ena/upload/ena-timesheet")
    public String uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("date") String dateStr, Model model) {
        try (InputStream inputStream = file.getInputStream()) {
            saveEnaUpdatePhd(dateStr, inputStream, dynamoDBClient);
            model.addAttribute("invoiceMonth", getYearMonth(dateStr));
            return "download"; // ""invoice";
        } catch (Exception e) {
            //logger.error("Error uploading file", e);
            System.out.println("Error uploading file");
            e.printStackTrace();
            String stackTrace = Text.stackFormatter2(e);
            model.addAttribute("stackTrace", stackTrace);
            return "error-ena";
        }
    }

    private void prepareModelForDisplay(LocalDate tsMonth, EnaTimesheet enaTimesheet, Model model) {
        List<EnaTsEntry> bdws = enaTimesheet.getEntriesWithTotals();
        List<EnaTsProjectEntry> bps = enaTimesheet.getProjectEntries();
        model.addAttribute("invoiceMonth", mmyyFmt.format(tsMonth));
        model.addAttribute("invoiceDate", mdyFmt.format(LocalDate.now()));
        model.addAttribute("invoiceByDayAndWeek", bdws);
        model.addAttribute("invoiceByProject", bps);
    }

}
