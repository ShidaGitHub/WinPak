package ru.it_mm.winpak.views.report5;

import com.sun.faces.component.visit.FullVisitContext;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import ru.it_mm.winpak.utils.DateRangeValidator;
import ru.it_mm.winpak.utils.Report;
import static ru.it_mm.winpak.utils.Report.IN_STR;
import static ru.it_mm.winpak.utils.Report.OUT_STR;
import static ru.it_mm.winpak.utils.Utils.atEndOfDay;
import static ru.it_mm.winpak.utils.Utils.atStartOfDay;
import static ru.it_mm.winpak.utils.Utils.maxDate;
import static ru.it_mm.winpak.utils.Utils.minDate;
import ru.it_mm.winpak.utils.entity.CardHolder;

@Named
@SessionScoped
public class Report05 extends Report{
    private static final long serialVersionUID = 51525354L;
    public enum FIELDS {
        //Порядок ВАЖЕН!!! В таком поядке поля будут отражаться в p:selectManyCheckbox layout="grid"
        //В p:dataTable порядок полей отсортируем по sortOrder
        LAST_NAME("Фамилия", "lastName", "cardHolder", 100, 1),             NOTE12("Семейное положение", "note12", "cardHolder", 200, 15),
        FIRST_NAME("Имя", "firstName", "cardHolder", 100, 2),               NOTE13("Образование", "note13", "cardHolder", 150, 16),
        NOTE1("Отчество", "note1", "cardHolder", 100, 3),                   NOTE14("Серия номер паспорта", "note14", "cardHolder", 100, 17),
        CARD_NUMBER("Номер карты", "param3", "history", 100, 4),            NOTE15("Кем выдан", "note15", "cardHolder", 250, 18),
        NOTE2("Табельный номер", "note2", "cardHolder", 100, 5),            NOTE16("Дата выдачи", "note16", "cardHolder", 100, 19),
        NOTE3("Цех", "note3", "cardHolder", 100, 6),                        NOTE17("Дата приема на работу", "note17", "cardHolder", 100, 20),
        NOTE4("Служба", "note4", "cardHolder", 200, 7),                     NOTE18("Характер работы", "note18", "cardHolder", 250, 21),
        NOTE5("Цех(полн. наименован.)", "note5", "cardHolder", 200, 8),     NOTE19("Номер приказа об увольнении", "note19", "cardHolder", 100, 22),
        NOTE6("Участок", "note6", "cardHolder", 200, 9),                    NOTE20("Дата приказа об увольнении", "note20", "cardHolder", 100, 23), 
        NOTE7("Должность", "note7", "cardHolder", 250, 10),                 NOTE21("Дата увольнения", "note21", "cardHolder", 100, 24),
        NOTE8("Дата рождения", "note8", "cardHolder", 100, 11),             NOTE22("Должность сокр", "note22", "cardHolder", 150, 25), 
        NOTE9("Место рождения", "note9", "cardHolder", 250, 12),            NOTE23("Режим работы", "note23", "cardHolder", 150, 26),                                                        
        NOTE10("Место прописки", "note10", "cardHolder", 250, 13),          NOTE24("Заказчик", "note24", "cardHolder", 150, 27),
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

    private String lastName;
    private String firstName;
    private String note1;
    private String cardNumber;
    private String note5;
    
    private List<SelectItem> fields;
    private List<FIELDS> selected_fields;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    private List<Report05Data> report05Data;
    
    @PostConstruct
    @Override
    public void init() {
        setDate1(atStartOfDay(new Date()));
        setDate2(new Date());
        
        fields = new ArrayList<>();
        for(FIELDS f : FIELDS.values()){
            fields.add(new SelectItem(f, f.getHeader()));
        }
        
        report05Data = new ArrayList<>();
        
        selected_fields = new ArrayList<>();
        selected_fields.add(FIELDS.LAST_NAME);
        selected_fields.add(FIELDS.FIRST_NAME);
        selected_fields.add(FIELDS.NOTE1);
        selected_fields.add(FIELDS.CARD_NUMBER);
        selected_fields.add(FIELDS.NOTE2);
        selected_fields.add(FIELDS.NOTE3);
        selected_fields.add(FIELDS.NOTE4);
        selected_fields.add(FIELDS.NOTE5);
        selected_fields.add(FIELDS.NOTE6);
        selected_fields.add(FIELDS.NOTE7);
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

    public List<Report05Data> getReport05Data() {
        return report05Data;
    }
    public void setReport05Data(List<Report05Data> report05Data) {
        this.report05Data = report05Data;
    }
    
    public boolean isHoliday(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).getDayOfWeek().getValue() > 5;
    }
    
