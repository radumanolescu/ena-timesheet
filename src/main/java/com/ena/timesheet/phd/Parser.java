package com.ena.timesheet.phd;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import com.ena.timesheet.xl.ExcelParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.poi.ss.usermodel.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static com.ena.timesheet.util.Text.replaceNonAscii;
import static com.ena.timesheet.xl.XlUtil.stringValue;

/**
 * A parser for a PHD template, which has the following structure:
 * HEADER,(ClientBlock)+,SUM
 * where:
 * HEADER = "CLIENT,TASK,1,2,3,...,31"
 * <p>
 * ClientBlock =
 * clientName   ,taskName,effort1,effort2,effort3,...,effort31,total
 * clientAddress,taskName,effort1,effort2,effort3,...,effort31,total
 * streetNumber ,taskName,effort1,effort2,effort3,...,effort31,total
 * [empty line, all cells are empty]
 * <p>
 * SUM ="SUM,Total,1,2,3,...,31"
 * <p>
 * Parse the PHD template and return a list of PhdTemplateEntry instances, such that each instance
 * has the clientName set to equal the streetNumber.
 */
public class Parser extends ExcelParser {
//    private static final Logger logger = LoggerFactory.getLogger(Parser.class);

//    public PhdTemplate parseTemplate(String yearMonth, InputStream inputStream) throws IOException {
//        List<PhdTemplateEntry> entries = parseEntries(inputStream);
//        return new PhdTemplate(yearMonth, entries);
//    }

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
        int rowNum = 0;
        for (Row row : sheet) {
            try {
                String client = stringValue(row.getCell(0)).trim();
                if (client.equals("SUM")) {
                    break;
                }
                // Empty rows are meaningful and should not be skipped.
                // They mark the separation between groups of tasks.
                String task = stringValue(row.getCell(1)).trim();
                // Read the effort per day from the row
                Map<Integer, Double> effort = new HashMap<>();
                // Skip the first row (header)
                if (rowNum > 0) {
                    // Each day is a column in the sheet, starting from column 2 (offset 1) and ending at column 32 (offset 31)
                    for (int colId = PhdTemplate.colOffset + 1; colId <= PhdTemplate.colOffset + 31; colId++) {
                        Cell cell = row.getCell(colId);
                        if (cell != null && cell.getCellType() == CellType.NUMERIC) {
                            Double d = cell.getNumericCellValue();
                            effort.put(colId - PhdTemplate.colOffset, d);
                        }
                    }
                }
                PhdTemplateEntry entry = new PhdTemplateEntry(rowNum, replaceNonAscii(client), replaceNonAscii(task));
                entry.setEffort(effort);
                entries.add(entry);
            } catch (Exception e) {
                System.out.println("Error in row " + (rowNum + 1) + ": " + e.getMessage());
//                e.printStackTrace();
//                logger.error("Error parsing row " + rowNum, e);
            }
            rowNum++;
        }
        setProjectCodes(entries);
        checkDupClientTask(entries);
        return entries;
    }

    /**
     * Once the project code is set for all entries, check that there are no duplicate client-task pairs.
     * Such errors have been observed in the past, and they are likely to be caused by manual errors in the template.
     */
    private void checkDupClientTask(List<PhdTemplateEntry> entries) {
        Set<String> clientTaskSet = new HashSet<>();
        for (PhdTemplateEntry entry : entries) {
            String clientTask = entry.clientCommaTask();
//            System.out.println("clientTask >>" + clientTask + "<<");
            if (clientTaskSet.contains(clientTask)) {
                String errMsg = "Duplicate client-task in row " + (entry.getRowNum() + 1) + ": `" + clientTask + "`";
                throw new IllegalStateException(errMsg);
            }
            // No point in adding empty client-task pairs, which are represented as "","".
            if (clientTask != null && clientTask.length() > 5) {
                clientTaskSet.add(clientTask);
            }
        }
    }

    /**
     * Parse the PHD template and set the project codes for all the entries.
     * Iterate over the entries in reverse order, starting from the last line.
     * Use empty lines to separate groups of tasks.
     * Inside each group, the project code is the first non-empty value in the "Client" field (iterating from end to start).
     * Once the project code is found, set it (the client property) for all the entries in the group.
     */
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
        // Once found, set the project code (the client property) for all the entries in the project.
        for (int lineNum = numLin - 1; lineNum >= 0; lineNum--) {
            p(entries.get(lineNum), projectCde, projectBgn, projectEnd);
            String entryType = entries.get(lineNum).entryType();
            switch (entryType) {
                case "null_null": // empty line between groups
                    // The first empty line after a project group indicates that the project begins at the next line.
                    projectBgn = lineNum + 1;
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

    private void s(String pC, int pB, int pE, List<PhdTemplateEntry> entries) {
        if (pC.isEmpty() || pB < 0 || pE < 0) {
            return;
        }
        for (int lineNum = pB; lineNum <= pE; lineNum++) {
            entries.get(lineNum).setClient(pC);
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

Examples (see also the files under docs/):
HEADER:
CLIENT,TASK,10-1,10-2,10-3,10-4,10-5,10-6,10-7,10-8,10-9,10-10,10-11,10-12,10-13,10-14,10-15,10-16,10-17,10-18,10-19,10-20,10-21,10-22,10-23,10-24,10-25,10-26,10-27,10-28,10-29,10-30,10-31,TOTALS

ClientBlock:
Brodsky - Ongoing Addresses,1705 - SPT124 Lobby/Amenities (list task),,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,0.00
address varies,2001 - BA307&310 Lobbies (list task),,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,0.00
project number varies,2003 - 75 WEA Amenities (list task),,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,0.00
,2301 - BA310 Fitness (list task),,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,0.00
,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,0.00

ClientBlock:
Brodsky - BA307 WrkLng/Bike/Stor,307 CA - FF&E / Procurements,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,0.00
307 E 44th St,307 CA - Site Visits / Punchlist,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,0.00
2401,307 Admin / Billing / Travel,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,0.00
,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,0.00

SUM:
SUM,,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00
* */
