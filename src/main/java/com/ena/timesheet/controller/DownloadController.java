package com.ena.timesheet.controller;

import com.ena.timesheet.dao.EnaTimesheetDao;
import com.ena.timesheet.dao.PhdDropdownsDao;
import com.ena.timesheet.dao.PhdTemplateDao;
import com.ena.timesheet.ena.EnaTimesheet;
import com.ena.timesheet.ena.EnaTsEntry;
import com.ena.timesheet.ena.EnaTsProjectEntry;
import com.ena.timesheet.util.Text;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.ena.timesheet.util.Time.mmyyFmt;

@Controller
public class DownloadController  {
    private static final String dropdownsFilename = "ena_dropdown.txt";
    private static final DateTimeFormatter mdyFmt = DateTimeFormatter.ofPattern("MMMM d, yyyy");
    private static final DateTimeFormatter yyyyMMddFmt = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final DynamoDbClient dynamoDBClient;

    @Autowired
    public DownloadController(DynamoDbClient dynamoDBClient) {
        this.dynamoDBClient = dynamoDBClient;
    }

    @GetMapping("/download-phd-dropdowns")
    public ResponseEntity<Resource> phdDropdowns(@RequestParam("yyyyMM") String yyyyMM) {
        try {
            System.out.println("Controller:DownloadDropdowns for " + yyyyMM);
            PhdDropdownsDao ddDao = new PhdDropdownsDao(dynamoDBClient);
            byte[] dropdownBytes = ddDao.getItem(yyyyMM);
            ByteArrayInputStream bais = new ByteArrayInputStream(dropdownBytes);
            Resource resource = new InputStreamResource(bais);
            String attachment = ContentDisposition.attachment().filename(dropdownsFilename).build().toString();
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .header(HttpHeaders.CONTENT_DISPOSITION, attachment)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/download-phd-timesheet")
    public ResponseEntity<Resource> phdTimesheet(@RequestParam("yyyyMM") String yyyyMM) {
        try {
            PhdTemplateDao dao = new PhdTemplateDao(dynamoDBClient);
            byte[] fileBytes = dao.getItem(yyyyMM);
            ByteArrayInputStream bais = new ByteArrayInputStream(fileBytes);
            Resource resource = new InputStreamResource(bais);
            String filename = phdTimesheetFileName(yyyyMM);
            String attachment = ContentDisposition.attachment().filename(filename).build().toString();
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, attachment)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/display-ena-invoice")
    public String enaInvoice(@RequestParam("yyyyMM") String yyyyMM, Model model) {
        try {
            LocalDate invoiceMonth = invoiceMonth(yyyyMM);
            EnaTimesheetDao dao = new EnaTimesheetDao(dynamoDBClient);
            byte[] fileBytes = dao.getItem(yyyyMM);
            EnaTimesheet enaTimesheet = new EnaTimesheet(invoiceMonth, fileBytes);
            prepareModelForDisplay(invoiceMonth, enaTimesheet, model);
            return "invoice";
        } catch (Exception e) {
            System.out.println("Error uploading file");
            e.printStackTrace();
            String stackTrace = Text.stackFormatter2(e);
            model.addAttribute("stackTrace", stackTrace);
            return "error-ena";
        }
    }

    /**
     * Provide parameters for the invoice.html template.
     * */
    private void prepareModelForDisplay(LocalDate tsMonth, EnaTimesheet enaTimesheet, Model model) {
        List<EnaTsEntry> bdws = enaTimesheet.getEntriesWithTotals();
        List<EnaTsProjectEntry> bps = enaTimesheet.getProjectEntries();
        model.addAttribute("invoiceMonth", mmyyFmt.format(tsMonth));
        model.addAttribute("invoiceDate", mdyFmt.format(LocalDate.now()));
        model.addAttribute("invoiceByDayAndWeek", bdws);
        model.addAttribute("invoiceByProject", bps);
    }

    private String phdTimesheetFileName(String yyyyMM) {
        // "PHD Timesheet 2023-03.xlsx
        return "PHD timesheet " + yyyyMM.substring(0, 4) + "-" + yyyyMM.substring(4, 6) + ".xlsx";
    }

    private LocalDate invoiceMonth(String yyyyMM) {
        return LocalDate.parse(yyyyMM + "01", yyyyMMddFmt);
    }
}
