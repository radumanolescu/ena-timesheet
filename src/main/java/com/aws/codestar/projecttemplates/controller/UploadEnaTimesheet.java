package com.aws.codestar.projecttemplates.controller;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import com.aws.codestar.projecttemplates.ena.EnaTimesheet;
import com.aws.codestar.projecttemplates.ena.EnaTsEntry;
import com.aws.codestar.projecttemplates.ena.EnaTsProjectEntry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class UploadEnaTimesheet {
    //private static final Logger logger = LoggerFactory.getLogger(UploadEnaTimesheet.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter mmyyFmt = DateTimeFormatter.ofPattern("MMMM yyyy");
    private static final DateTimeFormatter mdyFmt = DateTimeFormatter.ofPattern("MMMM d, yyyy");

    @PostMapping(value = "/ena/upload/ena-timesheet")
    public String uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("date") String dateStr, Model model) {
        try (InputStream inputStream = file.getInputStream()) {
            //System.out.println("Controller:Upload " + dateStr + ", " + file.getOriginalFilename() + ", " + file.getSize() + " bytes");
            LocalDate tsMonth = LocalDate.parse(dateStr, formatter);
            EnaTimesheet enaTimesheet = new EnaTimesheet(tsMonth, inputStream);
            List<EnaTsEntry> bdws = enaTimesheet.getEntries();
            List<EnaTsProjectEntry> bps = enaTimesheet.getProjectEntries();
            model.addAttribute("invoiceMonth", mmyyFmt.format(tsMonth));
            model.addAttribute("invoiceDate", mdyFmt.format(LocalDate.now()));
            model.addAttribute("invoiceByDayAndWeek", bdws);
            model.addAttribute("invoiceByProject", bps);
        } catch (Exception e) {
            //logger.error("Error uploading file", e);
            System.out.println("Error uploading file");
        }
        return "invoice";
    }

}
