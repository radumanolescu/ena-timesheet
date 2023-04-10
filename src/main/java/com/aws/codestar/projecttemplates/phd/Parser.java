package com.aws.codestar.projecttemplates.phd;

import com.aws.codestar.projecttemplates.util.Text;
import com.aws.codestar.projecttemplates.xl.ExcelParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Parser extends ExcelParser {
    public List<PhdTemplateEntry> parse(InputStream inputStream) throws IOException {
        List<PhdTemplateEntry> entries = new ArrayList<>();
        List<String> lines = new ArrayList<>();
        Map<Integer, List<String>> rows = readWorkbook(inputStream, 0);
        boolean stop = false;
        for (List<String> row : rows.values()) {
            if (row.get(0).equals("SUM")) {
                stop = true;
            }
            if (row.size() > 1 && !stop) {
                String rawLine = row.get(0) + "\t" + row.get(1);
                String line = Text.replaceNonAscii(rawLine);
                lines.add(line);
            }
        }
        return setProjectCodes(lines);
    }

    public List<PhdTemplateEntry> setProjectCodes(List<String> lines) {
        List<String[]> worksheet = new ArrayList<>();
        String projectCde = "";
        int projectBgn = -1;
        int projectEnd = -1;

        int numLin = lines.size();
        while (lines.get(numLin - 1).length() < 2) {
            numLin--; // remove last lines if they are empty
        }
        for (int lineNum = numLin - 1; lineNum >= 0; lineNum--) {
            worksheet.add(new String[2]);
        }
        for (int lineNum = numLin - 1; lineNum >= 0; lineNum--) {
            worksheet.get(lineNum)[0] = "";
            worksheet.get(lineNum)[1] = "";
            String[] words = lines.get(lineNum).trim().split("\t");
            int numWords = words.length;

            switch (numWords) {
                case 1:
                    worksheet.get(lineNum)[1] = words[0].trim();
                    if (words[0].trim().isEmpty()) {
                        projectBgn = lineNum + 1;
                        p(words, projectCde, projectBgn, projectEnd);
                        s(projectCde, projectBgn, projectEnd, worksheet);
                        projectBgn = -1;
                        projectEnd = -1;
                        projectCde = "";
                    } else {
                        if (projectEnd < 0) {
                            projectEnd = lineNum;
                        }
                    }
                    break;
                default:
                    worksheet.get(lineNum)[1] = words[1].trim();
                    if (projectCde.isEmpty()) {
                        projectCde = words[0];
                    }
                    if (projectEnd < 0) {
                        projectEnd = lineNum;
                    }
                    break;
            }

            p(words, projectCde, projectBgn, projectEnd);
            if (lineNum == 0) {
                projectBgn = 1;
                p(words, projectCde, projectBgn, projectEnd);
                s(projectCde, projectBgn, projectEnd, worksheet);
            }
        }

        List<PhdTemplateEntry> entries = new ArrayList<>();
        for (String[] row : worksheet) {
            entries.add(new PhdTemplateEntry(row[0], row[1]));
        }

        return entries;
    }

    private void p(String[] w, String pC, int pB, int pE) {
        String ws = Arrays.toString(w).replace(", ", "|||").replace("[", "[").replace("]", "]");
        String ps = w.length + "," + pC + ":(" + pB + "," + pE + ")";
        // System.out.println(ws + ps);
    }

    private void s(String pC, int pB, int pE, List<String[]> worksheet) {
        for (int lineNum = pB; lineNum <= pE; lineNum++) {
            worksheet.get(lineNum)[0] = pC;
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
