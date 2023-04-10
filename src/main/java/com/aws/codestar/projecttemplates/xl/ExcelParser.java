package com.aws.codestar.projecttemplates.xl;

import org.apache.poi.ss.usermodel.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public String stringValue(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getRichStringCellValue().getString();
            case NUMERIC:
                return numericCell(cell);
            case BOOLEAN:
                return cell.getBooleanCellValue() + "";
            case FORMULA:
                return cell.getCellFormula() + "";
            default:
                return "";
        }
    }

    public String numericCell(Cell cell) {
        if (DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue() + "";
        } else {
            return cell.getNumericCellValue() + "";
        }
    }

}
