package ru.it_mm.winpak.views.report4;

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
public class Report04  extends Report {
    private static final long serialVersionUID = 41424344L;
    public enum FIELDS {
        //Порядок ВАЖЕН!!! В таком поядке поля будут отражаться в p:selectManyCheckbox layout="grid"
        //В p:dataTable порядок полей отсортируем по sortOrder
        LAST_NAME("Фамилия", "lastName", "cardHolder", 150, 1),            NOTE10("Место прописки", "note10", "cardHolder", 250, 15),
        FIRST_NAME("Имя", "firstName", "cardHolder", 150, 2),              NOTE11("Место проживания", "note11", "cardHolder", 250, 16),
        NOTE1("Отчество", "note1", "cardHolder", 150, 3),                  NOTE12("Семейное положение", "note12", "cardHolder", 200, 15),
        CARD_NUMBER("Номер карты", "param3", "history", 100, 4),           NOTE13("Образование", "note13", "cardHolder", 150, 16),
        NOTE2("Табельный номер", "note2", "cardHolder", 100, 5),           NOTE14("Серия номер паспорта", "note14", "cardHolder", 100, 17),
        GEN_TIME("Время", "genTime", "history", 100, 6),                   NOTE15("Кем выдан", "note15", "cardHolder", 250, 18), 
        EVENT("Событие", "name", "dev", 100, 7),                           NOTE16("Дата выдачи", "note16", "cardHolder", 100, 19),
        NOTE3("Цех", "note3", "cardHolder", 200, 8),                       NOTE17("Дата приема на работу", "note17", "cardHolder", 100, 20),                                            
        NOTE4("Служба", "note4", "cardHolder", 200, 9),                    NOTE18("Характер работы", "note18", "cardHolder", 150, 21),
        NOTE5("Цех(полн. наименован.)", "note5", "cardHolder", 200, 10),   NOTE19("Номер приказа об увольнении", "note19", "cardHolder", 100, 22),                     
        NOTE6("Участок", "note6", "cardHolder", 200, 11),                  NOTE20("Дата приказа об увольнении", "note20", "cardHolder", 100, 23),  
        NOTE7("Должность", "note7", "cardHolder", 250, 12),                NOTE21("Дата увольнения", "note21", "cardHolder", 100, 24),
        NOTE8("Дата рождения", "note8", "cardHolder", 100, 13),            NOTE22("Должность сокр", "note22", "cardHolder", 150, 25),     
        NOTE9("Место рождения", "note9", "cardHolder", 250, 14),           NOTE23("Режим работы", "note23", "cardHolder", 200, 26),     
                                                                           NOTE24("Заказчик", "note24", "cardHolder", 150, 27);
        
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
    
    private List<SelectItem> fields;
    private List<FIELDS> selected_fields;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    private List<Report04Data> report04Data;
    
    public Report04(){}
    
    @PostConstruct
    @Override
    public void init() {
        setDate1(atStartOfDay(new Date()));
        setDate2(Utils.atEndOfDay(new Date()));
        period = getDate1();
        
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
        selected_fields.add(FIELDS.GEN_TIME);
        selected_fields.add(FIELDS.EVENT);
        
        report04Data = new ArrayList<>();
    }

