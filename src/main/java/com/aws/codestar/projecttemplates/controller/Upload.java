package com.aws.codestar.projecttemplates.controller;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import com.aws.codestar.projecttemplates.ena.EnaTsEntry;
import com.aws.codestar.projecttemplates.ena.EnaTsProjectEntry;
import com.aws.codestar.projecttemplates.ena.EnaTimesheet;
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
public class Upload {
    //private static final Logger logger = LoggerFactory.getLogger(Upload.class);
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static DateTimeFormatter mmyyFmt = DateTimeFormatter.ofPattern("MMMM yyyy");
    private static DateTimeFormatter mdyFmt = DateTimeFormatter.ofPattern("MMMM d, yyyy");

    @PostMapping(value = "/ena/upload/ena-timesheet")
    public String uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("date") String dateStr, Model model) {
        try (InputStream inputStream = file.getInputStream()) {
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
