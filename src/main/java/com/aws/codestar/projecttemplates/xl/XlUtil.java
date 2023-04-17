package com.aws.codestar.projecttemplates.xl;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

public class XlUtil {
    public static LocalDate getLocalDate(Cell cell) {
        return cell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalTime getLocalTime(Cell cell) {
        return cell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
    }

    public static String stringValue(Cell cell) {
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

    public static String numericCell(Cell cell) {
        if (DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue() + "";
        } else {
            return cell.getNumericCellValue() + "";
        }
    }

}
