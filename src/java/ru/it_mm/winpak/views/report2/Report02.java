package ru.it_mm.winpak.views.report2;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
import static ru.it_mm.winpak.utils.Utils.atEndOfDay;
import static ru.it_mm.winpak.utils.Utils.atStartOfDay;
import static ru.it_mm.winpak.utils.Utils.maxDate;
import static ru.it_mm.winpak.utils.Utils.minDate;
import ru.it_mm.winpak.utils.entity.CardHolder;

@Named
@SessionScoped
public class Report02 extends Report{
    private static final long serialVersionUID = 21222324L;
    public enum FIELDS {
        //Порядок ВАЖЕН!!! В таком поядке поля будут отражаться в p:selectManyCheckbox layout="grid"
        //В p:dataTable порядок полей отсортируем по sortOrder
        LAST_NAME("Фамилия", "lastName", "cardHolder", 150, 1),             NOTE11("Место проживания", "note11", "cardHolder", 250, 16),
        FIRST_NAME("Имя", "firstName", "cardHolder", 150, 2),               NOTE12("Семейное положение", "note12", "cardHolder", 200, 17),
        NOTE1("Отчество", "note1", "cardHolder", 150, 3),                   NOTE13("Образование", "note13", "cardHolder", 150, 18),
        CARD_NUMBER("Номер карты", "param3", "history", 100, 4),            NOTE14("Серия номер паспорта", "note14", "cardHolder", 100, 19),
        DATE_IN("Время входа", "inTime", "history", 120, 5),                NOTE15("Кем выдан", "note15", "cardHolder", 250, 20),
        NOTE2("Табельный номер", "note2", "cardHolder", 100, 7),            NOTE16("Дата выдачи", "note16", "cardHolder", 100, 21),
        NOTE3("Цех", "note3", "cardHolder", 100, 8),                        NOTE17("Дата приема на работу", "note17", "cardHolder", 100, 22),
        NOTE4("Служба", "note4", "cardHolder", 200, 9),                     NOTE18("Характер работы", "note18", "cardHolder", 250, 23),
        NOTE5("Цех(полн. наименован.)", "note5", "cardHolder", 200, 10),    NOTE19("Номер приказа об увольнении", "note19", "cardHolder", 100, 24),
        NOTE6("Участок", "note6", "cardHolder", 200, 11),                   NOTE20("Дата приказа об увольнении", "note20", "cardHolder", 100, 25),
        NOTE7("Должность", "note7", "cardHolder", 250, 12),                 NOTE21("Дата увольнения", "note21", "cardHolder", 100, 26),
        NOTE8("Дата рождения", "note8", "cardHolder", 100, 13),             NOTE22("Должность сокр", "note22", "cardHolder", 150, 27),
        NOTE9("Место рождения", "note9", "cardHolder", 250, 14),            NOTE23("Режим работы", "note23", "cardHolder", 150, 28),
        NOTE10("Место прописки", "note10", "cardHolder", 250, 15),          NOTE24("Заказчик", "note24", "cardHolder", 150, 29);                                                                            
                
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
    
    private List<SelectItem> cardtypes;
    private Short[] selected_cardtypes;
    
    private List<SelectItem> fields;
    private List<FIELDS> selected_fields;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    private List<Report02Data> report02Data;
    
    public Report02(){}
    
    @PostConstruct
    @Override
    public void init() {
        setDate1(atStartOfDay(new Date()));
        setDate2(new Date());
        
        cardtypes = new ArrayList<>();
        cardtypes.add(new SelectItem(1, "Постоянные"));
        cardtypes.add(new SelectItem(2, "Временные"));
        cardtypes.add(new SelectItem(3, "Разовые"));
        
        selected_cardtypes = new Short[]{1, 2, 3};
        
        fields = new ArrayList<>();
        for(FIELDS f : FIELDS.values()){
            fields.add(new SelectItem(f, f.getHeader()));
        }
        
        selected_fields = new ArrayList<>();
        selected_fields.add(FIELDS.LAST_NAME);
        selected_fields.add(FIELDS.FIRST_NAME);
        selected_fields.add(FIELDS.NOTE1);
        selected_fields.add(FIELDS.DATE_IN);
        selected_fields.add(FIELDS.NOTE2);
        selected_fields.add(FIELDS.NOTE3);
        selected_fields.add(FIELDS.NOTE4);
        selected_fields.add(FIELDS.NOTE5);
        selected_fields.add(FIELDS.NOTE6);
        selected_fields.add(FIELDS.NOTE7);
        
        report02Data = new ArrayList<>();
    }
       
