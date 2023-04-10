package com.aws.codestar.projecttemplates.ena;

import com.aws.codestar.projecttemplates.xl.ExcelParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Parser extends ExcelParser {
    public List<EnaTimesheetEntry> parse(Integer month, InputStream inputStream) throws IOException {
        List<EnaTimesheetEntry> entries = new ArrayList<>();
        Map<Integer, List<String>> rows = readWorkbook(inputStream, 0);
        rows.values().stream()
                .filter(row -> row.size() > 1)
                .map(row -> new EnaTimesheetEntry(month, row))
                .forEach(entries::add);
        return entries;
    }
}
