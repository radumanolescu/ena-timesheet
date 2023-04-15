package com.aws.codestar.projecttemplates.controller;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import com.aws.codestar.projecttemplates.ena.EnaTimesheetEntry;
import com.aws.codestar.projecttemplates.ena.InvoiceByDayAndWeekEntry;
import com.aws.codestar.projecttemplates.ena.InvoiceByProjectEntry;
import com.aws.codestar.projecttemplates.ena.Parser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;

import java.util.List;

import static com.aws.codestar.projecttemplates.DataGen.getInvoiceByDayAndWeek;
import static com.aws.codestar.projecttemplates.DataGen.getInvoiceByProject;

@Controller
public class Upload {
    //private static final Logger logger = LoggerFactory.getLogger(Upload.class);

    @PostMapping(value = "/ena/upload/ena-timesheet")
    public String uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("date") String dateStr, Model model) {
        try (InputStream inputStream = file.getInputStream()) {
            System.out.println("UploadController:uploadFile: " + dateStr);
            Parser parser = new Parser();
            List<EnaTimesheetEntry> timesheetEntries = parser.parse(202303, inputStream);
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