    @Override
    public String getReportFileName() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy");
        return "Time recording " + sdf.format(getDate1()) + "_" + sdf.format(getDate2());
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
                + "WHERE card.deleted = 0 AND card.cardNumber LIKE :cardNumberPat";
        Query query = entityManager.createQuery(qlString);
        query.setParameter("cardNumberPat", "%" + cardNumberPat + "%");
        query.setMaxResults(10);
        return query.getResultList();
    }
    
    
    public List<Date> getDateRange() {
        Date startRage = atEndOfDay(minDate(getDate1(), getDate2()));
        Date endRage = atEndOfDay(maxDate(getDate1(), getDate2()));

        List<Date> dateRange = new ArrayList<>();
        dateRange.add(startRage);
        dateRange.add(startRage);
        dateRange.add(startRage);
        dateRange.add(startRage);
        while (atEndOfDay(startRage).before(endRage)) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(startRage);
            cal.add(Calendar.DATE, 1);
            startRage = cal.getTime();
            dateRange.add(startRage);
            dateRange.add(startRage);
            dateRange.add(startRage);
            dateRange.add(startRage);
        }
        return dateRange;
    }
    public List<Date> getDistinctDateRange(){
        return getDateRange().stream().distinct().collect(Collectors.toList());
    }
    
    public String getHeaderOnDate(int index){
        int d = (int) Math.floor(index / 4);
        switch(index - d * 4){
            case 0: return "Вход";
            case 1: return "Выход";
            case 2: return "Всего";
            case 3: return "Вх/Вых";
            default: return "";
        }
    }
    
    public boolean renderedCol(String colName){
        return selected_fields.stream().filter((field) -> field.getProperty().equals(colName)).count() > 0;
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
    
    @Override
    public String showReport() {
        String retVal = "/views/report05/report05?faces-redirect=true";
        
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
        
        report05Data.clear();
        
        if (selected_fields.isEmpty()) selected_fields.add(FIELDS.LAST_NAME);
        
        selected_fields.sort((FIELDS o1, FIELDS o2) -> {
            return o2.sortOrder > o1.sortOrder ? -1 : (o2.sortOrder < o1.sortOrder) ? 1 : 0;
        });
        
        StringBuilder sbF = new StringBuilder("");
        selected_fields.forEach((field) -> {
            if (field.getTableName().length() == 0) return;            
            sbF.append(field.getTableName()).append(".").append(field.getProperty()).append(", ");
        });
        sbF.replace(sbF.length()-2, sbF.length()-1, "");
        
        String qlString = "SELECT history.genTime gt, cardHolder.recordID, dev.name, " + sbF.toString() + " FROM History history "
                        + "LEFT JOIN history.cardHolder cardHolder "
                        + "LEFT JOIN history.hWIndependentDevices dev "
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
        Date se = new Date(0);
        
        int[] chIds = history.stream().mapToInt(value -> (int) value[1]).distinct().toArray(); // уникальные cardHolder из history
        List<Date> dateRange = getDistinctDateRange();
        for(int chId : chIds){
            //фильтруем историю по cardHolder
            List<Object[]> filtHist = history.stream().
                    filter(obj -> (int)obj[1] == chId).collect(Collectors.toList());
            
            Report05Data r05dDay = new Report05Data();
            Report05Data r05dNight = new Report05Data();
            
            r05dDay.setDataInfo(Report05Data.DATA_INFO.DAY);
            r05dNight.setDataInfo(Report05Data.DATA_INFO.NIGHT);
            
            //CardHolder
            CardHolder cardHolder = new CardHolder((Integer) filtHist.get(0)[1]);
            chSF.stream().forEachOrdered((field) -> {
                try {
                    java.lang.reflect.Field fieldCalss = cardHolder.getClass().getDeclaredField(field.getProperty());
                    fieldCalss.setAccessible(true);
                    fieldCalss.set(cardHolder, filtHist.get(0)[selected_fields.indexOf(field) + 3]);
                } catch (SecurityException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException ex) {}
            });
            r05dDay.setCardHolder(cardHolder);
            r05dNight.setCardHolder(cardHolder);
            if (selected_fields.contains(FIELDS.CARD_NUMBER)){
                r05dDay.setCardNumber(String.valueOf(filtHist.get(0)[selected_fields.indexOf(FIELDS.CARD_NUMBER) + 3]));
                r05dNight.setCardNumber(String.valueOf(filtHist.get(0)[selected_fields.indexOf(FIELDS.CARD_NUMBER) + 3]));
            }
            
            for(Date date : dateRange){
                List<Object[]> filtDateHist = history.stream().
                    filter(obj -> (int)obj[1] == chId &&
                        ((Date)obj[0]).after(atStartOfDay(date)) && ((Date)obj[0]).before(date)).
                        collect(Collectors.toList());
                
                Date in0 = se; //Вход ночь
                Date in1 = se; //Вход день
                Date out0 = se; //Выход ночь
                Date out1 = se; //Выход день
                    
                if (filtDateHist.isEmpty()){
                    r05dNight.setInTimeOnDate(date, in0);
                    r05dNight.setOutTimeOnDate(date, out0);
                    r05dNight.setTotalTimeOnDate(date, out0);
                    r05dNight.setCountEventOnDate(date, "");
                    r05dDay.setInTimeOnDate(date, in1);
                    r05dDay.setOutTimeOnDate(date, out1);
                    r05dDay.setTotalTimeOnDate(date, out0);
                    r05dDay.setCountEventOnDate(date, "");
                    continue;
                }
                
                //*** Выход/выход комбинации
                String devName0 = ((String)filtDateHist.get(0)[2]).toUpperCase(); //Первое событие на дату
                String devName1 = ((String)filtDateHist.get(filtDateHist.size() - 1)[2]).toUpperCase(); //Послед событие на дату
                List<Object[]> filtListIn = filtDateHist.stream().filter(obj -> ((String)obj[2]).toUpperCase().contains(IN_STR)).collect(Collectors.toList()); //Все входы за день
                List<Object[]> filtListOut = filtDateHist.stream().filter(obj -> ((String)obj[2]).toUpperCase().contains(OUT_STR)).collect(Collectors.toList()); //Все выходы за день
                    
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
                   
                r05dNight.setInTimeOnDate(date, in0);
                r05dNight.setOutTimeOnDate(date, out0);
                r05dDay.setInTimeOnDate(date, in1);
                r05dDay.setOutTimeOnDate(date, out1);
                
                //Итог на день
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
                
                r05dNight.setTotalTimeOnDate(date, se);
                r05dDay.setTotalTimeOnDate(date, totalDayNight);
                
                r05dNight.setCountEventOnDate(date, "");
                r05dDay.setCountEventOnDate(date, String.format("%3d/%3d", filtListIn.size(), filtListOut.size()));
            }
            report05Data.add(r05dNight);
            report05Data.add(r05dDay);
        }
        return retVal;
    }
    
    @Override
    public void postProcessXLS(Object document) {
        super.postProcessXLS(document);
        
        HSSFWorkbook wb = (HSSFWorkbook) document;
        HSSFSheet sheet = wb.getSheetAt(0);
        sheet.shiftRows(3, sheet.getLastRowNum(), 1);
        //заморозка шапки
        sheet.createFreezePane(1, 4, 1, 4);
        
        Font cg = wb.createFont();
        cg.setBold(true);
        cg.setFontHeightInPoints((short) 12);
        
        CellStyle chHeaderStyle = wb.createCellStyle();
        chHeaderStyle.setFont(cg);
        chHeaderStyle.setAlignment(HorizontalAlignment.CENTER);
        chHeaderStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        chHeaderStyle.setBorderTop(BorderStyle.MEDIUM);
        chHeaderStyle.setBorderBottom(BorderStyle.MEDIUM);
        chHeaderStyle.setBorderLeft(BorderStyle.MEDIUM);
        chHeaderStyle.setBorderRight(BorderStyle.MEDIUM);
        
        //стиль шапки + объед 2 и 3 строки до дат
        for(int i = 0; i <= selected_fields.size(); i++){
            sheet.addMergedRegion(new CellRangeAddress(2, 3, i, i));
            sheet.getRow(3).createCell(i).setCellStyle(chHeaderStyle);
        }
        //обединение дат
        for(int i = selected_fields.size() + 1; i <= sheet.getRow(2).getLastCellNum() - 6; i+=4){
            sheet.addMergedRegion(new CellRangeAddress(2, 2, i, i+3));
        }
        
        //обединение итогов
        sheet.addMergedRegion(new CellRangeAddress(2, 3, sheet.getRow(2).getLastCellNum()-2, sheet.getRow(2).getLastCellNum()-2));
        sheet.addMergedRegion(new CellRangeAddress(2, 3, sheet.getRow(2).getLastCellNum()-1, sheet.getRow(2).getLastCellNum()-1));
        //стиль итогов
        sheet.getRow(3).createCell(sheet.getRow(2).getLastCellNum()-2).setCellStyle(chHeaderStyle);
        sheet.getRow(3).createCell(sheet.getRow(2).getLastCellNum()-1).setCellStyle(chHeaderStyle);
        
        Font headerFont = wb.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 10);
        
        CellStyle headerDay = wb.createCellStyle();
        headerDay.setFont(headerFont);
        headerDay.setAlignment(HorizontalAlignment.CENTER);
        headerDay.setVerticalAlignment(VerticalAlignment.CENTER);
        headerDay.setBorderTop(BorderStyle.MEDIUM);
        headerDay.setBorderBottom(BorderStyle.MEDIUM);
        headerDay.setBorderLeft(BorderStyle.MEDIUM);
        headerDay.setBorderRight(BorderStyle.MEDIUM);
        
        //шапка дат стр 3
        for(int i = selected_fields.size() + 1; i <= sheet.getRow(2).getLastCellNum() - 3; i++){
            HSSFCell dayCell = sheet.getRow(3).createCell(i);
            int perComp =  (int) Math.floor((i - selected_fields.size() - 1)/ 4);
            switch (i- selected_fields.size() - 1 - perComp * 4){
                case 3: dayCell.setCellValue("Вх/Вых"); break;
                case 2: dayCell.setCellValue("Всего"); break;
                case 1: dayCell.setCellValue("Выход"); break;
                case 0: dayCell.setCellValue("Вход"); break;
            }
            dayCell.setCellStyle(headerDay);
        }
        
        //table        
        CellStyle chTable = wb.createCellStyle();
        chTable.setAlignment(HorizontalAlignment.LEFT);
        chTable.setVerticalAlignment(VerticalAlignment.CENTER);
        chTable.setBorderTop(BorderStyle.THIN);
        chTable.setBorderBottom(BorderStyle.THIN);
        chTable.setBorderLeft(BorderStyle.THIN);
        chTable.setBorderRight(BorderStyle.THIN);
        
        CellStyle stBorderL = wb.createCellStyle();
        stBorderL.setBorderLeft(BorderStyle.THIN);
        
        CellStyle stBorderLB = wb.createCellStyle();
        stBorderLB.setBorderLeft(BorderStyle.THIN);
        stBorderLB.setBorderBottom(BorderStyle.THIN);
        
        CellStyle stBorderB = wb.createCellStyle();
        stBorderB.setBorderBottom(BorderStyle.THIN);
        
        CellStyle stDayTot = wb.createCellStyle();
        stDayTot.setFont(headerFont);
        stDayTot.setAlignment(HorizontalAlignment.CENTER);
        stDayTot.setVerticalAlignment(VerticalAlignment.CENTER);
        
        for(int r = 5; r <= sheet.getLastRowNum(); r+=2){
            HSSFRow row0 = sheet.getRow(r-1); //ночь
            HSSFRow row1 = sheet.getRow(r); //день
            
            //стиль + обединение ночи и дня до дат
            for(int col = 0; col < selected_fields.size(); col++){
                sheet.addMergedRegion(new CellRangeAddress(r-1, r, col, col));
                row0.getCell(col).setCellStyle(chTable);
                row1.getCell(col).setCellStyle(chTable);
            }
            row0.getCell(selected_fields.size()).setCellStyle(chTable);
            row1.getCell(selected_fields.size()).setCellStyle(chTable);
            
            //Время
            for(int dCol = selected_fields.size() + 1; dCol <= sheet.getRow(2).getLastCellNum() - 1; dCol++){
                int perComp =  (int) Math.floor((dCol - selected_fields.size() - 1)/ 4);
                switch (dCol- selected_fields.size() - 1 - perComp * 4){
                    case 3:
                        row1.getCell(dCol).setCellStyle(stBorderB);
                        String val3 = row1.getCell(dCol).getStringCellValue();
                        row0.createCell(dCol).setCellValue(val3);
                        row0.getCell(dCol).setCellStyle(stDayTot);
                        sheet.addMergedRegion(new CellRangeAddress(r-1, r, dCol, dCol));
                        break;
                    case 2:
                        row1.getCell(dCol).setCellStyle(stBorderB);
                        String val2 = row1.getCell(dCol).getStringCellValue();
                        row0.createCell(dCol).setCellValue(val2);
                        row0.getCell(dCol).setCellStyle(stDayTot);
                        sheet.addMergedRegion(new CellRangeAddress(r-1, r, dCol, dCol));
                        break;
                    case 1: 
                        row1.getCell(dCol).setCellStyle(stBorderB);
                        break;
                    case 0: 
                        row0.getCell(dCol).setCellStyle(stBorderL);
                        row1.getCell(dCol).setCellStyle(stBorderLB);
                        break;
                }
            }
            
            //Итог: Время      
            int lastCol = row1.getLastCellNum();
            row1.getCell(lastCol-2).setCellStyle(headerDay);
            String tot = row1.getCell(lastCol-2).getStringCellValue();
            row0.createCell(lastCol-2).setCellValue(tot);
            row0.getCell(lastCol-2).setCellStyle(headerDay);
            sheet.addMergedRegion(new CellRangeAddress(r-1, r, lastCol-2, lastCol-2));
            //Итог: Вх/Вых
            row1.getCell(lastCol-1).setCellStyle(headerDay);
            tot = row1.getCell(lastCol-1).getStringCellValue();
            row0.createCell(lastCol-1).setCellValue(tot);
            row0.getCell(lastCol-1).setCellStyle(headerDay);
            sheet.addMergedRegion(new CellRangeAddress(r-1, r, lastCol-1, lastCol-1));
        }
    }
}
