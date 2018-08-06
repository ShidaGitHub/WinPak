package ru.it_mm.winpak.utils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import javax.annotation.PostConstruct;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.primefaces.component.export.ExcelOptions;

public abstract  class Report implements Serializable {
    public static final String IN_STR = "ВХОД";
    public static final String OUT_STR = "ВЫХОД";
    
    private Date date1;
    private Date date2;
    
    public Report(){}
    
    @PostConstruct
    public abstract void init();
    
    public Date getDate1() {
        return date1;
    }
    public void setDate1(Date date1) {
        this.date1 = date1;
    }

    public Date getDate2() {
        return date2;
    }
    public void setDate2(Date date2) {
        this.date2 = date2;
    }
    
    public TimeZone getTimeZone(){
        return TimeZone.getDefault();
    }
    
    public abstract String getReportFileName();
    
    public ExcelOptions getExcelOpt() {
        ExcelOptions excelOpt;
        excelOpt = new ExcelOptions();
        excelOpt.setFacetFontStyle("BOLD");
        excelOpt.setFacetFontSize("12");
        return excelOpt;
    }
    
    public void postProcessXLS(Object document) {
        HSSFWorkbook wb = (HSSFWorkbook) document;
        HSSFSheet sheet = wb.getSheetAt(0);
        sheet.shiftRows(0, 2, 2);
        
        HSSFRow header = sheet.getRow(2);
        
        HSSFCell hssfSell = sheet.createRow(0).createCell(0);
        hssfSell.setCellValue("Сотрудники на заводе");        
        
        hssfSell = sheet.getRow(0).createCell(1);
        hssfSell.setCellValue("Период");        
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy");
        
        hssfSell = sheet.getRow(0).createCell(2);
        hssfSell.setCellValue(sdf.format(date1) + " - " + sdf.format(date2));
        
        for(int i=0; i < header.getPhysicalNumberOfCells(); i++) {
            header.getCell(i).getCellStyle().setBorderTop(BorderStyle.MEDIUM);
            header.getCell(i).getCellStyle().setBorderBottom(BorderStyle.MEDIUM);
            header.getCell(i).getCellStyle().setBorderLeft(BorderStyle.MEDIUM);
            header.getCell(i).getCellStyle().setBorderRight(BorderStyle.MEDIUM);
        }
        sheet.createFreezePane(1, 3, 1, 3);
    }
    
    public abstract  String showReport();
}