    public Date getPeriod() {
        return period;
    }
    public void setPeriod(Date period) {
        this.period = period;
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

    public List<Report04Data> getReport04Data() {
        return report04Data;
    }
    public void setReport04Data(List<Report04Data> report04Data) {
        this.report04Data = report04Data;
    }
    
    @Override
    public String getReportFileName(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy");
        return "Repeat events " + sdf.format(getDate1()) + "_" + sdf.format(getDate2());
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
        String retVal = "/views/report04/report04?faces-redirect=true";
        
        report04Data.clear();
        
        if (selected_fields.isEmpty()) selected_fields.add(FIELDS.LAST_NAME);
        selected_fields.sort((FIELDS o1, FIELDS o2) -> {
            return o2.sortOrder > o1.sortOrder ? -1 : (o2.sortOrder < o1.sortOrder) ? 1 : 0;
        });
        
        StringBuilder sbF = new StringBuilder("");
        selected_fields.forEach((field) -> {
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
                        + "ORDER BY cardHolder.lastName, cardHolder.firstName, cardHolder.note1, history.genTime";
        
        Query query = entityManager.createQuery(qlString);
        query.setParameter("date1", minDate(getDate1(), getDate2()));
        query.setParameter("date2", maxDate(getDate1(), getDate2()));
        query.setParameter("devNameIn", "%" + IN_STR + "%");
        query.setParameter("devNameOut", "%" + OUT_STR + "%");
        query.setParameter("param1", 118);
        
        List<Object[]> history = query.getResultList();
        
        List<FIELDS> chSF = selected_fields.stream().
            filter((field) -> field.getTableName().contains("cardHolder")).collect(Collectors.toList());
        int[] chIds = history.stream().mapToInt(value -> (int) value[1]).distinct().toArray(); // уникальные cardHolder из history
        LocalTime perLT = LocalDateTime.ofInstant(period.toInstant(), ZoneId.systemDefault()).toLocalTime();
        
        for(int chId : chIds){
            //фильтруем историю по cardHolder
            List<Object[]> filtHist = history.stream().
                filter(obj -> (int)obj[1] == chId).collect(Collectors.toList());
            
            if (filtHist.size() < 2) continue;
            
            for(int i = 1; i< filtHist.size(); i++){
                Duration dfr = Duration.between(((Date)filtHist.get(i-1)[0]).toInstant(), ((Date)filtHist.get(i)[0]).toInstant());
                if (dfr.getSeconds() < perLT.toSecondOfDay()){
                    Report04Data r04d0 = new Report04Data();
                    //CardHolder
                    CardHolder cardHolder0 = new CardHolder((Integer) filtHist.get(i-1)[1]);
                    for(FIELDS field : chSF){
                        try {
                            java.lang.reflect.Field fieldCalss = cardHolder0.getClass().getDeclaredField(field.getProperty());
                            fieldCalss.setAccessible(true);
                            fieldCalss.set(cardHolder0, filtHist.get(i-1)[selected_fields.indexOf(field) + 3]);
                        } catch (SecurityException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException ex) {}
                    };
                    r04d0.setCardHolder(cardHolder0);
                    r04d0.setGenTime((Date) filtHist.get(i-1)[0]);
                    r04d0.setName(selected_fields.contains(FIELDS.EVENT)? String.valueOf(filtHist.get(i-1)[selected_fields.indexOf(FIELDS.EVENT) + 3]) : "");
                    r04d0.setParam3(selected_fields.contains(FIELDS.CARD_NUMBER)? String.valueOf(filtHist.get(i-1)[selected_fields.indexOf(FIELDS.CARD_NUMBER) + 3]) : "");

                    if (!report04Data.contains(r04d0))
                        report04Data.add(r04d0);
                    
                    Report04Data r04d = new Report04Data();
                    //CardHolder
                    CardHolder cardHolder = new CardHolder((Integer) filtHist.get(i)[1]);
                    chSF.stream().forEachOrdered((field) -> {
                        try {
                            java.lang.reflect.Field fieldCalss = cardHolder.getClass().getDeclaredField(field.getProperty());
                            fieldCalss.setAccessible(true);
                            fieldCalss.set(cardHolder, filtHist.get(0)[selected_fields.indexOf(field) + 3]);
                        } catch (SecurityException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException ex) {}
                    });
                    r04d.setCardHolder(cardHolder);
                    r04d.setGenTime((Date) filtHist.get(i)[0]);
                    r04d.setName(selected_fields.contains(FIELDS.EVENT)? String.valueOf(filtHist.get(i)[selected_fields.indexOf(FIELDS.EVENT) + 3]) : "");
                    r04d.setParam3(selected_fields.contains(FIELDS.CARD_NUMBER)? String.valueOf(filtHist.get(i)[selected_fields.indexOf(FIELDS.CARD_NUMBER) + 3]) : "");
                    
                    report04Data.add(r04d);
                }
            }
        }
        return retVal;
    }
}
