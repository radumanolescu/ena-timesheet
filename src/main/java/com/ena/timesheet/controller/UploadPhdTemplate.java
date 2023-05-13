package com.ena.timesheet.controller;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import com.ena.timesheet.dao.PhdDropdownsDao;
import com.ena.timesheet.dao.PhdTemplateDao;
import com.ena.timesheet.phd.PhdTemplate;
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

@Controller
public class UploadPhdTemplate {
    private static final DateTimeFormatter yyyyMMdd = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter yyyyMM = DateTimeFormatter.ofPattern("yyyyMM");
    private static final DateTimeFormatter mmyyFmt = DateTimeFormatter.ofPattern("MMMM yyyy");

    private final DynamoDbClient dynamoDBClient;

    @Autowired
    public UploadPhdTemplate(DynamoDbClient dynamoDBClient) {
        this.dynamoDBClient = dynamoDBClient;
    }

    @PostMapping(value = "/phd/upload/phd-template")
    public String uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("date") String dateStr, Model model) {
        try (InputStream inputStream = file.getInputStream()) {
            System.out.println("Controller:Upload " + dateStr + ", " + file.getOriginalFilename() + ", " + file.getSize() + " bytes");
            String yearMonth = getYearMonth(dateStr);

            byte[] fileBytes = getBytes(inputStream);

            PhdTemplateDao dao = new PhdTemplateDao(dynamoDBClient);
            dao.putItem(yearMonth, fileBytes);
            System.out.println("File saved to DynamoDB, bytes: " + fileBytes.length);

            PhdTemplate phdTemplate = new PhdTemplate(yearMonth, fileBytes);

            byte[] dropdownBytes = phdTemplate.dropdowns();

            PhdDropdownsDao ddDao = new PhdDropdownsDao(dynamoDBClient);
            ddDao.putItem(yearMonth, dropdownBytes);
            System.out.println("Dropdowns saved to DynamoDB, bytes: " + dropdownBytes.length);

            model.addAttribute("invoiceMonth", yearMonth);
            return "download";
        } catch (Exception e) {
            //logger.error("Error uploading file", e);
            System.out.println("Error uploading file");
            e.printStackTrace();
            return "error-phd";
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

    private String getYearMonth(String dateStr) {
        LocalDate tsMonth = LocalDate.parse(dateStr, yyyyMMdd);
        return "" + Integer.parseInt(yyyyMM.format(tsMonth));
    }

    private String getMonthYear(String dateStr) {
        LocalDate tsMonth = LocalDate.parse(dateStr, yyyyMMdd);
        return mmyyFmt.format(tsMonth);
    }
}
