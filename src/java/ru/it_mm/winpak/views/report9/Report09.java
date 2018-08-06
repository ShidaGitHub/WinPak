package ru.it_mm.winpak.views.report9;

import com.sun.faces.component.visit.FullVisitContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitResult;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.primefaces.event.RowEditEvent;
import ru.it_mm.winpak.utils.DateRangeValidator;
import ru.it_mm.winpak.utils.Report;
import static ru.it_mm.winpak.utils.Utils.*;
import ru.it_mm.winpak.utils.entity.DepartmentOpt;

@Named
@SessionScoped
public class Report09 extends Report{
    private static final long serialVersionUID = 91929394L;

    @PersistenceContext
    private EntityManager entityManager;
    @Resource
    private UserTransaction userTransaction;
    
    private List<DepartmentOpt> departmentOpt;
    private DepartmentOpt selectedDepartmentOpt;
    
    private List<Object[]> history;
    private List<Report09Data> report09Data;

    public Report09() {}

    @PostConstruct
    @Override
    public void init() {
        setDate1(atStartOfMonth(new Date()));
        setDate2(atEndOfMonth(new Date()));

        String qlString = "SELECT do FROM DepartmentOpt do "
                + "WHERE do.userName = :userName";
        Query query = entityManager.createQuery(qlString, DepartmentOpt.class);
        query.setParameter("userName", FacesContext.getCurrentInstance().getExternalContext().getRemoteUser());
        departmentOpt = query.getResultList();
        report09Data = new ArrayList<>();
    }
    
