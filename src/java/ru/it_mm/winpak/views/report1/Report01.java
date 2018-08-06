package ru.it_mm.winpak.views.report1;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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
import org.primefaces.event.SelectEvent;
import ru.it_mm.winpak.utils.Report;
import ru.it_mm.winpak.utils.Utils;
import static ru.it_mm.winpak.utils.Utils.atEndOfDay;
import static ru.it_mm.winpak.utils.Utils.atStartOfDay;
import static ru.it_mm.winpak.utils.Utils.maxDate;
import static ru.it_mm.winpak.utils.Utils.minDate;
import ru.it_mm.winpak.utils.entity.CardHolder;

@Named
@SessionScoped
public class Report01 extends Report{
    private static final long serialVersionUID = 11121314L;    
    public enum FIELDS {
        //Порядок ВАЖЕН!!! В таком поядке поля будут отражаться в p:selectManyCheckbox layout="grid"
        //В p:dataTable порядок полей отсортируем по sortOrder
        
        LAST_NAME("Фамилия", "lastName", "cardHolder", 150, 1),             NOTE10("Место прописки", "note10", "cardHolder", 250, 15),
        FIRST_NAME("Имя", "firstName", "cardHolder", 150, 2),               NOTE11("Место проживания", "note11", "cardHolder", 250, 16),
        NOTE1("Отчество", "note1", "cardHolder", 150, 3),                   NOTE12("Семейное положение", "note12", "cardHolder", 200, 17),           
        CARD_NUMBER("Номер карты", "param3", "history", 100, 4),            NOTE13("Образование", "note13", "cardHolder", 150, 18),            
        DATE_IN("Время входа", "inTime", "history", 120, 5),                NOTE14("Серия номер паспорта", "note14", "cardHolder", 100, 19),
        DATE_OUT("Время выхода", "outTime", "history", 120, 6),             NOTE15("Кем выдан", "note15", "cardHolder", 250, 20),
        NOTE2("Табельный номер", "note2", "cardHolder", 100, 7),            NOTE16("Дата выдачи", "note16", "cardHolder", 100, 21),
        NOTE3("Цех", "note3", "cardHolder", 100, 8),                        NOTE17("Дата приема на работу", "note17", "cardHolder", 100, 22),
        NOTE4("Служба", "note4", "cardHolder", 200, 9),                     NOTE18("Характер работы", "note18", "cardHolder", 250, 23),
        NOTE5("Цех(полн. наименован.)", "note5", "cardHolder", 200, 10),    NOTE19("Номер приказа об увольнении", "note19", "cardHolder", 100, 24),
        NOTE6("Участок", "note6", "cardHolder", 200, 11),                   NOTE20("Дата приказа об увольнении", "note20", "cardHolder", 100, 25),
        NOTE7("Должность", "note7", "cardHolder", 250, 12),                 NOTE21("Дата увольнения", "note21", "cardHolder", 100, 26),
        NOTE8("Дата рождения", "note8", "cardHolder", 100, 13),             NOTE22("Должность сокр", "note22", "cardHolder", 150, 27),
        NOTE9("Место рождения", "note9", "cardHolder", 250, 14),            NOTE23("Режим работы", "note23", "cardHolder", 150, 28),
                                                                            NOTE24("Заказчик", "note24", "cardHolder", 150, 29);
                
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
    
    private Date period;
    private List<SelectItem> cardtypes;
    private Short[] selected_cardtypes;
    
    private List<SelectItem> fields;
    private List<FIELDS> selected_fields;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    private List<Report01Data> report01Data;
    
    public Report01(){}
    
    @PostConstruct
    @Override
    public void init() {
        setDate1(Utils.atStartOfDay(new Date()));
        setDate2(Utils.atEndOfDay(new Date()));
        period = new Date();
        
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
        selected_fields.add(FIELDS.DATE_OUT);
        selected_fields.add(FIELDS.NOTE2);
        selected_fields.add(FIELDS.NOTE3);
        selected_fields.add(FIELDS.NOTE4);
        selected_fields.add(FIELDS.NOTE5);
        selected_fields.add(FIELDS.NOTE6);
        selected_fields.add(FIELDS.NOTE7);
        
        report01Data = new ArrayList<>();
    }

