package ru.it_mm.winpak.views.report6;

import com.sun.faces.component.visit.FullVisitContext;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitResult;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import ru.it_mm.winpak.utils.DateRangeValidator;
import ru.it_mm.winpak.utils.Report;
import static ru.it_mm.winpak.utils.Report.IN_STR;
import static ru.it_mm.winpak.utils.Report.OUT_STR;
import ru.it_mm.winpak.utils.Utils;
import static ru.it_mm.winpak.utils.Utils.atEndOfDay;
import static ru.it_mm.winpak.utils.Utils.atStartOfDay;
import static ru.it_mm.winpak.utils.Utils.maxDate;
import static ru.it_mm.winpak.utils.Utils.minDate;
import ru.it_mm.winpak.utils.entity.CardHolder;

@Named
@SessionScoped
public class Report06 extends Report{
    private static final long serialVersionUID = 61626364L;
    public enum FIELDS {
        //Порядок ВАЖЕН!!! В таком поядке поля будут отражаться в p:selectManyCheckbox layout="grid"
        //В p:dataTable порядок полей отсортируем по sortOrder
        LAST_NAME("Фамилия", "lastName", "cardHolder", 150, 1),            NOTE12("Семейное положение", "note12", "cardHolder", 200, 15),
        FIRST_NAME("Имя", "firstName", "cardHolder", 150, 2),              NOTE13("Образование", "note13", "cardHolder", 150, 16),
        NOTE1("Отчество", "note1", "cardHolder", 150, 3),                  NOTE14("Серия номер паспорта", "note14", "cardHolder", 100, 17),
        CARD_NUMBER("Номер карты", "cardNumber", "card", 100, 4),          NOTE15("Кем выдан", "note15", "cardHolder", 250, 18),
        NOTE2("Табельный номер", "note2", "cardHolder", 100, 5),           NOTE16("Дата выдачи", "note16", "cardHolder", 100, 19),                                                        
        NOTE3("Цех", "note3", "cardHolder", 200, 6),                       NOTE17("Дата приема на работу", "note17", "cardHolder", 100, 20),                                            
        NOTE4("Служба", "note4", "cardHolder", 200, 7),                    NOTE18("Характер работы", "note18", "cardHolder", 150, 21),
        NOTE5("Цех(полн. наименован.)", "note5", "cardHolder", 200, 8),    NOTE19("Номер приказа об увольнении", "note19", "cardHolder", 100, 22),                     
        NOTE6("Участок", "note6", "cardHolder", 200, 9),                   NOTE20("Дата приказа об увольнении", "note20", "cardHolder", 100, 23),  
        NOTE7("Должность", "note7", "cardHolder", 250, 10),                NOTE21("Дата увольнения", "note21", "cardHolder", 100, 24),
        NOTE8("Дата рождения", "note8", "cardHolder", 100, 11),            NOTE22("Должность сокр", "note22", "cardHolder", 150, 25),     
        NOTE9("Место рождения", "note9", "cardHolder", 250, 12),           NOTE23("Режим работы", "note23", "cardHolder", 200, 26),     
        NOTE10("Место прописки", "note10", "cardHolder", 250, 13),         NOTE24("Заказчик", "note24", "cardHolder", 150, 27),   
        NOTE11("Место проживания", "note11", "cardHolder", 250, 14);
                                                                            
        
        private final String header;
        private final String property;        
        private final String tableName;
        private final int width;
        private final int sortOrder;
        
        FIELDS(String header, String property, String tableName,int width, int sortOrder){
            this.header = header;
            this.property = property;
            this.tableName = tableName;
            this.width = width;
            this.sortOrder = sortOrder;
        }
        
        public String getHeader() { return header; }
        public String getProperty() { return property; }
        public String getTableName() {return tableName;}
        public int getWidth() { return width; }
        public int getSortOrder() { return sortOrder; }
    }
    
    private Date period1;
    private Date period2;
    