    public List<SelectItem> getCardtypes() {
        return cardtypes;
    }
    public void setCardtypes(List<SelectItem> cardtypes) {
        this.cardtypes = cardtypes;
    }

    public Short[] getSelected_cardtypes() {
        return selected_cardtypes;
    }
    public void setSelected_cardtypes(Short[] selected_cardtypes) {
        this.selected_cardtypes = selected_cardtypes;
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

    public List<Report02Data> getReport02Data() {
        return report02Data;
    }
    public void setReport02Data(List<Report02Data> report02Data) {
        this.report02Data = report02Data;
    }
    
    @Override
    public String getReportFileName() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy");
        return "Present workers " + sdf.format(getDate1()) + "_" + sdf.format(getDate2());
    }
    
    //slower
    @Deprecated
    public List<Object[]> getHistoryOnJPQL(){
        if (getDate1() == null) setDate1(atStartOfDay(new Date()));
        if (getDate2() == null) setDate2(atEndOfDay(new Date()));
        
        List<FIELDS> chSelFields = selected_fields.stream().
            filter((field) -> !(!field.getTableName().contains("cardHolder"))).collect(Collectors.toList());
        chSelFields.sort((FIELDS o1, FIELDS o2) -> {
            return o2.sortOrder > o1.sortOrder ? -1 : (o2.sortOrder < o1.sortOrder) ? 1 : 0;
        });
        
        StringBuilder sbF = new StringBuilder("");
        if(chSelFields.size() > 0){
            chSelFields.forEach((field) -> {
                sbF.append(field.getTableName()).append(".").append(field.getProperty()).append(", ");
            });
            sbF.replace(sbF.length()-2, sbF.length()-1, "");
        }
        
        String qlString = "SELECT history.genTime, cardHolder.recordID, dev.name, card.cardStatus, history.param3, " + sbF.toString() + " FROM History history "
                        + "LEFT JOIN history.cardHolder cardHolder "
                        + "LEFT JOIN history.hWIndependentDevices dev "                        
                        + "LEFT JOIN history.card card "
                        + "WHERE history.deleted != 1 AND cardHolder.deleted != 1 AND "
                            + "history.genTime BETWEEN :date1 AND :date2 AND "
                            + "(dev.name LIKE :devNameIn OR dev.name LIKE :devNameOUT) AND "
                            + "history.param1 = :param1 AND "
                            + "history.genTime = ("
                                + "SELECT MAX(h.genTime) FROM History h "
                                + "LEFT JOIN h.hWIndependentDevices d "
                                    + "WHERE h.cardHolder = history.cardHolder AND "
                                    + "h.genTime BETWEEN :date1 AND :date2 AND "
                                    + "h.param1 = :param1 AND "
                                    + "(d.name LIKE :devNameIn OR dev.name LIKE :devNameOUT)"
                                + ") ";
        if (selected_cardtypes.length > 0)
            qlString += " AND card.cardStatus IN :selected_cardtypes ";
        qlString += " ORDER BY cardHolder.lastName, cardHolder.firstName, cardHolder.note1, history.genTime";
        
        Query query = entityManager.createQuery(qlString);
        query.setParameter("date1", minDate(getDate1(), getDate2()));
        query.setParameter("date2", maxDate(getDate1(), getDate2()));
        query.setParameter("devNameIn", "%вход%");
        query.setParameter("devNameOUT", "%выход%");
        query.setParameter("param1", 118);        
        if (selected_cardtypes.length > 0)
            query.setParameter("selected_cardtypes", Arrays.asList(selected_cardtypes));
        
        List<Object[]> history = query.getResultList();
        return history;
    }
    
