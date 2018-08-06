package ru.it_mm.winpak.views.report7;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
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
public class Report07  extends Report {
    private static final long serialVersionUID = 71727374L;
    public enum FIELDS {
        //Порядок ВАЖЕН!!! В таком поядке поля будут отражаться в p:selectManyCheckbox layout="grid"
        //В p:dataTable порядок полей отсортируем по sortOrder
        LAST_NAME("Фамилия", "lastName", "cardHolder", 150, 1),            NOTE12("Семейное положение", "note12", "cardHolder", 200, 15),
        FIRST_NAME("Имя", "firstName", "cardHolder", 150, 2),              NOTE13("Образование", "note13", "cardHolder", 150, 16),
        NOTE1("Отчество", "note1", "cardHolder", 150, 3),                  NOTE14("Серия номер паспорта", "note14", "cardHolder", 100, 17),
        CARD_NUMBER("Номер карты", "param3", "history", 100, 4),           NOTE15("Кем выдан", "note15", "cardHolder", 250, 18),
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
    
    private List<SelectItem> fields;
    private List<FIELDS> selected_fields;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    private List<Report07Data> report07Data;
    
    public Report07(){}
    
    @PostConstruct
    @Override
    public void init() {
        setDate1(atStartOfDay(new Date()));
        setDate2(Utils.atEndOfDay(new Date()));
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
        selected_fields.add(FIELDS.CARD_NUMBER);
        selected_fields.add(FIELDS.NOTE2);
        selected_fields.add(FIELDS.NOTE3);
        selected_fields.add(FIELDS.NOTE4);
        selected_fields.add(FIELDS.NOTE5);
        selected_fields.add(FIELDS.NOTE6);
        selected_fields.add(FIELDS.NOTE7);
        
        report07Data = new ArrayList<>();
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

    public List<Report07Data> getReport07Data() {
        return report07Data;
    }
    public void setReport07Data(List<Report07Data> report07Data) {
        this.report07Data = report07Data;
    }
    
    @Override
    public String getReportFileName(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy");
        return "Laters " + sdf.format(getDate1()) + "_" + sdf.format(getDate2());
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
        while (atEndOfDay(startRage).before(endRage)) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(startRage);
            cal.add(Calendar.DATE, 1);
            startRage = cal.getTime();
            dateRange.add(startRage);
        }
        return dateRange;
    }
    
