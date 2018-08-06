package ru.it_mm.winpak.views.report3;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import ru.it_mm.winpak.utils.Report;
import static ru.it_mm.winpak.utils.Utils.atStartOfDay;
import static ru.it_mm.winpak.utils.Utils.maxDate;
import static ru.it_mm.winpak.utils.Utils.minDate;
import ru.it_mm.winpak.utils.entity.Card;
import ru.it_mm.winpak.utils.entity.CardHolder;
import ru.it_mm.winpak.utils.entity.HWIndependentDevices;

@Named
@SessionScoped
public class Report03  extends Report {
    private static final long serialVersionUID = 31323334L;
    public enum FIELDS {
        //Порядок ВАЖЕН!!! В таком поядке поля будут отражаться в p:selectManyCheckbox layout="grid"
        //В p:dataTable порядок полей отсортируем по sortOrder
        
        LAST_NAME("Фамилия", "lastName", "cardHolder", 150, 1),             NOTE10("Место прописки", "note10", "cardHolder", 250, 16),
        FIRST_NAME("Имя", "firstName", "cardHolder", 150, 2),               NOTE11("Место проживания", "note11", "cardHolder", 250, 17),
        NOTE1("Отчество", "note1", "cardHolder", 150, 3),                   NOTE12("Семейное положение", "note12", "cardHolder", 200, 18),
        CARD_NUMBER("Номер карты", "cardNumber", "card", 100, 4),           NOTE13("Образование", "note13", "cardHolder", 150, 19),
        GEN_TIME("Время события", "genTime", "history", 120, 5),            NOTE14("Серия номер паспорта", "note14", "cardHolder", 100, 20),
        DEV_NAME("Событие", "name", "hWIndependentDevices", 200, 6),        NOTE15("Кем выдан", "note15", "cardHolder", 250, 21),
        CARD_STATUS("Состояние", "cardStatus", "card", 150, 6),             NOTE16("Дата выдачи", "note16", "cardHolder", 100, 22),
        NOTE2("Табельный номер", "note2", "cardHolder", 100, 7),            NOTE17("Дата приема на работу", "note17", "cardHolder", 100, 23),
        NOTE3("Цех", "note3", "cardHolder", 100, 9),                        NOTE18("Характер работы", "note18", "cardHolder", 250, 24),
        NOTE4("Служба", "note4", "cardHolder", 200, 10),                    NOTE19("Номер приказа об увольнении", "note19", "cardHolder", 100, 25), 
        NOTE5("Цех(полн. наименован.)", "note5", "cardHolder", 200, 11),    NOTE20("Дата приказа об увольнении", "note20", "cardHolder", 100, 26),
        NOTE6("Участок", "note6", "cardHolder", 200, 12),                   NOTE21("Дата увольнения", "note21", "cardHolder", 100, 27),
        NOTE7("Должность", "note7", "cardHolder", 250, 13),                 NOTE22("Должность сокр", "note22", "cardHolder", 150, 28),
        NOTE8("Дата рождения", "note8", "cardHolder", 100, 14),             NOTE23("Режим работы", "note23", "cardHolder", 150, 29),
        NOTE9("Место рождения", "note9", "cardHolder", 250, 15),            NOTE24("Заказчик", "note24", "cardHolder", 150, 30);
        
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
    private boolean inPeriod;
    
    private String lastName;
    private String firstName;
    private String note1;
    private String cardNumber;
    private String note5;
    
    private boolean inEvent;
    private boolean outEvent;
    
    private List<SelectItem> fields;
    private List<FIELDS> selected_fields;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    private List<Report03Data> report03Data;
    
    public Report03(){}
    