    public List<Object[]> getHistoryOnSQL(){
        if (getDate1() == null) setDate1(atStartOfDay(new Date()));
        if (getDate2() == null) setDate2(atEndOfDay(new Date()));
        
        List<FIELDS> chSelFields = selected_fields.stream().
                filter((field) -> !(!field.getTableName().contains("cardHolder"))).collect(Collectors.toList());
        chSelFields.sort((FIELDS o1, FIELDS o2) -> {
            return o2.sortOrder > o1.sortOrder ? -1 : (o2.sortOrder < o1.sortOrder) ? 1 : 0;
        });
        
        StringBuilder sbF = new StringBuilder("");
        if(chSelFields.size() > 0){
            chSelFields.forEach((field) -> {
                sbF.append(field.getTableName()).append(".").append(field.getProperty()).append(", ");
            });
            sbF.replace(sbF.length()-2, sbF.length()-1, "");
        }
        
        StringBuilder sCT = new StringBuilder("( ");
        for(Short ct : selected_cardtypes)
            sCT.append(ct).append(", ");
        sCT.replace(sCT.length()-2, sCT.length()-1, ")");
        
        String qlString = "SELECT history.genTime, cardHolder.recordID, dev.name, card.cardStatus, history.param3, " + sbF.toString() + " FROM History history "
                        + "LEFT OUTER JOIN CardHolder cardHolder ON (cardHolder.RecordID = history.Link3) "
                        + "LEFT OUTER JOIN HWIndependentDevices dev ON (dev.DeviceID = history.Link1) "
                        + "LEFT OUTER JOIN Card card ON (card.RecordID = history.Link2) "
                        + "INNER JOIN ("
                            + "SELECT MAX(te0.GenTime) AS maxGenTime, te1.RecordID FROM History te0 "
                            + "LEFT OUTER JOIN CardHolder te1 ON (te1.RecordID = te0.Link3) "
                            + "LEFT OUTER JOIN HWIndependentDevices te2 ON (te2.DeviceID = te0.Link1) "
                            + "WHERE (te0.GenTime BETWEEN ? AND ?) AND "
                                + "(te2.name LIKE ? OR te2.name LIKE ?) AND "
                                + "te0.param1 = ? "
                            + "GROUP BY te1.RecordID"
                            + ") AS CHMaxDate ON history.genTime = CHMaxDate.maxGenTime AND cardHolder.RecordID = CHMaxDate.RecordID "
                        + "WHERE history.deleted != 1 AND cardHolder.deleted != 1 AND "
                            + "history.genTime BETWEEN ? AND ? AND "
                            + "(dev.name LIKE ? OR dev.name LIKE ?) AND history.param1 = ?";
        if (selected_cardtypes.length > 0)
            qlString += " AND card.cardStatus IN " + sCT;
        qlString += " ORDER BY cardHolder.lastName, cardHolder.firstName, cardHolder.note1, history.genTime";
        
        Query query = entityManager.createNativeQuery(qlString);
        query.setParameter(1, minDate(getDate1(), getDate2()));
        query.setParameter(2, maxDate(getDate1(), getDate2()));
        query.setParameter(3, "%вход%");
        query.setParameter(4, "%выход%");
        query.setParameter(5, 118);
        
        query.setParameter(6, minDate(getDate1(), getDate2()));
        query.setParameter(7, maxDate(getDate1(), getDate2()));
        query.setParameter(8, "%вход%");
        query.setParameter(9, "%выход%");
        query.setParameter(10, 118);
        
        List<Object[]> history = query.getResultList();
        return history;
    }
    
    @Override
    public String showReport(){
        String retVal = "/views/report02/report02?faces-redirect=true";
        
        report02Data.clear();
        List<Object[]> history = getHistoryOnSQL();
        
        if (selected_fields.isEmpty()) selected_fields.add(FIELDS.LAST_NAME);
        List<FIELDS> chSelFields = selected_fields.stream().
                filter((field) -> !(!field.getTableName().contains("cardHolder"))).collect(Collectors.toList());
        chSelFields.sort((FIELDS o1, FIELDS o2) -> {
            return o2.sortOrder > o1.sortOrder ? -1 : (o2.sortOrder < o1.sortOrder) ? 1 : 0;
        });
        
        history.stream().filter((obj) -> !(((String)obj[2]).toUpperCase().contains(OUT_STR))).map((obj) -> {
            //CardHolder
            CardHolder cardHolder = new CardHolder((Integer) obj[1]);
            chSelFields.stream().forEachOrdered((field) -> {
                try {
                    java.lang.reflect.Field fieldCalss = cardHolder.getClass().getDeclaredField(field.getProperty());
                    fieldCalss.setAccessible(true);
                    fieldCalss.set(cardHolder, obj[chSelFields.indexOf(field) + 5]);
                } catch (SecurityException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException ex) {}
            });
            Report02Data rDt = new Report02Data();
            rDt.setCardHolder(cardHolder);
            rDt.setParam3((String)obj[4]);
            rDt.setInTime((Date) obj[0]);
            return rDt;            
        }).forEachOrdered((rDt) -> {
            report02Data.add(rDt);
        });
        
        selected_fields.sort((FIELDS o1, FIELDS o2) -> {
            return o2.sortOrder > o1.sortOrder ? -1 : (o2.sortOrder < o1.sortOrder) ? 1 : 0;
        });
        
        return retVal;
    }
}
