package com.aws.codestar.projecttemplates.controller;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import com.aws.codestar.projecttemplates.ena.InvoiceByDayAndWeek;
import com.aws.codestar.projecttemplates.ena.InvoiceActivityEntry;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ui.Model;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class Upload {
    //private static final Logger logger = LoggerFactory.getLogger(Upload.class);

    @PostMapping("/ena/upload/ena-timesheet")
    @ResponseBody
    public RedirectView uploadFile(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        printFileInfo(file);
        request.setAttribute("invoice", getInvoiceByDayAndWeek());
        // see https://www.baeldung.com/spring-redirect-and-forward
        request.setAttribute(
                View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.FOUND);
        return new RedirectView ("/ena/nvc/invoice.html");
    }

    @PostMapping("/ena/upload-multiple-files")
    @ResponseBody
    public List<String> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        return Arrays.stream(files)
                .map(file -> printFileInfo(file))
                .collect(Collectors.toList());
    }

    public String addNewEmployee(Model model){
        return "";
    }

    private String printFileInfo(MultipartFile file){
        String name = file.getOriginalFilename();
        String contentType = file.getContentType();
        long size = file.getSize();
        System.out.println("UploadController:ReceivedFile: " + name);
        System.out.println("UploadController:ReceivedFile: " + contentType);
        System.out.println("UploadController:ReceivedFile: " + size);
        return name;
    }

    private InvoiceByDayAndWeek getInvoiceByDayAndWeek() {
        InvoiceByDayAndWeek invoiceByDayAndWeek = new InvoiceByDayAndWeek();
        InvoiceActivityEntry invoiceActivityEntry = new InvoiceActivityEntry();
        invoiceActivityEntry.setProject("1803");
        invoiceActivityEntry.setActivity("CD - House");
        invoiceActivityEntry.setDate("03/01/23");
        invoiceActivityEntry.setHours("3.5");
        invoiceActivityEntry.setRate("$60/hr");
        invoiceActivityEntry.setDescription("800");
        invoiceActivityEntry.setCharge("$210.00");
        invoiceByDayAndWeek.addEntry(invoiceActivityEntry);
        return invoiceByDayAndWeek;
    }
}
