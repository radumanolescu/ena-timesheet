package com.ena.timesheet.xl;

import org.apache.poi.ss.usermodel.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ena.timesheet.xl.XlUtil.stringValue;

public class ExcelParser {

    public Map<Integer, List<String>> readWorkbook(InputStream inputStream, int index) throws IOException {
        Workbook workbook = WorkbookFactory.create(inputStream);
        return readWorkbook(workbook, index);
    }

    public Map<Integer, List<String>> readWorkbook(Workbook workbook, int index) {
        return readWorksheet(workbook.getSheetAt(index));
    }

    public Map<Integer, List<String>> readWorksheet(Sheet sheet) {
        Map<Integer, List<String>> data = new HashMap<>();
        int i = 0;
        for (Row row : sheet) {
            data.put(i, stringValues(row));
            i++;
        }
        return data;
    }

    public List<String> stringValues(Row row) {
        List<String> data = new ArrayList<>();
        for (Cell cell : row) {
            data.add(stringValue(cell));
        }
        return data;
    }

}
