package com.aws.codestar.projecttemplates;

import com.aws.codestar.projecttemplates.ena.InvoiceByDayAndWeek;
import com.aws.codestar.projecttemplates.ena.InvoiceByDayAndWeekEntry;
import com.aws.codestar.projecttemplates.ena.InvoiceByProject;
import com.aws.codestar.projecttemplates.ena.InvoiceByProjectEntry;

import java.time.LocalDate;

public class DataGen {
    public static InvoiceByDayAndWeek getInvoiceByDayAndWeek() {
        InvoiceByDayAndWeek invoiceByDayAndWeek = new InvoiceByDayAndWeek();
        InvoiceByDayAndWeekEntry invoiceByDayAndWeekEntry = new InvoiceByDayAndWeekEntry();
        invoiceByDayAndWeekEntry.setProject("1803");
        invoiceByDayAndWeekEntry.setActivity("CD - House");
        invoiceByDayAndWeekEntry.setDate(LocalDate.now());
        invoiceByDayAndWeekEntry.setHours(3.5f);
        invoiceByDayAndWeekEntry.setRate(60.0f);
        invoiceByDayAndWeekEntry.setDescription("schlulter order, site rev w john, review sequence re floors, trim, usai order");
        invoiceByDayAndWeekEntry.setCharge(210.0f);
        invoiceByDayAndWeek.addEntry(invoiceByDayAndWeekEntry);
        return invoiceByDayAndWeek;
    }

    public static InvoiceByProject getInvoiceByProject() {
        InvoiceByProject invoiceByProject = new InvoiceByProject();
        InvoiceByProjectEntry invoiceByProjectEntry = new InvoiceByProjectEntry();
        invoiceByProjectEntry.setProject("1803");
        invoiceByProjectEntry.setActivity("CD - House");
        invoiceByProjectEntry.setHours(3.5f);
        invoiceByProject.addEntry(invoiceByProjectEntry);
        return invoiceByProject;
    }
    
}