    public Date getPeriod() {
        return period;
    }
    public void setPeriod(Date period) {
        this.period = period;
    }

    public void onPeriodSelect(SelectEvent event) {
        setDate1(atStartOfDay(period));
        setDate2(atEndOfDay(period));
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

    public List<Report01Data> getReport01Data() {
        return report01Data;
    }
    public void setReport01Data(List<Report01Data> report01Data) {
        this.report01Data = report01Data;
    }
    
    @Override
    public String getReportFileName(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy");
        return "Employees at the factory " + sdf.format(getDate1()) + "_" + sdf.format(getDate2());
    }
    
    @Override
    public String showReport(){
        String retVal = "/views/report01/report01?faces-redirect=true";
        
        if (getDate1() == null) setDate1(atStartOfDay(new Date()));
        if (getDate1() == null) setDate2(atEndOfDay(new Date()));
        if (selected_fields.isEmpty()) selected_fields.add(FIELDS.LAST_NAME);
        
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
                            + "history.param1 = :param1 ";
        if (selected_cardtypes.length > 0)
            qlString += "AND card.cardStatus IN :selected_cardtypes ";
        qlString += "ORDER BY cardHolder.lastName, cardHolder.firstName, cardHolder.note1, history.genTime";
        
        Query query = entityManager.createQuery(qlString);
        query.setParameter("date1", minDate(getDate1(), getDate2()));
        query.setParameter("date2", maxDate(getDate1(), getDate2()));
        query.setParameter("devNameIn", "%вход%");
        query.setParameter("devNameOUT", "%выход%");
        query.setParameter("param1", 118);        
        if (selected_cardtypes.length > 0)
            query.setParameter("selected_cardtypes", Arrays.asList(selected_cardtypes));
        
        report01Data.clear();
        List<Object[]> history = query.getResultList();
        
        Comparator<Object[]> comprDate = (o1, o2) -> {
            Date date1c = (Date) o1[0];
            Date date2c = (Date) o2[0];
            int i = 0;
            if(date1c.before(getDate2()))
                i = -1;
            if(date1c.after(getDate2()))
                i = 1;
            return i;
        };        
        Date sed = new Date(0);
        
        int[] chIds = history.stream().mapToInt(value -> (int) value[1]).distinct().toArray(); // уникальные cardHolder из history
        for(int recordID : chIds){
            List<Object[]> filtHist = history.stream().filter(obj -> (int)obj[1] == recordID).
                    collect(Collectors.toList());
            
            //Даты
            Optional<Object[]> optObjIn = filtHist.stream().
                    filter(obj -> ((Date)obj[0]).before(period) && ((String)obj[2]).toUpperCase().contains(IN_STR)).min(comprDate);
            if(!optObjIn.isPresent()) continue; // еще не пришел
            
            Date timeIn = (Date)optObjIn.get()[0];
            
            Optional<Object[]> optObjOut = filtHist.stream().
                    filter(obj -> ((String)obj[2]).toUpperCase().contains(OUT_STR)).max(comprDate);            
           
            Date timeOut = optObjOut.isPresent() ? (Date)optObjOut.get()[0] : sed;
            timeOut = timeOut.after(period) || timeOut.before(timeIn) ? sed : timeOut;
            
            //CardHolder
            CardHolder cardHolder = new CardHolder(recordID);
            chSelFields.stream().forEachOrdered((field) -> {
                try {
                    java.lang.reflect.Field fieldCalss = cardHolder.getClass().getDeclaredField(field.getProperty());
                    fieldCalss.setAccessible(true);
                    fieldCalss.set(cardHolder, filtHist.get(0)[chSelFields.indexOf(field) + 5]);
                } catch (SecurityException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException ex) {}
            });
            
            Report01Data rDt = new Report01Data();
            rDt.setCardHolder(cardHolder);
            rDt.setParam3((String)filtHist.get(0)[4]);
            rDt.setInTime(timeIn);
             rDt.setOutTime(timeOut);
            report01Data.add(rDt);
        }
        
        selected_fields.sort((FIELDS o1, FIELDS o2) -> {
            return o2.sortOrder > o1.sortOrder ? -1 : (o2.sortOrder < o1.sortOrder) ? 1 : 0;
        });
        return retVal;
    }
}