    @Override
    public String showReport(){
        String retVal = "/views/report07/report07?faces-redirect=true";
        
        report07Data.clear();
        
        if (selected_fields.isEmpty()) selected_fields.add(FIELDS.LAST_NAME);
        selected_fields.sort((FIELDS o1, FIELDS o2) -> {
            return o2.sortOrder > o1.sortOrder ? -1 : (o2.sortOrder < o1.sortOrder) ? 1 : 0;
        });
        
        StringBuilder sbF = new StringBuilder("");
        selected_fields.forEach((field) -> {
            sbF.append(field.getTableName()).append(".").append(field.getProperty()).append(", ");
        });
        sbF.replace(sbF.length()-2, sbF.length()-1, "");        
        
        //1. Выберем опоздавших
        String qlLaters = "SELECT MIN(history.genTime), (FUNC('DAY', history.genTime) + FUNC('MONTH', history.genTime)) dmGT, cardHolder.recordID, MIN(hWIndependentDevices.name), " + sbF.toString() + " FROM History history "
                        + "LEFT JOIN history.cardHolder cardHolder "
                        + "LEFT JOIN history.hWIndependentDevices hWIndependentDevices "
                        + "WHERE history.deleted != 1 AND cardHolder.deleted != 1 AND "
                            + "history.genTime BETWEEN :date1 AND :date2 AND "
                            + "CAST(history.genTime AS TIME) > CAST(:period1 AS TIME) AND "
                            + "history.param1 = 118 AND hWIndependentDevices.name LIKE :devNameIn "
                            + (lastName != null && lastName.trim().length() > 0 ? "AND cardHolder.lastName LIKE :lastName " : "")
                            + (firstName != null && firstName.trim().length() > 0 ? "AND cardHolder.firstName LIKE :firstName " : "")
                            + (note1 != null && note1.trim().length() > 0 ? "AND cardHolder.note1 LIKE :note1 " : "")
                            + (note5 != null && note5.trim().length() > 0 ? "AND cardHolder.note5 LIKE :note5 " : "")
                            + (cardNumber != null && cardNumber.trim().length() > 0 ? "AND history.param3 LIKE :cardNumber " : "")
                        + "GROUP BY dmGT, cardHolder.recordID, " + sbF.toString() + " "
                        + "ORDER BY cardHolder.recordID";
        
        Query queryLate = entityManager.createQuery(qlLaters);
        queryLate.setParameter("date1", atStartOfDay(minDate(getDate1(), getDate2())));
        queryLate.setParameter("date2", Utils.atEndOfDay(maxDate(getDate1(), getDate2())));
        queryLate.setParameter("period1", minDate(period1, period2));
        queryLate.setParameter("devNameIn", "%" + IN_STR + "%");
        if (lastName != null && lastName.trim().length() > 0)
            queryLate.setParameter("lastName", "%" + lastName.trim() + "%");
        if (firstName != null && firstName.trim().length() > 0)
            queryLate.setParameter("firstName", "%" + firstName.trim() + "%");
        if (note1 != null && note1.trim().length() > 0)
            queryLate.setParameter("note1", "%" + note1.trim() + "%");
        if (note5 != null && note5.trim().length() > 0)
            queryLate.setParameter("note5", "%" + note5.trim() + "%");
        if (cardNumber != null && cardNumber.trim().length() > 0)
            queryLate.setParameter("cardNumber", "%" + cardNumber.trim() + "%");
        
        List<Object[]> historyLaters = queryLate.getResultList();
        
        //2. Выберем ушедщих раньше
        String qlAbs = "SELECT MAX(history.genTime), (FUNC('DAY', history.genTime) + FUNC('MONTH', history.genTime)) dmGT, cardHolder.recordID, MIN(hWIndependentDevices.name), " + sbF.toString() + " FROM History history "
                        + "LEFT JOIN history.cardHolder cardHolder "
                        + "LEFT JOIN history.hWIndependentDevices hWIndependentDevices "
                        + "WHERE history.deleted != 1 AND cardHolder.deleted != 1 AND "
                            + "history.genTime BETWEEN :date1 AND :date2 AND "
                            + "CAST(history.genTime AS TIME) < CAST(:period2 AS TIME) AND CAST(history.genTime AS TIME) > CAST(:period1 AS TIME) AND "
                            + "history.param1 = 118 AND hWIndependentDevices.name LIKE :devNameOUT "
                            + (lastName != null && lastName.trim().length() > 0 ? "AND cardHolder.lastName LIKE :lastName " : "")
                            + (firstName != null && firstName.trim().length() > 0 ? "AND cardHolder.firstName LIKE :firstName " : "")
                            + (note1 != null && note1.trim().length() > 0 ? "AND cardHolder.note1 LIKE :note1 " : "")
                            + (note5 != null && note5.trim().length() > 0 ? "AND cardHolder.note5 LIKE :note5 " : "")
                            + (cardNumber != null && cardNumber.trim().length() > 0 ? "AND history.param3 LIKE :cardNumber " : "")
                        + "GROUP BY dmGT, cardHolder.recordID, " + sbF.toString() + " "
                        + "ORDER BY cardHolder.recordID";
        
        Query queryAbs = entityManager.createQuery(qlAbs);
        queryAbs.setParameter("date1", atStartOfDay(minDate(getDate1(), getDate2())));
        queryAbs.setParameter("date2", Utils.atEndOfDay(maxDate(getDate1(), getDate2())));
        queryAbs.setParameter("period1", minDate(period1, period2));
        queryAbs.setParameter("period2", maxDate(period1, period2));
        queryAbs.setParameter("devNameOUT", "%" + OUT_STR + "%");
        if (lastName != null && lastName.trim().length() > 0)
            queryAbs.setParameter("lastName", "%" + lastName.trim() + "%");
        if (firstName != null && firstName.trim().length() > 0)
            queryAbs.setParameter("firstName", "%" + firstName.trim() + "%");
        if (note1 != null && note1.trim().length() > 0)
            queryAbs.setParameter("note1", "%" + note1.trim() + "%");
        if (note5 != null && note5.trim().length() > 0)
            queryAbs.setParameter("note5", "%" + note5.trim() + "%");
        if (cardNumber != null && cardNumber.trim().length() > 0)
            queryAbs.setParameter("cardNumber", "%" + cardNumber.trim() + "%");
        
        List<Object[]> historyAbs = queryAbs.getResultList();
        
        historyLaters.addAll(historyAbs);
        historyLaters.sort((o1, o2) -> {
            if(((Date)o1[0]).before((Date)o2[0])) return -1;
            if(((Date)o1[0]).after((Date)o2[0])) return 1;
            return 0;
        });
        
        List<FIELDS> chSF = selected_fields.stream().
            filter((field) -> field.getTableName().contains("cardHolder")).collect(Collectors.toList());
        
        int[] chIds = historyLaters.stream().mapToInt(value -> (int) value[2]).distinct().toArray(); // уникальные cardHolder из history
        for(int recordID : chIds){
            List<Object[]> filtHist = historyLaters.stream().
                    filter(obj -> (int)obj[2] == recordID).collect(Collectors.toList());
            
            filtHist.stream().map(obj -> (int)obj[1]).distinct().forEach(dayMonth -> { //Перебор по дням - строки отчета
                Report07Data r07d = new Report07Data();                
                //CardHolder
                CardHolder cardHolder = new CardHolder((Integer) filtHist.get(0)[2]);
                chSF.stream().forEachOrdered((field) -> {
                    try {
                        java.lang.reflect.Field fieldCalss = cardHolder.getClass().getDeclaredField(field.getProperty());
                        fieldCalss.setAccessible(true);
                        fieldCalss.set(cardHolder, filtHist.get(0)[selected_fields.indexOf(field) + 4]);
                    } catch (SecurityException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException ex) {}
                });
                r07d.setCardHolder(cardHolder);
                if (selected_fields.contains(FIELDS.CARD_NUMBER))
                    r07d.setParam3(String.valueOf(filtHist.get(0)[selected_fields.indexOf(FIELDS.CARD_NUMBER) + 4]).trim());
                
                Optional<Date> dateLate = filtHist.stream().
                        filter(obj -> (int)obj[1] == dayMonth && String.valueOf(obj[3]).toUpperCase().contains(IN_STR)).map(obj -> (Date)obj[0]).
                        min((o1, o2) -> {
                            if (((Date)o1).before((Date)o2)) return -1;
                            if (((Date)o1).after((Date)o2)) return 1;
                            return 0;
                        });
                if(dateLate.isPresent())
                    r07d.setTimeIn(dateLate.get());
                
                Optional<Date> dateAbs = filtHist.stream().
                        filter(obj -> (int)obj[1] == dayMonth && String.valueOf(obj[3]).toUpperCase().contains(OUT_STR)).map(obj -> (Date)obj[0]).
                        max((o1, o2) -> {
                            if (((Date)o1).before((Date)o2)) return -1;
                            if (((Date)o1).after((Date)o2)) return 1;
                            return 0;
                        });
                if(dateAbs.isPresent())
                    r07d.setTimeOut(dateAbs.get());
                
                report07Data.add(r07d);
            });
        }
        return retVal;
    }
}