    private String lastName;
    private String firstName;
    private String note1;
    private String cardNumber;
    private String note5;
    
    private boolean notCheckHolidays;
    private boolean inWholePeriod;
    
    private List<SelectItem> fields;
    private List<FIELDS> selected_fields;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    private List<Report06Data> report06Data;
    
    public Report06(){}
    
    @PostConstruct
    @Override
    public void init() {
        setDate1(atStartOfDay(new Date()));
        setDate2(new Date());
        period1 = atStartOfDay(new Date());
        period2 = new Date();
        
        fields = new ArrayList<>();
        for(FIELDS f : FIELDS.values()){
            fields.add(new SelectItem(f, f.getHeader()));
        }
        
        selected_fields = new ArrayList<>();
        selected_fields.add(FIELDS.LAST_NAME);
        selected_fields.add(FIELDS.FIRST_NAME);
        selected_fields.add(FIELDS.NOTE1);
        selected_fields.add(FIELDS.NOTE2);
        selected_fields.add(FIELDS.CARD_NUMBER);
        selected_fields.add(FIELDS.NOTE3);
        selected_fields.add(FIELDS.NOTE4);
        selected_fields.add(FIELDS.NOTE5);
        selected_fields.add(FIELDS.NOTE6);
        selected_fields.add(FIELDS.NOTE7);
        
        report06Data = new ArrayList<>();
    }

    public Date getPeriod1() {
        return period1;
    }
    public void setPeriod1(Date period1) {
        this.period1 = period1;
    }

    public Date getPeriod2() {
        return period2;
    }
    public void setPeriod2(Date period2) {
        this.period2 = period2;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getNote1() {
        return note1;
    }
    public void setNote1(String note1) {
        this.note1 = note1;
    }

    public String getCardNumber() {
        return cardNumber;
    }
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getNote5() {
        return note5;
    }
    public void setNote5(String note5) {
        this.note5 = note5;
    }

    public List<SelectItem> getFields() {
        return fields;
    }
    public void setFields(List<SelectItem> fields) {
        this.fields = fields;
    }

    public List<FIELDS> getSelected_fields() {
        return selected_fields;
    }
    public void setSelected_fields(List<FIELDS> selected_fields) {
        this.selected_fields = selected_fields;
    }

    public boolean isNotCheckHolidays() {
        return notCheckHolidays;
    }
    public void setNotCheckHolidays(boolean notCheckHolidays) {
        this.notCheckHolidays = notCheckHolidays;
    }

    public boolean isInWholePeriod() {
        return inWholePeriod;
    }
    public void setInWholePeriod(boolean inWholePeriod) {
        this.inWholePeriod = inWholePeriod;
    }

    public List<Report06Data> getReport06Data() {
        return report06Data;
    }
    public void setReport06Data(List<Report06Data> report06Data) {
        this.report06Data = report06Data;
    }
        
    @Override
    public String getReportFileName() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy");
        return "Missing " + sdf.format(getDate1()) + "_" + sdf.format(getDate2());
    }
    
    public List<String> completeLastName(String lastNamePat) {
        String qlString = "SELECT DISTINCT cardHolder.lastName FROM CardHolder cardHolder "
                + "WHERE cardHolder.deleted = 0 AND cardHolder.lastName LIKE :lastNamePat";
        Query query = entityManager.createQuery(qlString);
        query.setParameter("lastNamePat", "%" + lastNamePat + "%");
        query.setMaxResults(10);
        return query.getResultList();
    }
    
    public List<String> completeFirstName(String firstNamePat) {
        String qlString = "SELECT DISTINCT cardHolder.firstName FROM CardHolder cardHolder "
                + "WHERE cardHolder.deleted = 0 AND cardHolder.firstName LIKE :firstNamePat";
        Query query = entityManager.createQuery(qlString);
        query.setParameter("firstNamePat", "%" + firstNamePat + "%");
        query.setMaxResults(10);
        return query.getResultList();
    }
    
