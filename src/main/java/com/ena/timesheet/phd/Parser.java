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

    public void setProjectCodes(List<PhdTemplateEntry> lines) throws JsonProcessingException {
        String projectCde = "";
        int projectBgn = -1;
        int projectEnd = -1;

        int numLin = lines.size();
        while (lines.get(numLin - 1).isBlank()) {
            lines.remove(numLin - 1); // remove last lines if they are empty
            numLin--;
        }
        for (int lineNum = numLin - 1; lineNum >= 0; lineNum--) {
            p(lines.get(lineNum), projectCde, projectBgn, projectEnd);
            int numWords = lines.get(lineNum).numWords();
            switch (numWords) {
                case 0: // empty line between groups
                    projectBgn = lineNum + 1;
                    s(projectCde, projectBgn, projectEnd, lines);
                    p(lines.get(lineNum), projectCde, projectBgn, projectEnd);
                    projectBgn = -1;
                    projectEnd = -1;
                    projectCde = "";
                    break;
                case 1: // activity line
                    if (projectEnd < 0) {
                        projectEnd = lineNum;
                    }
                    break;
                default: // numWords == 2
                    if (projectCde.isEmpty()) {
                        projectCde = lines.get(lineNum).getClient();
                    }
                    if (projectEnd < 0) {
                        projectEnd = lineNum;
                    }
                    break;
            }
            if (lineNum == 0) {
                projectBgn = 1;
                s(projectCde, projectBgn, projectEnd, lines);
            }
            p(lines.get(lineNum), projectCde, projectBgn, projectEnd);
        }
    }

    private void p(PhdTemplateEntry w, String pC, int pB, int pE) throws JsonProcessingException {
        String ws = w.toJson();
        int xl = w.getRowNum() + 1;
        String ps = "{xl=" + xl + ",w=" + w.numWords() + ",c=`" + pC + "`,b=" + pB + ",e=" + pE + "}";
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