    public void validatedateRange(FacesContext context, UIComponent comp, Object value) {
        LocalDateTime minDate = LocalDateTime.ofInstant(getDate1().toInstant(), ZoneId.systemDefault());
        LocalDateTime maxDate = LocalDateTime.ofInstant(getDate2().toInstant(), ZoneId.systemDefault());
        ((org.primefaces.component.calendar.Calendar) comp).setValid(true);
        
        Duration diff = Duration.between(minDate, maxDate);
        if (diff.toDays() > 250){ //максимум столбцов в excel 256 => максимум дней 253
            ((org.primefaces.component.calendar.Calendar) comp).setValid(false);
            
            if (((org.primefaces.component.calendar.Calendar) comp).getClientId().contains("2")){
                FacesContext facesContext = FacesContext.getCurrentInstance();
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Период охватывает " + String.valueOf(diff.toDays()) + " дней!",
                        "Файл Excel может содежать только 256 колонок"));
            }
        }
        
    }
    
    public List<DepartmentOpt> getDepartmentOpt() {
        return departmentOpt;
    }
    public void setDepartmentOpt(List<DepartmentOpt> departmentOpt) {
        this.departmentOpt = departmentOpt;
    }

    public String updateDepartmentOpt() {
        String retVal = "";
        try {
            userTransaction.begin();
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaDelete<DepartmentOpt> criteriaDelete = criteriaBuilder.createCriteriaDelete(DepartmentOpt.class);
            Root<DepartmentOpt> root = criteriaDelete.from(DepartmentOpt.class);
            criteriaDelete.where(criteriaBuilder.equal(
                    root.get("userName"), FacesContext.getCurrentInstance().getExternalContext().getRemoteUser()));

            Query query = entityManager.createQuery(criteriaDelete);
            int deletedRows = query.executeUpdate();

            departmentOpt.forEach((opt) -> {
                entityManager.persist(opt);
            });
            userTransaction.commit();

        } catch (IllegalStateException | SecurityException | HeuristicMixedException | HeuristicRollbackException | NotSupportedException | RollbackException | SystemException e) {
            retVal = "error";
        }

        return retVal;
    }

    public void onAddDepOpt() {
        DepartmentOpt depOp = new DepartmentOpt(0l);
        depOp.setPathDir("отчеты\\Новое подразделение\\");
        depOp.setDepartment("Новое подразделение");
        depOp.setUserName(FacesContext.getCurrentInstance().getExternalContext().getRemoteUser());

        departmentOpt.add(depOp);
        updateDepartmentOpt();
    }

    public void onDelDepOpt(DepartmentOpt depOpt) {
        if (depOpt == null && departmentOpt.size() > 0) {
            departmentOpt.remove(departmentOpt.size() - 1);
        }
        if (depOpt != null && departmentOpt.contains(depOpt)) {
            departmentOpt.remove(depOpt);
        }
        updateDepartmentOpt();
    }

    public void onRowEdit(RowEditEvent event) {
        updateDepartmentOpt();
    }

    public void onRowCancel(RowEditEvent event) {
        updateDepartmentOpt();
    }

    public List<String> completeDepartment(String departmentPat) {
        String qlString = "SELECT DISTINCT cardHolder.note5 FROM CardHolder cardHolder "
                + "WHERE cardHolder.deleted = 0 AND cardHolder.note5 LIKE :departmentPat";
        Query query = entityManager.createQuery(qlString);
        query.setParameter("departmentPat", "%" + departmentPat + "%");
        query.setMaxResults(30);
        return query.getResultList();
    }

    public DepartmentOpt getSelectedDepartmentOpt() {
        return selectedDepartmentOpt;
    }

    public void setSelectedDepartmentOpt(DepartmentOpt selectedDepartmentOpt) {
        this.selectedDepartmentOpt = selectedDepartmentOpt;
    }
    
    public List<Report09Data> getReport09Data() {
        return report09Data;
    }
    public void setReport09Data(List<Report09Data> report09Data) {
        this.report09Data = report09Data;
    }

    public List<Object[]> getHistory() {
        return history;
    }
    public void setHistory(List<Object[]> history) {
        this.history = history;
    }

    
    public List<Date> getDateRange() {
        Date startRage = atEndOfDay(minDate(getDate1(), getDate2()));
        Date endRage = atEndOfDay(maxDate(getDate1(), getDate2()));

        List<Date> dateRange = new ArrayList<>();
        dateRange.add(startRage);
        dateRange.add(startRage);
        while (atEndOfDay(startRage).before(endRage)) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(startRage);
            cal.add(Calendar.DATE, 1);
            startRage = cal.getTime();
            dateRange.add(startRage);
            dateRange.add(startRage);
        }
        return dateRange;
    }
    public List<Date> getDistinctDateRange(){
        return getDateRange().stream().distinct().collect(Collectors.toList());
    }
    
    public boolean isHoliday(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).getDayOfWeek().getValue() > 5;
    }
    
    
    private Sheet createSummaryTable(Workbook workbook, String departmentName, List<Date> dateRange){
        Sheet sheet = workbook.createSheet("Сводная таблица");
            
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 10);

        Font holidayHeaderFont = workbook.createFont();
        holidayHeaderFont.setBold(true);
        holidayHeaderFont.setFontHeightInPoints((short) 10);
        holidayHeaderFont.setColor(IndexedColors.RED.getIndex());

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        CellStyle tableHeaderCellStyle = workbook.createCellStyle();
        tableHeaderCellStyle.setFont(headerFont);
        tableHeaderCellStyle.setAlignment(HorizontalAlignment.CENTER);
        tableHeaderCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        tableHeaderCellStyle.setBorderTop(BorderStyle.THIN);
        tableHeaderCellStyle.setBorderBottom(BorderStyle.THIN);
        tableHeaderCellStyle.setBorderLeft(BorderStyle.THIN);
        tableHeaderCellStyle.setBorderRight(BorderStyle.THIN);

        CellStyle tableHeaderHolidayCellStyle = workbook.createCellStyle();
        tableHeaderHolidayCellStyle.setAlignment(HorizontalAlignment.CENTER);
        tableHeaderHolidayCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        tableHeaderHolidayCellStyle.setFont(holidayHeaderFont);
        tableHeaderHolidayCellStyle.setBorderTop(BorderStyle.THIN);
        tableHeaderHolidayCellStyle.setBorderBottom(BorderStyle.THIN);
        tableHeaderHolidayCellStyle.setBorderLeft(BorderStyle.THIN);
        tableHeaderHolidayCellStyle.setBorderRight(BorderStyle.THIN);

        //header
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy");

        Row headerRow = sheet.createRow(0);
        Cell cell = headerRow.createCell(0);
        cell.setCellStyle(headerCellStyle);
        cell.setCellValue("Цех: " + departmentName + " (" + sdf.format(getDate1()) + " - " + sdf.format(getDate2()) +")");

        //table header
        headerRow = sheet.createRow(2);
        Row headerRow1 = sheet.createRow(3);

        headerRow.createCell(0).setCellValue("ФИО");
        sheet.addMergedRegion(new CellRangeAddress(2, 3, 0, 0));
        headerRow.createCell(1).setCellValue("Табель");
        sheet.addMergedRegion(new CellRangeAddress(2, 3, 1, 1));        
        headerRow.createCell(2).setCellValue("Данные");
        sheet.addMergedRegion(new CellRangeAddress(2, 3, 2, 2));
        
        headerRow.getCell(0).setCellStyle(tableHeaderCellStyle);
        headerRow1.createCell(0).setCellStyle(tableHeaderCellStyle);        
        headerRow.getCell(1).setCellStyle(tableHeaderCellStyle);
        headerRow1.createCell(1).setCellStyle(tableHeaderCellStyle);
        headerRow.getCell(2).setCellStyle(tableHeaderCellStyle);
        headerRow1.createCell(2).setCellStyle(tableHeaderCellStyle);

        sdf = new SimpleDateFormat("dd MMMM");
        for(Date date : dateRange){
            headerRow.createCell(dateRange.indexOf(date) * 2  + 3).setCellValue(sdf.format(date));
            if (!isHoliday(date)){
                headerRow.getCell(dateRange.indexOf(date) * 2 + 3).setCellStyle(tableHeaderCellStyle);
                headerRow.createCell(dateRange.indexOf(date) * 2 + 4).setCellStyle(tableHeaderCellStyle);
            }else{
                headerRow.getCell(dateRange.indexOf(date) * 2 + 3).setCellStyle(tableHeaderHolidayCellStyle);
                headerRow.createCell(dateRange.indexOf(date) * 2 + 4).setCellStyle(tableHeaderHolidayCellStyle);
            }
            sheet.addMergedRegion(new CellRangeAddress(2, 2, dateRange.indexOf(date) * 2 + 3, dateRange.indexOf(date) * 2 + 4));
            
            headerRow1.createCell(dateRange.indexOf(date) * 2  + 3).setCellValue("Ночь");
            headerRow1.getCell(dateRange.indexOf(date) * 2 + 3).setCellStyle(tableHeaderCellStyle);
            headerRow1.createCell(dateRange.indexOf(date) * 2  + 4).setCellValue("День");
            headerRow1.getCell(dateRange.indexOf(date) * 2 + 4).setCellStyle(tableHeaderCellStyle);
        }
        headerRow.createCell(dateRange.size() * 2 + 3).setCellValue("Итого");
        sheet.addMergedRegion(new CellRangeAddress(2, 3, dateRange.size() * 2 + 3, dateRange.size() * 2 + 3));
        headerRow.getCell(dateRange.size() * 2 + 3).setCellStyle(tableHeaderCellStyle);
        headerRow1.createCell(dateRange.size() * 2 + 3).setCellStyle(tableHeaderCellStyle);        

        //table
        int[] chIds = history.stream().mapToInt(value -> (int) value[1]).distinct().toArray(); // уникальные cardHolder из history
        int r = 4;
        for(int chId : chIds){
            Row sRow0 = sheet.createRow(r);
            Row sRow1 = sheet.createRow(r+1);
            Row sRow2 = sheet.createRow(r+2);
            
            //фильтруем историю по cardHolder на дату
            for(Date date : dateRange){
                List<Object[]> filtHist = history.stream().
                        filter(obj -> (int)obj[1] == chId &&
                                ((Date)obj[0]).after(atStartOfDay(date)) && ((Date)obj[0]).before(date)).
                                collect(Collectors.toList());                
                
                if(filtHist.size() > 0){
                    String lastName = String.valueOf(filtHist.get(0)[2]);
                    String firstName = String.valueOf(filtHist.get(0)[3]);
                    String secName = String.valueOf(filtHist.get(0)[4]);
                    String tab = String.valueOf(filtHist.get(0)[5]);
                    
                    sRow0.createCell(0).setCellValue(lastName + " " + firstName.substring(0, 1) + "." + secName.substring(0, 1) + ".");                        
                    sRow0.createCell(1).setCellValue(tab);
                    sRow0.createCell(2).setCellValue("Вход");
                    sRow1.createCell(2).setCellValue("Выход");
                    sRow2.createCell(2).setCellValue("Итого");
                    
                    //*** Выход/выход комбинации
                    String devName0 = ((String)filtHist.get(0)[6]).toUpperCase(); //Первое событие на дату
                    String devName1 = ((String)filtHist.get(filtHist.size() - 1)[6]).toUpperCase(); //Послед событие на дату
                    List<Object[]> filtListIn = filtHist.stream().filter(obj -> ((String)obj[6]).toUpperCase().contains(IN_STR)).collect(Collectors.toList()); //Все входы за день
                    List<Object[]> filtListOut = filtHist.stream().filter(obj -> ((String)obj[6]).toUpperCase().contains(OUT_STR)).collect(Collectors.toList()); //Все выходы за день
                    
                    Date se = new Date(0);
                    Date in0 = se; //Вход ночь
                    Date in1 = se; //Вход день
                    Date out0 = se; //Выход ночь
                    Date out1 = se; //Выход день
                    
                    //***Первое событие
                    //*** Вошел в начале
                    if (devName0.contains(IN_STR)){
                        in1 = (Date)filtHist.get(0)[0];
                        out1 = atEndOfDay(date);
                        out1.setTime(out1.getTime() - 1000); //поправка на эксель
                    }
                    //*** Вышел в начале
                    if (devName0.contains(OUT_STR)){
                        in0 = atStartOfDay(date);
                        out0 = (Date)filtHist.get(0)[0];
                    }
                    
                    //***Пследнее событие
                    //*** Вошел в конце
                    if (devName1.contains(IN_STR)){
                        in1 = (Date)filtHist.get(filtHist.size() - 1)[0];
                        out1 = atEndOfDay(date);
                        out1.setTime(out1.getTime() - 1000); //поправка на эксель
                    }
                    //*** Вышел в конце
                    if (devName1.contains(OUT_STR)){
                        out1 = (Date)filtHist.get(filtHist.size() - 1)[0];
                        if(in1.equals(se) && filtListIn.size() > 0){
                            in1 = (Date) filtListIn.get(filtListIn.size() - 1)[0];
                        }
                    }
                    
                    //*** Вошел не вышел - работает до конца суток
                    if (filtListIn.size() > 0 && filtListOut.isEmpty()){
                        in0 = se;
                        out0 = se;
                        in1 = (Date)filtListIn.get(0)[0];
                        out1 = atEndOfDay(date);
                        out1.setTime(out1.getTime() - 1000); //поправка на эксель
                    }                    
                    //*** Вышел не вошел - работает с начала суток и больше не входит
                    if (filtListOut.size() > 0 && filtListIn.isEmpty()){
                        in1 = se;
                        out1 = se;
                        in0 = atStartOfDay(date);
                        out0 = (Date)filtListOut.get(filtListOut.size() - 1)[0]; // послед. выход
                    }
                    
                    sRow0.createCell(dateRange.indexOf(date) * 2 + 3).setCellValue(in0);
                    sRow1.createCell(dateRange.indexOf(date) * 2 + 3).setCellValue(out0);
                    sRow0.createCell(dateRange.indexOf(date) * 2 + 4).setCellValue(in1);
                    sRow1.createCell(dateRange.indexOf(date) * 2 + 4).setCellValue(out1);
                    

                    sRow2.createCell(dateRange.indexOf(date) * 2 + 3).setCellType(CellType.FORMULA);
                    sRow2.getCell(dateRange.indexOf(date) * 2 + 3).setCellFormula(
                                "SUM(-" + sRow0.getCell(dateRange.indexOf(date) * 2 + 3).getAddress().formatAsString() + "," 
                                        + sRow1.getCell(dateRange.indexOf(date) * 2 + 3).getAddress().formatAsString() + ",-" 
                                        + sRow0.getCell(dateRange.indexOf(date) * 2 + 4).getAddress().formatAsString() + ","
                                        + sRow1.getCell(dateRange.indexOf(date) * 2 + 4).getAddress().formatAsString() + ")");
                }
            }
            r += 3;
        }

        //Стиль
        CreationHelper createHelper = workbook.getCreationHelper();
        
        CellStyle stBorderLT = workbook.createCellStyle();
        stBorderLT.setBorderLeft(BorderStyle.THIN);
        stBorderLT.setBorderTop(BorderStyle.THIN);
        
        CellStyle stBorderL = workbook.createCellStyle();
        stBorderL.setBorderLeft(BorderStyle.THIN);
        
        CellStyle stBorderLB = workbook.createCellStyle();
        stBorderLB.setBorderLeft(BorderStyle.THIN);
        stBorderLB.setBorderBottom(BorderStyle.THIN);
        
        CellStyle stBorderLT_DF = workbook.createCellStyle();
        stBorderLT_DF.setBorderLeft(BorderStyle.THIN);
        stBorderLT_DF.setBorderTop(BorderStyle.THIN);
        stBorderLT_DF.setDataFormat(createHelper.createDataFormat().getFormat(" [<25570];HH:mm"));
        
        CellStyle stBorderL_DF = workbook.createCellStyle();
        stBorderL_DF.setBorderLeft(BorderStyle.THIN);
        stBorderL_DF.setDataFormat(createHelper.createDataFormat().getFormat(" [<25570];HH:mm"));
        
        CellStyle stBorderRT_DF = workbook.createCellStyle();
        stBorderRT_DF.setBorderRight(BorderStyle.THIN);
        stBorderRT_DF.setBorderTop(BorderStyle.THIN);
        stBorderRT_DF.setDataFormat(createHelper.createDataFormat().getFormat(" [<25570];HH:mm"));
        
        CellStyle stBorderR_DF = workbook.createCellStyle();
        stBorderR_DF.setBorderRight(BorderStyle.THIN);
        stBorderR_DF.setDataFormat(createHelper.createDataFormat().getFormat(" [<25570];HH:mm"));
        
        CellStyle stBorderLB_DF = workbook.createCellStyle();
        stBorderLB_DF.setBorderLeft(BorderStyle.THIN);
        stBorderLB_DF.setBorderBottom(BorderStyle.THIN);
        stBorderLB_DF.setBorderTop(BorderStyle.DASHED);
        stBorderLB_DF.setDataFormat(createHelper.createDataFormat().getFormat("[H]:mm"));
        stBorderLB_DF.setAlignment(HorizontalAlignment.CENTER);
        
        CellStyle stBorderRB_DF = workbook.createCellStyle();
        stBorderRB_DF.setBorderRight(BorderStyle.THIN);
        stBorderRB_DF.setBorderBottom(BorderStyle.THIN);
        stBorderRB_DF.setBorderTop(BorderStyle.DASHED);
        stBorderRB_DF.setDataFormat(createHelper.createDataFormat().getFormat("[H]:mm"));
        
        for (int rc = 4; rc < sheet.getPhysicalNumberOfRows(); rc +=3){
            //Первые 3 колонки 
            for (int cc = 0; cc < 3; cc++){
                Cell cell0 = sheet.getRow(rc).getCell(cc) != null ? sheet.getRow(rc).getCell(cc) : sheet.getRow(rc).createCell(cc);
                Cell cell1 = sheet.getRow(rc+1).getCell(cc) != null ? sheet.getRow(rc+1).getCell(cc) : sheet.getRow(rc+1).createCell(cc);
                Cell cell2 = sheet.getRow(rc+2).getCell(cc) != null ? sheet.getRow(rc+2).getCell(cc) : sheet.getRow(rc+2).createCell(cc);

                cell0.setCellStyle(stBorderLT);
                cell1.setCellStyle(stBorderL);
                cell2.setCellStyle(stBorderLB);

            }
            //Таблица
            for (int cc = 3; cc < dateRange.size()*2 + 3; cc +=2){
                Cell cell0 = sheet.getRow(rc).getCell(cc) != null ? sheet.getRow(rc).getCell(cc) : sheet.getRow(rc).createCell(cc);
                Cell cell1 = sheet.getRow(rc+1).getCell(cc) != null ? sheet.getRow(rc+1).getCell(cc) : sheet.getRow(rc+1).createCell(cc);
                Cell cell2 = sheet.getRow(rc+2).getCell(cc) != null ? sheet.getRow(rc+2).getCell(cc) : sheet.getRow(rc+2).createCell(cc);

                cell0.setCellStyle(stBorderLT_DF);
                cell1.setCellStyle(stBorderL_DF);
                cell2.setCellStyle(stBorderLB_DF);
                
                if (cell2.getCellTypeEnum() != CellType.FORMULA)
                    cell2.setCellValue("00:00");
            }
            
            for (int cc = 4; cc < dateRange.size()*2 + 4; cc +=2){
                Cell cell0 = sheet.getRow(rc).getCell(cc) != null ? sheet.getRow(rc).getCell(cc) : sheet.getRow(rc).createCell(cc);
                Cell cell1 = sheet.getRow(rc+1).getCell(cc) != null ? sheet.getRow(rc+1).getCell(cc) : sheet.getRow(rc+1).createCell(cc);
                Cell cell2 = sheet.getRow(rc+2).getCell(cc) != null ? sheet.getRow(rc+2).getCell(cc) : sheet.getRow(rc+2).createCell(cc);

                cell0.setCellStyle(stBorderRT_DF);
                cell1.setCellStyle(stBorderR_DF);
                cell2.setCellStyle(stBorderRB_DF);
                
                sheet.addMergedRegion(new CellRangeAddress(rc+2, rc+2, cc-1, cc));
            }

            //Итог
            sheet.getRow(rc).createCell(dateRange.size()*2 + 3).setCellStyle(stBorderR_DF);
            sheet.getRow(rc+1).createCell(dateRange.size()*2 + 3).setCellStyle(stBorderR_DF);

            Cell cell2 = sheet.getRow(rc+2).createCell(dateRange.size()*2 + 3);
            cell2.setCellStyle(stBorderRB_DF);
            if (dateRange.size() > 1)
                cell2.setCellFormula("SUM(" + sheet.getRow(rc+2).getCell(3).getAddress().formatAsString() + 
                                     ":" + sheet.getRow(rc+2).getCell(dateRange.size()*2 + 2).getAddress().formatAsString() + ")");
            else
                cell2.setCellFormula("SUM(" + sheet.getRow(rc+2).getCell(3).getAddress().formatAsString() + ")");
        }          
        sheet.setColumnWidth(0, 20*256);
        sheet.createFreezePane(1, 4, 1, 4);
        
        return sheet;
    }
    
    private Sheet createPassageTable(Workbook workbook, String departmentName, List<Date> dateRange){
        Sheet sheet = workbook.createSheet("Проходы");
        
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 10);

        Font holidayHeaderFont = workbook.createFont();
        holidayHeaderFont.setBold(true);
        holidayHeaderFont.setFontHeightInPoints((short) 10);
        holidayHeaderFont.setColor(IndexedColors.RED.getIndex());

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        CellStyle tableHeaderCellStyle = workbook.createCellStyle();
        tableHeaderCellStyle.setFont(headerFont);
        tableHeaderCellStyle.setBorderTop(BorderStyle.THIN);
        tableHeaderCellStyle.setBorderBottom(BorderStyle.THIN);
        tableHeaderCellStyle.setBorderLeft(BorderStyle.THIN);
        tableHeaderCellStyle.setBorderRight(BorderStyle.THIN);

        CellStyle tableHeaderHolidayCellStyle = workbook.createCellStyle();
        tableHeaderHolidayCellStyle.setFont(holidayHeaderFont);
        tableHeaderHolidayCellStyle.setBorderTop(BorderStyle.THIN);
        tableHeaderHolidayCellStyle.setBorderBottom(BorderStyle.THIN);
        tableHeaderHolidayCellStyle.setBorderLeft(BorderStyle.THIN);
        tableHeaderHolidayCellStyle.setBorderRight(BorderStyle.THIN);

        //header
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy");

        Row headerRow = sheet.createRow(0);
        Cell cell = headerRow.createCell(0);
        cell.setCellStyle(headerCellStyle);
        cell.setCellValue("Цех: " + departmentName + " (" + sdf.format(getDate1()) + " - " + sdf.format(getDate2()) +")");

        //table header
        headerRow = sheet.createRow(2);

        headerRow.createCell(0).setCellValue("ФИО");
        headerRow.getCell(0).setCellStyle(tableHeaderCellStyle);
        headerRow.createCell(1).setCellValue("Табель");
        headerRow.getCell(1).setCellStyle(tableHeaderCellStyle);
        headerRow.createCell(2).setCellValue("Вход дата");
        headerRow.getCell(2).setCellStyle(tableHeaderCellStyle);
        headerRow.createCell(3).setCellValue("Вход время");
        headerRow.getCell(3).setCellStyle(tableHeaderCellStyle);
        headerRow.createCell(4).setCellValue("Выход дата");
        headerRow.getCell(4).setCellStyle(tableHeaderCellStyle);
        headerRow.createCell(5).setCellValue("Выход время");
        headerRow.getCell(5).setCellStyle(tableHeaderCellStyle);
        headerRow.createCell(6).setCellValue("Время");
        headerRow.getCell(6).setCellStyle(tableHeaderCellStyle);
        
        //table
        int[] chIds = history.stream().mapToInt(value -> (int) value[1]).distinct().toArray(); // уникальные recordID из history
        for(int recordID : chIds){                
            
            //фильтруем историю по recordID на дату
            dateRange.forEach((date) -> {
                List<Object[]> filtHist = history.stream().
                        filter(obj -> (int)obj[1] == recordID &&
                                ((Date)obj[0]).after(atStartOfDay(date)) && ((Date)obj[0]).before(date)).
                                collect(Collectors.toList());
                if (!filtHist.isEmpty()){
                    String lastName = String.valueOf(filtHist.get(0)[2]);
                    String firstName = String.valueOf(filtHist.get(0)[3]);
                    String secName = String.valueOf(filtHist.get(0)[4]);
                    String tab = String.valueOf(filtHist.get(0)[5]);

                    String devName0 = ((String)filtHist.get(0)[6]).toUpperCase(); //Первое событие на дату
                    if (devName0.contains(OUT_STR)){ //Сначала вышел
                        Date in = atStartOfDay(date);
                        Date out = (Date)filtHist.get(0)[0];

                        Row sRow0 = sheet.createRow(sheet.getPhysicalNumberOfRows()+1);
                        sRow0.createCell(0).setCellValue(lastName + " " + firstName.substring(0, 1) + "." + secName.substring(0, 1) + ".");                        
                        sRow0.createCell(1).setCellValue(tab);
                        sRow0.createCell(2).setCellValue(in);
                        sRow0.createCell(3).setCellValue(in);
                        sRow0.createCell(4).setCellValue(out);
                        sRow0.createCell(5).setCellValue(out);
                        sRow0.createCell(6).setCellType(CellType.FORMULA);
                        sRow0.getCell(6).setCellFormula(
                                "SUM(-" + sRow0.getCell(3).getAddress().formatAsString() + "," 
                                        + sRow0.getCell(5).getAddress().formatAsString() + ")");
                        
                    }

                    List<Object[]> filtListIn = filtHist.stream().filter(obj -> ((String)obj[6]).toUpperCase().contains(IN_STR)).collect(Collectors.toList()); //Все входы за день
                    filtListIn.stream().map((obj) -> (Date) obj[0]).forEachOrdered((in) -> { //Для каждого входа
                        Date out = atEndOfDay(date); //Сначала считаем что не вышел
                        out.setTime(out.getTime() - 1000); //Поправка на Excel
                        //Ищем последующий выход
                        Optional<Object[]> firstOut = filtHist.stream().
                                filter(filtObj -> ((String)filtObj[6]).toUpperCase().contains(OUT_STR) &&
                                        ((Date) filtObj[0]).after(in)).findFirst();
                        if (firstOut.isPresent()){
                            out = (Date) firstOut.get()[0];
                        }

                        Row sRow = sheet.createRow(sheet.getPhysicalNumberOfRows()+1);
                        sRow.createCell(0).setCellValue(lastName + " " + firstName.substring(0, 1) + "." + secName.substring(0, 1) + ".");                        
                        sRow.createCell(1).setCellValue(tab);
                        sRow.createCell(2).setCellValue(in);
                        sRow.createCell(3).setCellValue(in);
                        sRow.createCell(4).setCellValue(out);
                        sRow.createCell(5).setCellValue(out);
                        sRow.createCell(6).setCellType(CellType.FORMULA);
                        sRow.getCell(6).setCellFormula(
                                "SUM(-" + sRow.getCell(3).getAddress().formatAsString() + "," 
                                        + sRow.getCell(5).getAddress().formatAsString() + ")");
                    });
                }
            });
        }
        
        
        //Стиль
        CellStyle cellStTBLR = workbook.createCellStyle();
        cellStTBLR.setBorderTop(BorderStyle.THIN);
        cellStTBLR.setBorderBottom(BorderStyle.THIN);
        cellStTBLR.setBorderLeft(BorderStyle.THIN);
        cellStTBLR.setBorderRight(BorderStyle.THIN);
        
        CreationHelper createHelper = workbook.getCreationHelper();
        CellStyle cellStTBLR_DF = workbook.createCellStyle();
        cellStTBLR_DF.setBorderTop(BorderStyle.THIN);
        cellStTBLR_DF.setBorderBottom(BorderStyle.THIN);
        cellStTBLR_DF.setBorderLeft(BorderStyle.THIN);
        cellStTBLR_DF.setBorderRight(BorderStyle.THIN);
        cellStTBLR_DF.setDataFormat(createHelper.createDataFormat().getFormat("DD.MM.YY"));
        
        CellStyle cellStTBLR_TF = workbook.createCellStyle();
        cellStTBLR_TF.setBorderTop(BorderStyle.THIN);
        cellStTBLR_TF.setBorderBottom(BorderStyle.THIN);
        cellStTBLR_TF.setBorderLeft(BorderStyle.THIN);
        cellStTBLR_TF.setBorderRight(BorderStyle.THIN);
        cellStTBLR_TF.setDataFormat(createHelper.createDataFormat().getFormat("hh:mm"));
        
        for(int k = 3; k <= sheet.getPhysicalNumberOfRows(); k++){
            Row sRow = sheet.getRow(k);            
            Cell cell0 = sRow.getCell(0) != null ? sRow.getCell(0):sRow.createCell(0);
            Cell cell1 = sRow.getCell(1) != null ? sRow.getCell(1):sRow.createCell(1);
            Cell cell2 = sRow.getCell(2) != null ? sRow.getCell(2):sRow.createCell(2);
            Cell cell3 = sRow.getCell(3) != null ? sRow.getCell(3):sRow.createCell(3);
            Cell cell4 = sRow.getCell(4) != null ? sRow.getCell(4):sRow.createCell(4);
            Cell cell5 = sRow.getCell(5) != null ? sRow.getCell(5):sRow.createCell(5);
            Cell cell6 = sRow.getCell(6) != null ? sRow.getCell(6):sRow.createCell(6);
            
            cell0.setCellStyle(cellStTBLR);
            cell1.setCellStyle(cellStTBLR);
            cell2.setCellStyle(cellStTBLR_DF);
            cell3.setCellStyle(cellStTBLR_TF);
            cell4.setCellStyle(cellStTBLR_DF);
            cell5.setCellStyle(cellStTBLR_TF);
            cell6.setCellStyle(cellStTBLR_TF);
        }
        sheet.setColumnWidth(0, 20*256);
        sheet.setColumnWidth(1, 15*256);
        sheet.setColumnWidth(2, 10*256);
        sheet.setColumnWidth(3, 10*256);
        sheet.setColumnWidth(4, 10*256);
        sheet.setColumnWidth(5, 10*256);
        sheet.setColumnWidth(6, 10*256);
        
        sheet.createFreezePane(0, 3, 0, 3);
        
        return sheet;
    }
    
    public UIComponent findComponent(final String id) {
        FacesContext context = FacesContext.getCurrentInstance(); 
        UIViewRoot root = context.getViewRoot();
        final UIComponent[] found = new UIComponent[1];

        root.visitTree(new FullVisitContext(context), (VisitContext context1, UIComponent component) -> {
            if(component.getId().equals(id)){
                found[0] = component;
                return VisitResult.COMPLETE;              
            }
            return VisitResult.ACCEPT;
        });
        return found[0];
    }
    
    public String showReport(DepartmentOpt departmentOpt){
        String retVal = "/views/report09/report09?faces-redirect=true";
        selectedDepartmentOpt = departmentOpt;
        if (getDate1() == null) setDate1(atStartOfMonth(new Date()));
        if (getDate2() == null) setDate2(atEndOfMonth(atEndOfDay(new Date())));
        
        org.primefaces.component.calendar.Calendar calDate2 = (org.primefaces.component.calendar.Calendar) findComponent("date2");
        calDate2.setValid(true);
        try {
            Validator validator = new DateRangeValidator();
            validator.validate(FacesContext.getCurrentInstance(), calDate2, getDate2());
        } catch (ValidatorException ex) {
            calDate2.setValid(false);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), "Отчет может содежать только 256 колонок"));
            return "";
        }
        
        String qlString = "SELECT history.genTime, ch.recordID, ch.lastName, ch.firstName, ch.note1, ch.note2, dev.name FROM History history "
                        + "LEFT JOIN history.cardHolder ch "
                        + "LEFT JOIN history.hWIndependentDevices dev "
                        + "WHERE history.deleted != 1 AND ch.deleted != 1 AND "
                            + "history.genTime BETWEEN :date1 AND :date2 AND ch.note5 = :departmentName AND "
                            + "(dev.name LIKE :devNameIn OR dev.name LIKE :devNameOUT) AND "
                            + "history.param1 = :param1 "
                        + "ORDER BY ch.lastName, ch.firstName, ch.note1, history.genTime";
        
        Query query = entityManager.createQuery(qlString);
        query.setParameter("date1", atStartOfDay(minDate(getDate1(), getDate2())));
        query.setParameter("date2", atEndOfDay(maxDate(getDate1(), getDate2())));
        query.setParameter("departmentName", departmentOpt.getDepartment());
        query.setParameter("devNameIn", "%вход%");
        query.setParameter("devNameOUT", "%выход%");
        query.setParameter("param1", 118);
        
        history = query.getResultList();
        report09Data.clear();
        
        int[] chIds = history.stream().mapToInt(value -> (int) value[1]).distinct().toArray(); // уникальные cardHolder из history
        List<Date> dateRange = getDistinctDateRange();
        for(int chId : chIds){
            Report09Data sRow0 = new Report09Data();
            Report09Data sRow1 = new Report09Data();
            Report09Data sRow2 = new Report09Data();
            
            //фильтруем историю по cardHolder
            List<Object[]> filtHist = history.stream().
                    filter(obj -> (int)obj[1] == chId).collect(Collectors.toList());

            if(filtHist.size() > 0){
                String lastName = String.valueOf(filtHist.get(0)[2]);
                String firstName = String.valueOf(filtHist.get(0)[3]);
                String secName = String.valueOf(filtHist.get(0)[4]);
                String tab = String.valueOf(filtHist.get(0)[5]);

                sRow0.setName(lastName + " " + firstName.substring(0, 1) + "." + secName.substring(0, 1) + ".");                        
                sRow0.setTabNumber(tab);
                sRow0.setDataInfo(Report09Data.DATA_INFO.IN);

                sRow1.setName(lastName + " " + firstName.substring(0, 1) + "." + secName.substring(0, 1) + ".");                        
                sRow1.setTabNumber(tab);
                sRow1.setDataInfo(Report09Data.DATA_INFO.OUT);

                sRow2.setName(lastName + " " + firstName.substring(0, 1) + "." + secName.substring(0, 1) + ".");                        
                sRow2.setTabNumber(tab);
                sRow2.setDataInfo(Report09Data.DATA_INFO.TOTAL);
                
                
                for(Date date : dateRange){
                    List<Object[]> filtDateHist = history.stream().
                            filter(obj -> (int)obj[1] == chId &&
                                    ((Date)obj[0]).after(atStartOfDay(date)) && ((Date)obj[0]).before(date)).
                                    collect(Collectors.toList());
                    
                    Date se = new Date(0);
                    Date in0 = se; //Вход ночь
                    Date in1 = se; //Вход день
                    Date out0 = se; //Выход ночь
                    Date out1 = se; //Выход день
                    
                    if (filtDateHist.isEmpty()){
                        sRow0.setNightTimeOnDate(date, in0);
                        sRow0.setDayTimeOnDate(date, in1);
                        sRow1.setNightTimeOnDate(date, out0);
                        sRow1.setDayTimeOnDate(date, out1);
                        sRow2.setNightTimeOnDate(date, out0);
                        sRow2.setDayTimeOnDate(date, out1);
                        continue;
                    }
                
                    //*** Выход/выход комбинации
                    String devName0 = ((String)filtDateHist.get(0)[6]).toUpperCase(); //Первое событие на дату
                    String devName1 = ((String)filtDateHist.get(filtDateHist.size() - 1)[6]).toUpperCase(); //Послед событие на дату
                    List<Object[]> filtListIn = filtDateHist.stream().filter(obj -> ((String)obj[6]).toUpperCase().contains(IN_STR)).collect(Collectors.toList()); //Все входы за день
                    List<Object[]> filtListOut = filtDateHist.stream().filter(obj -> ((String)obj[6]).toUpperCase().contains(OUT_STR)).collect(Collectors.toList()); //Все выходы за день
                                       
                    
                    //***Первое событие
                    //*** Вошел в начале
                    if (devName0.contains(IN_STR)){
                        in1 = (Date)filtDateHist.get(0)[0];
                        out1 = atEndOfDay(date);
                        out1.setTime(out1.getTime() - 1000); //поправка на эксель
                    }
                    //*** Вышел в начале
                    if (devName0.contains(OUT_STR)){
                        in0 = atStartOfDay(date);
                        out0 = (Date)filtDateHist.get(0)[0];
                    }
                    
                    //***Пследнее событие
                    //*** Вошел в конце
                    if (devName1.contains(IN_STR)){
                        in1 = (Date)filtDateHist.get(filtDateHist.size() - 1)[0];
                        out1 = atEndOfDay(date);
                        out1.setTime(out1.getTime() - 1000); //поправка на эксель
                    }
                    //*** Вышел в конце
                    if (devName1.contains(OUT_STR)){
                        out1 = (Date)filtDateHist.get(filtDateHist.size() - 1)[0];
                        if(in1.equals(se) && filtListIn.size() > 0){
                            in1 = (Date) filtListIn.get(filtListIn.size() - 1)[0];
                        }
                    }
                    
                    //*** Вошел не вышел - работает до конца суток
                    if (filtListIn.size() > 0 && filtListOut.isEmpty()){
                        in0 = se;
                        out0 = se;
                        in1 = (Date)filtListIn.get(0)[0];
                        out1 = atEndOfDay(date);
                        out1.setTime(out1.getTime() - 1000); //поправка на эксель
                    }                    
                    //*** Вышел не вошел - работает с начала суток и больше не входит
                    if (filtListOut.size() > 0 && filtListIn.isEmpty()){
                        in1 = se;
                        out1 = se;
                        in0 = atStartOfDay(date);
                        out0 = (Date)filtListOut.get(filtListOut.size() - 1)[0]; // послед. выход
                    }
                    
                    
                    sRow0.setNightTimeOnDate(date, in0);
                    sRow0.setDayTimeOnDate(date, in1);
                    
                    sRow1.setNightTimeOnDate(date, out0);
                    sRow1.setDayTimeOnDate(date, out1);
                    
                    LocalDateTime in0ldt = LocalDateTime.ofInstant(in0.toInstant(), ZoneId.systemDefault());
                    LocalDateTime out0ldt = LocalDateTime.ofInstant(out0.toInstant(), ZoneId.systemDefault());
                    
                    LocalDateTime in1ldt = LocalDateTime.ofInstant(in1.toInstant(), ZoneId.systemDefault());
                    LocalDateTime out1ldt = LocalDateTime.ofInstant(out1.toInstant(), ZoneId.systemDefault());
                    
                    //1. Дату входа в начало дня (к началу дня прибавим разницу)
                    //2. Добавим наносекунды к началу дня которые получим из Duration дат
                    //3. перегоним в дату
                    LocalDateTime drldt = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());                    
                    Date totalDayNight = Date.from(drldt.with(LocalTime.MIN).
                            plusNanos(Duration.between(in0ldt, out0ldt).toNanos()).
                            plusNanos(Duration.between(in1ldt, out1ldt).toNanos()).
                            atZone(ZoneId.systemDefault()).toInstant());
                    
                    sRow2.setNightTimeOnDate(date, se);
                    sRow2.setDayTimeOnDate(date, totalDayNight);
                }
            }
            
            report09Data.add(sRow0);
            report09Data.add(sRow1);
            report09Data.add(sRow2);
        }
        return retVal;
    }
   
    @Override
    public void postProcessXLS(Object document) {
        HSSFWorkbook workbook = (HSSFWorkbook) document;
        workbook.removeSheetAt(0);
        createSummaryTable(workbook, selectedDepartmentOpt.getDepartment(), getDistinctDateRange());
        createPassageTable(workbook, selectedDepartmentOpt.getDepartment(), getDistinctDateRange());
    }
    
    @Override
    public String getReportFileName(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy");
        return "Work sheet " + sdf.format(getDate1()) + "_" + sdf.format(getDate2());
    }
        
    public Path createExcelOnDepartment(DepartmentOpt departmentOpt, String sessionId) throws FileNotFoundException, IOException{        
        if (getDate1() == null) setDate1(atStartOfMonth(new Date()));
        if (getDate2() == null) setDate1(atEndOfMonth(atEndOfDay(new Date())));
        
        String qlString = "SELECT history.genTime, ch.recordID, ch.lastName, ch.firstName, ch.note1, ch.note2, dev.name FROM History history "
                        + "LEFT JOIN history.cardHolder ch "
                        + "LEFT JOIN history.hWIndependentDevices dev "
                        + "WHERE history.deleted != 1 AND ch.deleted != 1 AND "
                            + "history.genTime BETWEEN :date1 AND :date2 AND ch.note5 = :departmentName AND "
                            + "(dev.name LIKE :devNameIn OR dev.name LIKE :devNameOUT) AND "
                            + "history.param1 = :param1 "
                        + "ORDER BY ch.lastName, ch.firstName, ch.note1, history.genTime";
        
        Query query = entityManager.createQuery(qlString);
        query.setParameter("date1", atStartOfDay(minDate(getDate1(), getDate2())));
        query.setParameter("date2", atEndOfDay(maxDate(getDate1(), getDate2())));
        query.setParameter("departmentName", departmentOpt.getDepartment());
        query.setParameter("devNameIn", "%вход%");
        query.setParameter("devNameOUT", "%выход%");
        query.setParameter("param1", 118);
        
        history = query.getResultList();
        
        try (Workbook workbook = new HSSFWorkbook()) {
            createSummaryTable(workbook, departmentOpt.getDepartment(), getDistinctDateRange());
            createPassageTable(workbook, departmentOpt.getDepartment(), getDistinctDateRange());
            
            String tempFilePath = System.getProperty("java.io.tmpdir") + 
                    validName(departmentOpt.getDepartment() + "_" + sessionId + "_" + String.valueOf(departmentOpt.getId())) + ".xls";
            System.out.println(tempFilePath);
            try (FileOutputStream fileOut = new FileOutputStream(tempFilePath)) {
                workbook.write(fileOut);
                return Paths.get(tempFilePath);
            }
        }
    }
    
    public void createZIPFile() {
        org.primefaces.component.calendar.Calendar calDate2 = (org.primefaces.component.calendar.Calendar) findComponent("date2");
        calDate2.setValid(true);
        try {
            Validator validator = new DateRangeValidator();
            validator.validate(FacesContext.getCurrentInstance(), calDate2, getDate2());
        } catch (ValidatorException ex) {
            calDate2.setValid(false);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), "Отчет может содежать только 256 колонок"));
        }
        
        
        final String sesId = FacesContext.getCurrentInstance().getExternalContext().getSessionId(false);        
        CopyOnWriteArrayList<Path> tempFiles = new CopyOnWriteArrayList();
        departmentOpt.forEach((_item) -> {
            try {
                tempFiles.add(createExcelOnDepartment(_item, sesId));
            } catch (IOException ex) {
                Logger.getLogger(Report09.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        /*final ExecutorService executorService = Executors.newCachedThreadPool();
        departmentOpt.forEach((_item) -> {
            executorService.submit(() -> {
                try {
                    tempFiles.add(createExcelOnDepartment(_item, sesId));
                } catch (IOException ex) {
                    Logger.getLogger(Report09.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        });
        executorService.shutdown();
        try {
            executorService.awaitTermination(5, TimeUnit.MINUTES);
        } catch (InterruptedException ex) {
            Logger.getLogger(Report09.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        
        String tempZip = System.getProperty("java.io.tmpdir") + File.separator + "Reports_" + sesId + ".zip";
        try{
            if (Files.exists(Paths.get(tempZip), LinkOption.NOFOLLOW_LINKS))
                Files.delete(Paths.get(tempZip));
            
            Path zip = Files.createFile(Paths.get(tempZip));
            try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(zip))){
                for (Path tempExcel : tempFiles){                    
                    String filename = tempExcel.getFileName().toString();
                    Matcher matcher = Pattern.compile("[0-9]*.xls").matcher(filename);
                    long id = 0l;
                    while (matcher.find()) {                        
                        id = Long.valueOf(filename.substring(matcher.start(), filename.length() - 4));
                    }
                    String exZipName = filename.replaceFirst("_" + sesId + (id > 0 ? "_" + String.valueOf(id) : ""), "");
                    final long fid = id;
                    Optional<DepartmentOpt> depOpt = departmentOpt.stream().filter(dp -> dp.getId() == fid).findFirst();
                    
                    if (depOpt.isPresent()){
                        exZipName = depOpt.get().getPathDir() + exZipName;
                    }
                    Path excel = Paths.get(exZipName);
                    Files.walk(tempExcel).filter(path -> !Files.isDirectory(path)).forEach(path -> {
                        ZipEntry zipEntry = new ZipEntry(excel.toString());
                        try {
                            zs.putNextEntry(zipEntry);
                            Files.copy(path, zs);
                            zs.closeEntry();
                        } catch (IOException ex) {
                            Logger.getLogger(Report09.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
                }
            }
            
            HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
            response.setContentType("application/x-msdownload");
            response.setHeader("Content-disposition", "attachment; filename=Reports.zip");
            response.setContentLength((int) zip.toFile().length());
            try (ServletOutputStream out = response.getOutputStream(); FileInputStream input = new FileInputStream(zip.toFile());) {
                byte[] buffer = new byte[1024];
                int i = 0;
                while ((i = input.read(buffer)) != -1) {
                    out.write(buffer);
                    out.flush();
                }
                if(FacesContext.getCurrentInstance().getResponseComplete()){
                    /*tempFilePathZip.deleteOnExit();
                    for (File tf : fileArray)
                        tf.deleteOnExit();*/
                }
            }
        }catch (IOException ex) {
            Logger.getLogger(Report09.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String showReport() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