    public List<String> completeNote1(String note1Pat) {
        String qlString = "SELECT DISTINCT cardHolder.note1 FROM CardHolder cardHolder "
                + "WHERE cardHolder.deleted = 0 AND cardHolder.note1 LIKE :note1Pat";
        Query query = entityManager.createQuery(qlString);
        query.setParameter("note1Pat", "%" + note1Pat + "%");
        query.setMaxResults(10);
        return query.getResultList();
    }
    
    public List<String> completeNote5(String note5Pat) {
        String qlString = "SELECT DISTINCT cardHolder.note5 FROM CardHolder cardHolder "
                + "WHERE cardHolder.deleted = 0 AND cardHolder.note5 LIKE :note5Pat";
        Query query = entityManager.createQuery(qlString);
        query.setParameter("note5Pat", "%" + note5Pat + "%");
        query.setMaxResults(10);
        return query.getResultList();
    }
    
    public List<String> completeCardNumber(String cardNumberPat) {
        String qlString = "SELECT DISTINCT card.cardNumber FROM Card card "
                + "WHERE card.deleted != 1 AND card.cardStatus = 1 AND card.cardNumber LIKE :cardNumberPat";
        Query query = entityManager.createQuery(qlString);
        query.setParameter("cardNumberPat", "%" + cardNumberPat + "%");
        query.setMaxResults(10);
        return query.getResultList();
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
    
    public List<Date> getDateRange() {
        Date startRage = atEndOfDay(minDate(getDate1(), getDate2()));
        Date endRage = atEndOfDay(maxDate(getDate1(), getDate2()));

        List<Date> dateRange = new ArrayList<>();
        dateRange.add(startRage);
        while (atEndOfDay(startRage).before(endRage)) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(startRage);
            cal.add(Calendar.DATE, 1);
            startRage = cal.getTime();
            if (notCheckHolidays && Utils.isHoliday(startRage)) continue;
            dateRange.add(startRage);
        }
        return dateRange;
    }
    