    @PostConstruct
    @Override
    public void init() {
        setDate1(atStartOfDay(new Date()));
        setDate2(new Date());
        period1 = atStartOfDay(new Date());
        period2 = new Date();
        
        inEvent = true;
        outEvent = true;
        
        fields = new ArrayList<>();
        for(FIELDS f : FIELDS.values()){
            fields.add(new SelectItem(f, f.getHeader()));
        }
        
        report03Data = new ArrayList<>();
        
        selected_fields = new ArrayList<>();
        selected_fields.add(FIELDS.LAST_NAME);
        selected_fields.add(FIELDS.FIRST_NAME);
        selected_fields.add(FIELDS.NOTE1);
        selected_fields.add(FIELDS.CARD_NUMBER);
        selected_fields.add(FIELDS.GEN_TIME);
        selected_fields.add(FIELDS.DEV_NAME);
        selected_fields.add(FIELDS.CARD_STATUS);
        selected_fields.add(FIELDS.NOTE2);
        selected_fields.add(FIELDS.NOTE3);
        selected_fields.add(FIELDS.NOTE4);
        selected_fields.add(FIELDS.NOTE5);
        selected_fields.add(FIELDS.NOTE6);
        selected_fields.add(FIELDS.NOTE7);
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

    public boolean isInPeriod() {
        return inPeriod;
    }
    public void setInPeriod(boolean inPeriod) {
        this.inPeriod = inPeriod;
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

    public boolean isInEvent() {
        return inEvent;
    }
    public void setInEvent(boolean inEvent) {
        this.inEvent = inEvent;
    }

    public boolean isOutEvent() {
        return outEvent;
    }
    public void setOutEvent(boolean outEvent) {
        this.outEvent = outEvent;
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
    
    public List<Report03Data> getReport03Data() {
        return report03Data;
    }
    public void setReport03Data(List<Report03Data> report03Data) {
        this.report03Data = report03Data;
    }
    
    @Override
    public String getReportFileName(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy");
        return "Events " + sdf.format(getDate1()) + "_" + sdf.format(getDate2());
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
        
    @Override
    public String showReport(){
        String retVal = "/views/report03/report03?faces-redirect=true";
        report03Data.clear();
        
        if (selected_fields.isEmpty()) selected_fields.add(FIELDS.LAST_NAME);
        selected_fields.sort((FIELDS o1, FIELDS o2) -> {
            return o2.sortOrder > o1.sortOrder ? -1 : (o2.sortOrder < o1.sortOrder) ? 1 : 0;
        });
        
        StringBuilder sbF = new StringBuilder("");
        selected_fields.forEach((field) -> {
            sbF.append(field.getTableName()).append(".").append(field.getProperty()).append(", ");
        });
        sbF.replace(sbF.length()-2, sbF.length()-1, "");        
        
        String qlString = "SELECT history.genTime gt, cardHolder.recordID, hWIndependentDevices.deviceID, card.recordID, " + sbF.toString() + " FROM History history "
                        + "INNER JOIN history.cardHolder cardHolder "
                        + "INNER JOIN history.hWIndependentDevices hWIndependentDevices "                        
                        + "INNER JOIN history.card card "
                        + "WHERE history.deleted != 1 AND cardHolder.deleted != 1 AND "
                            + "history.genTime BETWEEN :date1 AND :date2 "
                            + (lastName != null && lastName.trim().length() > 0 ? "AND cardHolder.lastName LIKE :lastName " : "")
                            + (firstName != null && firstName.trim().length() > 0 ? "AND cardHolder.firstName LIKE :firstName " : "")
                            + (note1 != null && note1.trim().length() > 0 ? "AND cardHolder.note1 LIKE :note1 " : "")
                            + (note5 != null && note5.trim().length() > 0 ? "AND cardHolder.note5 LIKE :note5 " : "")
                            + (cardNumber != null && cardNumber.trim().length() > 0 ? "AND card.cardNumber LIKE :cardNumber " : "")
                            + (inEvent && !outEvent ? "AND hWIndependentDevices.name LIKE :devNameIn " : "")
                            + (!inEvent && outEvent ? "AND hWIndependentDevices.name LIKE :devNameOut " : "")
                            + (inEvent && outEvent ? "AND (hWIndependentDevices.name LIKE :devNameIn OR hWIndependentDevices.name LIKE :devNameOut) " : "")
                            + (inEvent || outEvent ? "AND history.param1 = 118 " : "")
                        + "ORDER BY cardHolder.lastName, cardHolder.firstName, cardHolder.note1, history.genTime";
        
        Query query = entityManager.createQuery(qlString);
        query.setParameter("date1", minDate(getDate1(), getDate2()));
        query.setParameter("date2", maxDate(getDate1(), getDate2()));
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
        if (inEvent)
            query.setParameter("devNameIn", "%" + IN_STR + "%");
        if (outEvent)
            query.setParameter("devNameOut", "%" + OUT_STR + "%");
        
        List<Object[]> history = query.getResultList();
        
        List<FIELDS> chSF = selected_fields.stream().
            filter((field) -> !(!field.getTableName().contains("cardHolder"))).collect(Collectors.toList());
        List<FIELDS> cSF = selected_fields.stream().
            filter((field) -> !(!field.getTableName().contains("card"))).collect(Collectors.toList());
        List<FIELDS> hwSF = selected_fields.stream().
            filter((field) -> !(!field.getTableName().contains("hWIndependentDevices"))).collect(Collectors.toList());
        
        for(Object[] obj : history){
            if(inPeriod){
                LocalTime ltGenTime = LocalDateTime.ofInstant(((Date)obj[0]).toInstant(), ZoneId.systemDefault()).toLocalTime();
                LocalTime ltPeriod1 = LocalDateTime.ofInstant(minDate(period1, period2).toInstant(), ZoneId.systemDefault()).toLocalTime();
                LocalTime ltPeriod2 = LocalDateTime.ofInstant(maxDate(period1, period2).toInstant(), ZoneId.systemDefault()).toLocalTime();
                
                if (!(ltGenTime.isAfter(ltPeriod1) && ltGenTime.isBefore(ltPeriod2)))
                    continue;
            }
            
            //CardHolder
            CardHolder cardHolder = new CardHolder((Integer) obj[1]);
            chSF.stream().forEachOrdered((field) -> {
                try {
                    java.lang.reflect.Field fieldCalss = cardHolder.getClass().getDeclaredField(field.getProperty());
                    fieldCalss.setAccessible(true);
                    fieldCalss.set(cardHolder, obj[selected_fields.indexOf(field) + 4]);
                } catch (SecurityException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException ex) {}
            });
            
            //Card
            Card card = new Card((Integer) obj[3]);
            cSF.stream().forEachOrdered((field) -> {
                try {
                    java.lang.reflect.Field fieldCalss = card.getClass().getDeclaredField(field.getProperty());
                    fieldCalss.setAccessible(true);
                    fieldCalss.set(card, obj[selected_fields.indexOf(field) + 4]);
                } catch (SecurityException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException ex) {}
            });
            
            //hWIndependentDevices
            HWIndependentDevices dev = new HWIndependentDevices((Integer) obj[2]);
            hwSF.stream().forEachOrdered((field) -> {
                try {
                    java.lang.reflect.Field fieldCalss = dev.getClass().getDeclaredField(field.getProperty());
                    fieldCalss.setAccessible(true);
                    fieldCalss.set(dev, obj[selected_fields.indexOf(field) + 4]);
                } catch (SecurityException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException ex) {}
            });
            
            Report03Data r3d = new Report03Data();
            r3d.setCardHolder(cardHolder);
            r3d.setCard(card);
            r3d.setDevName(dev.getName());
            r3d.setGenTime(((Date)obj[0]));
            
            report03Data.add(r3d);
        }
        return retVal;
    }
}
