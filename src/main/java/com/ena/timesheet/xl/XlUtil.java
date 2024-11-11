package com.ena.timesheet.xl;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

public class XlUtil {
    public static final DecimalFormat decimalFormat = new DecimalFormat("#.##");

//    public static LocalDate getLocalDate(Cell cell) {
//        return cell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//    }

    public static LocalTime getLocalTime(Cell cell) {
        return cell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
    }

    public static String stringValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getRichStringCellValue().getString();
            case NUMERIC:
                return numericCell(cell);
            case BOOLEAN:
                return cell.getBooleanCellValue() + "";
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    public static String numericCell(Cell cell) {
        if (DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue() + "";
        } else {
            return decimalFormat.format(cell.getNumericCellValue());
        }
    }

    public static byte[] write(Workbook workbook) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        bos.close();
        return bos.toByteArray();
    }

}