    public boolean isHoliday(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).getDayOfWeek().getValue() > 5;
    }
    
    @Override
    public String showReport() {
        String retVal = "/views/report06/report06?faces-redirect=true";
        
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
        
        report06Data.clear();
        
        selected_fields.sort((FIELDS o1, FIELDS o2) -> {
            return o2.sortOrder > o1.sortOrder ? -1 : (o2.sortOrder < o1.sortOrder) ? 1 : 0;
        });
        
        StringBuilder sbF = new StringBuilder("");
        selected_fields.forEach((field) -> {
            sbF.append(field.getTableName()).append(".").append(field.getProperty()).append(", ");
        });
        sbF.replace(sbF.length()-2, sbF.length()-1, "");
        
        String qlCards = "SELECT card.recordID, cardHolder.recordID, " + sbF.toString() + " FROM Card card "
                        + "INNER JOIN card.cardHolder cardHolder "
                        + "WHERE card.deleted != 1 AND card.cardStatus = 1 AND cardHolder.deleted != 1 "
                            + (lastName != null && lastName.trim().length() > 0 ? "AND cardHolder.lastName LIKE :lastName " : "")
                            + (firstName != null && firstName.trim().length() > 0 ? "AND cardHolder.firstName LIKE :firstName " : "")
                            + (note1 != null && note1.trim().length() > 0 ? "AND cardHolder.note1 LIKE :note1 " : "")
                            + (note5 != null && note5.trim().length() > 0 ? "AND cardHolder.note5 LIKE :note5 " : "")
                            + (cardNumber != null && cardNumber.trim().length() > 0 ? "AND card.cardNumber LIKE :cardNumber " : "")
                        + "ORDER BY cardHolder.lastName, cardHolder.firstName, cardHolder.note1";
        
        Query queryCards = entityManager.createQuery(qlCards);
        if (lastName != null && lastName.trim().length() > 0)
            queryCards.setParameter("lastName", "%" + lastName.trim() + "%");
        if (firstName != null && firstName.trim().length() > 0)
            queryCards.setParameter("firstName", "%" + firstName.trim() + "%");
        if (note1 != null && note1.trim().length() > 0)
            queryCards.setParameter("note1", "%" + note1.trim() + "%");
        if (note5 != null && note5.trim().length() > 0)
            queryCards.setParameter("note5", "%" + note5.trim() + "%");
        if (cardNumber != null && cardNumber.trim().length() > 0)
            queryCards.setParameter("cardNumber", "%" + cardNumber.trim() + "%");
        
        List<Object[]> allCards = queryCards.getResultList();
        
        String qlString = "SELECT history.genTime gt, cardHolder.recordID, dev.name, card.recordID FROM History history "
                        + "LEFT JOIN history.cardHolder cardHolder "
                        + "LEFT JOIN history.hWIndependentDevices dev "
                        + "LEFT JOIN history.card card "
                        + "WHERE history.deleted != 1 AND cardHolder.deleted != 1 AND "
                            + "history.genTime BETWEEN :date1 AND :date2 AND "
                            + "(dev.name LIKE :devNameIn OR dev.name LIKE :devNameOut) AND "
                            + "history.param1 = :param1 "
                            + (lastName != null && lastName.trim().length() > 0 ? "AND cardHolder.lastName LIKE :lastName " : "")
                            + (firstName != null && firstName.trim().length() > 0 ? "AND cardHolder.firstName LIKE :firstName " : "")
                            + (note1 != null && note1.trim().length() > 0 ? "AND cardHolder.note1 LIKE :note1 " : "")
                            + (note5 != null && note5.trim().length() > 0 ? "AND cardHolder.note5 LIKE :note5 " : "")
                            + (cardNumber != null && cardNumber.trim().length() > 0 ? "AND history.param3 LIKE :cardNumber " : "")
                        + "ORDER BY cardHolder.lastName, cardHolder.firstName, cardHolder.note1, history.genTime";
        
        Query query = entityManager.createQuery(qlString);
        query.setParameter("date1", minDate(getDate1(), getDate2()));
        query.setParameter("date2", maxDate(getDate1(), getDate2()));
        query.setParameter("devNameIn", "%" + IN_STR + "%");
        query.setParameter("devNameOut", "%" + OUT_STR + "%");
        query.setParameter("param1", 118);
        if (lastName != null && lastName.trim().length() > 0)
            query.setParameter("lastName", "%" + lastName.trim() + "%");
        if (firstName != null && firstName.trim().length() > 0)
            query.setParameter("firstName", "%" + firstName.trim() + "%");
        if (note1 != null && note1.trim().length() > 0)
            query.setParameter("note1", "%" + note1.trim() + "%");
        if (note5 != null && note5.trim().length() > 0)
            query.setParameter("note5", "%" + note5.trim() + "%");
        if (cardNumber != null && cardNumber.trim().length() > 0)
            query.setParameter("cardNumber", "%" + cardNumber.trim() + "%");
        
        List<Object[]> history = query.getResultList();
        
        List<FIELDS> chSF = selected_fields.stream().
            filter((field) -> field.getTableName().contains("cardHolder")).collect(Collectors.toList());
        
        List<Date> dateRange = getDateRange();
        for(Object[] cardObj : allCards){
            Report06Data r06Data = new Report06Data();
            //CardHolder
            CardHolder cardHolder = new CardHolder((Integer) cardObj[1]);
            chSF.stream().forEachOrdered((field) -> {
                try {
                    java.lang.reflect.Field fieldCalss = cardHolder.getClass().getDeclaredField(field.getProperty());
                    fieldCalss.setAccessible(true);
                    fieldCalss.set(cardHolder, cardObj[selected_fields.indexOf(field) + 2]);
                } catch (SecurityException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException ex) {}
            });
            r06Data.setCardHolder(cardHolder);
            if (selected_fields.contains(FIELDS.CARD_NUMBER))
                r06Data.setCardNumber(((String) cardObj[selected_fields.indexOf(FIELDS.CARD_NUMBER) + 2]).trim());
            
            int totalMissDay = 0;
            for(Date date : dateRange){
                //фильтруем историю по card
                List<Object[]> filtHist = history.stream().
                        filter(obj -> (int)obj[3] == (int)cardObj[0] &&
                                ((Date)obj[0]).after(atStartOfDay(date)) && ((Date)obj[0]).before(date)).collect(Collectors.toList());
                if (filtHist.isEmpty()) {
                    r06Data.setCountEventOnDate(date, "");
                    totalMissDay += 1;
                }else{
                    long filtListIn = filtHist.stream().filter(obj -> ((String)obj[2]).toUpperCase().contains(IN_STR)).count();
                    long filtListOut = filtHist.stream().filter(obj -> ((String)obj[2]).toUpperCase().contains(OUT_STR)).count();
                    r06Data.setCountEventOnDate(date, String.format("%3d/%3d", filtListIn, filtListOut));
                }
            };
            r06Data.setTotalMissDay(totalMissDay);
            if (inWholePeriod && totalMissDay != dateRange.size()) continue; //Только за весь период
            report06Data.add(r06Data);
        }
        return retVal;
    }
    
    @Override
    public void postProcessXLS(Object document) {
        super.postProcessXLS(document);
        
        HSSFWorkbook workbook = (HSSFWorkbook) document;
        HSSFSheet sheet = workbook.getSheetAt(0);
        
        Font holidayHeaderFont = workbook.createFont();
        holidayHeaderFont.setBold(true);
        holidayHeaderFont.setFontHeightInPoints((short) 12);
        holidayHeaderFont.setColor(IndexedColors.RED.getIndex());

        CellStyle tableHeaderHolidayCellStyle = workbook.createCellStyle();
        tableHeaderHolidayCellStyle.setAlignment(HorizontalAlignment.CENTER);
        tableHeaderHolidayCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        tableHeaderHolidayCellStyle.setFont(holidayHeaderFont);
        tableHeaderHolidayCellStyle.setBorderTop(BorderStyle.MEDIUM);
        tableHeaderHolidayCellStyle.setBorderBottom(BorderStyle.MEDIUM);
        tableHeaderHolidayCellStyle.setBorderLeft(BorderStyle.MEDIUM);
        tableHeaderHolidayCellStyle.setBorderRight(BorderStyle.MEDIUM);
        
        List<Date> dateRange = getDateRange();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM");
        dateRange.forEach((date) -> {
            sheet.getRow(2).getCell(selected_fields.size() + dateRange.indexOf(date)).setCellValue(sdf.format(date));
            if(isHoliday(date))
                sheet.getRow(2).getCell(selected_fields.size() + dateRange.indexOf(date)).setCellStyle(tableHeaderHolidayCellStyle);
            
            sheet.setColumnWidth(selected_fields.size() + dateRange.indexOf(date), 15*256);
        });        
        
        
        HSSFPalette palette = workbook.getCustomPalette();
        palette.setColorAtIndex(IndexedColors.AQUA.index,
                    (byte) 255,  //RGB red (0-255)
                    (byte) 221,    //RGB green
                    (byte) 211     //RGB blue
        );
        
        CellStyle tableMissCS = workbook.createCellStyle();
        tableMissCS.setFillForegroundColor(palette.getColor(IndexedColors.AQUA.index).getIndex());
        tableMissCS.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        for(int r = 3; r <= sheet.getLastRowNum(); r++){
            for(int c = selected_fields.size(); c <= sheet.getRow(r).getLastCellNum() - 2; c++){
                HSSFCell cell = sheet.getRow(r).getCell(c);
                if(cell.getStringCellValue().isEmpty())
                    cell.setCellStyle(tableMissCS);
            }
        }
    }
}
