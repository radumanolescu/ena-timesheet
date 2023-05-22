package com.ena.timesheet.phd;

import com.ena.timesheet.xl.ExcelParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.ena.timesheet.util.Text.replaceNonAscii;
import static com.ena.timesheet.xl.XlUtil.stringValue;

public class Parser extends ExcelParser {

    public PhdTemplate parseTemplate(String yearMonth, InputStream inputStream) throws IOException {
        List<PhdTemplateEntry> entries = parseEntries(inputStream);
        return new PhdTemplate(yearMonth, entries);
    }

    public List<PhdTemplateEntry> parseBytes(byte[] fileBytes) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(fileBytes);
        List<PhdTemplateEntry> entries = parseEntries(bis);
        bis.close();
        return entries;
    }

    public List<PhdTemplateEntry> parseEntries(InputStream inputStream) throws IOException {
        List<PhdTemplateEntry> entries = new ArrayList<>();
        Workbook workbook = WorkbookFactory.create(inputStream);
        int index = 0; // first sheet
        Sheet sheet = workbook.getSheetAt(index);
        int i = 0;
        for (Row row : sheet) {
            String client = stringValue(row.getCell(0)).trim();
            if (client.equals("SUM")) {
                break;
            }
            String task = stringValue(row.getCell(1)).trim();
            entries.add(new PhdTemplateEntry(i, replaceNonAscii(client), replaceNonAscii(task)));
            i++;
        }
        setProjectCodes(entries);
        return entries;
    }

    public void setProjectCodes(List<PhdTemplateEntry> entries) throws JsonProcessingException {
        String projectCde = "";
        int projectBgn = -1;
        int projectEnd = -1;

        int numLin = entries.size();
        while (entries.get(numLin - 1).isBlank()) {
            entries.remove(numLin - 1); // remove last entries if they are empty
            numLin--;
        }
        // Starting from the last line, find the project code and boundaries ([start,end] line IDs).
        // Once found, set the project code for all the entries in the project.
        for (int lineNum = numLin - 1; lineNum >= 0; lineNum--) {
            p(entries.get(lineNum), projectCde, projectBgn, projectEnd);
            String entryType = entries.get(lineNum).entryType();
            switch (entryType) {
                case "null_null": // empty line between groups
                    // The first empty line after a project group indicates that the project begins at the next line.
                    if (projectBgn < 0) {
                        projectBgn = lineNum + 1;
                    }
                    // So set the project code for all the entries in the project
                    s(projectCde, projectBgn, projectEnd, entries);
                    p(entries.get(lineNum), projectCde, projectBgn, projectEnd);
                    // Reset the project code and boundaries
                    projectBgn = -1;
                    projectEnd = -1;
                    projectCde = "";
                    break;
                case "null_Task": // activity line
                    // If we have encountered an activity but the project is undefined,
                    // it means we have encountered a new project.
                    if (projectEnd < 0) {
                        projectEnd = lineNum;
                    }
                    break;
                case "Client_Task":
                    // The project code is the first word of the line,
                    // in the last entry of the group that has a non-empty value in that field.
                    if (projectCde.isEmpty()) {
                        projectCde = entries.get(lineNum).getClient();
                    }
                    if (projectEnd < 0) {
                        projectEnd = lineNum;
                    }
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + entryType);
            }
            if (lineNum == 0) {
                projectBgn = 1;
                s(projectCde, projectBgn, projectEnd, entries);
            }
            p(entries.get(lineNum), projectCde, projectBgn, projectEnd);
        }
    }

    private void p(PhdTemplateEntry w, String pC, int pB, int pE) throws JsonProcessingException {
        String ws = w.toJson();
        int xl = w.getRowNum() + 1;
        String ps = "{xl=" + xl + ",t=" + w.entryType() + ",c=`" + pC + "`,b=" + pB + ",e=" + pE + "}";
//        System.out.println(ws + "\t\t" + ps);
    }

    private void s(String pC, int pB, int pE, List<PhdTemplateEntry> worksheet) {
        if (pC.isEmpty() || pB < 0 || pE < 0) {
            return;
        }
        for (int lineNum = pB; lineNum <= pE; lineNum++) {
            worksheet.get(lineNum).setClient(pC);
        }
    }

}
/*
The result of parsing the PHD template is PhdTemplate(entries), where
0: PhdTemplateLine("","TASK")
// Every entry of a group contains the project id
1: PhdTemplateLine("1507","12 E 86th St - Corridors")
6:PhdTemplateLine("","") // group separator
92:PhdTemplateLine(PHD Office,Bookeeping & Taxes)
// There is no last line with PhdTemplateLine("","")
* */
