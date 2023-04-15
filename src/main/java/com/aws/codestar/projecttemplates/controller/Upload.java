package com.aws.codestar.projecttemplates.controller;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import com.aws.codestar.projecttemplates.ena.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import static com.aws.codestar.projecttemplates.DataGen.getInvoiceByDayAndWeek;
import static com.aws.codestar.projecttemplates.DataGen.getInvoiceByProject;

@Controller
public class Upload {
    //private static final Logger logger = LoggerFactory.getLogger(Upload.class);
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static DateTimeFormatter mmyyFmt = DateTimeFormatter.ofPattern("MMMM yyyy");
    private static DateTimeFormatter mdyFmt = DateTimeFormatter.ofPattern("MMMM d, yyyy");

    @PostMapping(value = "/ena/upload/ena-timesheet")
    public String uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("date") String dateStr, Model model) {
        try (InputStream inputStream = file.getInputStream()) {
            System.out.println("UploadController:uploadFile: " + dateStr);
            LocalDate tsMonth = LocalDate.parse(dateStr, formatter);
            XTimesheet xTimesheet = new XTimesheet(tsMonth, inputStream);

            printFileInfo(file);
            List<XEntry> bdws = xTimesheet.getEntries();
            List<XProjectEntry> bps = xTimesheet.getProjectEntries();
            model.addAttribute("invoiceMonth", mmyyFmt.format(tsMonth));
            model.addAttribute("invoiceDate", mdyFmt.format(LocalDate.now()));
            model.addAttribute("invoiceByDayAndWeek", bdws);
            model.addAttribute("invoiceByProject", bps);
            for (XEntry bdw : bdws) {
                System.out.println("UploadController:XEntry: " + bdw.toString());
            }
            for (XProjectEntry bp : bps) {
                System.out.println("UploadController:XProjectEntry: " + bp.toString());
            }
        } catch (Exception e) {
            //logger.error("Error uploading file", e);
            System.out.println("Error uploading file");
        }

        return "invoice";
    }

    @PostMapping(value = "NOT-IN-USE/ena/upload/ena-timesheet")
    public String uploadFileOld(@RequestParam("file") MultipartFile file, @RequestParam("date") String dateStr, Model model) {
        try (InputStream inputStream = file.getInputStream()) {
            System.out.println("UploadController:uploadFile: " + dateStr);
            LocalDate tsMonth = LocalDate.parse(dateStr, formatter);
            Parser parser = new Parser(tsMonth);
            List<EnaTimesheetEntry> timesheetEntries = parser.parse(inputStream);
            System.out.println("parsed " + timesheetEntries.size() + " timesheet entries");
            printFileInfo(file);
            List<InvoiceByDayAndWeekEntry> bdws = getInvoiceByDayAndWeek().getEntries();
            List<InvoiceByProjectEntry> bps = getInvoiceByProject().getEntries();
            model.addAttribute("invoiceByDayAndWeek", bdws);
            model.addAttribute("invoiceByProject", bps);
            for (InvoiceByDayAndWeekEntry bdw : bdws) {
                System.out.println("UploadController:InvoiceByDayAndWeekEntry: " + bdw.getActivity());
            }
            for (InvoiceByProjectEntry bp : bps) {
                System.out.println("UploadController:InvoiceByProjectEntry: " + bp.getProject());
            }
        } catch (Exception e) {
            //logger.error("Error uploading file", e);
            System.out.println("Error uploading file");
        }

        return "invoice";
    }

//    @PostMapping("/ena/upload-multiple-files")
//    @ResponseBody
//    public List<String> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
//        return Arrays.stream(files)
//                .map(file -> printFileInfo(file))
//                .collect(Collectors.toList());
//    }

    private String printFileInfo(MultipartFile file) {
        String name = file.getOriginalFilename();
        String contentType = file.getContentType();
        long size = file.getSize();
        long t = System.currentTimeMillis();
        System.out.println("UploadController:ReceivedFile: " + t + " " + name);
        System.out.println("UploadController:ReceivedFile: " + t + " " + contentType);
        System.out.println("UploadController:ReceivedFile: " + t + " " + size);
        return name;
    }

}
