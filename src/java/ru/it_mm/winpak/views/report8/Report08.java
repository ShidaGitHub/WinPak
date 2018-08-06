package ru.it_mm.winpak.views.report8;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import ru.it_mm.winpak.utils.entity.Card;
import javax.persistence.Query;
import ru.it_mm.winpak.utils.Report;
import static ru.it_mm.winpak.utils.Utils.*;

@Named
@SessionScoped
public class Report08 extends Report{
    private static final long serialVersionUID = 81828384L;
    public enum FIELDS {
        //Порядок ВАЖЕН!!! В таком поядке поля будут отражаться в p:selectManyCheckbox layout="grid"
        //В p:dataTable порядок полей отсортируем по sortOrder        
        LAST_NAME("Фамилия", "lastName", true, 150, 1),                      NOTE12("Семейное положение", "note12", true, 150, 16),
        FIRST_NAME("Имя", "firstName", true, 150, 2),                         NOTE13("Образование", "note13", true, 150, 17),
        NOTE1("Отчество", "note1", true, 150, 3),                            NOTE14("Серия номер паспорта", "note14", true, 100, 18),
        TIME_STAMP("Время модификации", "timeStamp", false, 100, 4),         NOTE15("Кем выдан", "note15", true, 250, 19),
        CARD_NUMBER("Номер карты", "cardNumber", false, 100, 5),             NOTE16("Дата выдачи", "note16", true, 100, 20),
        NOTE2("Табельный номер", "note2", true, 100, 6),                     NOTE17("Дата приема на работу", "note17", true, 100, 21),
        NOTE3("Цех", "note3", true, 100, 7),                                 NOTE18("Характер работы", "note18", true, 250, 22),
        NOTE4("Служба", "note4", true, 200, 8),                              NOTE19("Номер приказа об увольнении", "note19", true, 100, 23),
        NOTE5("Цех(полн. наименован.)", "note5", true, 200, 9),              NOTE20("Дата приказа об увольнении", "note20", true, 100, 24),
        NOTE6("Участок", "note6", true, 200, 10),                            NOTE21("Дата увольнения", "note21", true, 100, 25),
        NOTE7("Должность", "note7", true, 150, 11),                          NOTE22("Должность сокр", "note22", true, 150, 26),
        NOTE8("Дата рождения", "note8", true, 100, 12),                      NOTE23("Режим работы", "note23", true, 150, 27),
        NOTE9("Место рождения", "note9", true, 250, 13),                     NOTE24("Заказчик", "note24", true, 150, 28),
        NOTE10("Место прописки", "note10", true, 250, 14),                   ACTIVATION_DATE("Дата активации", "activationDate", false, 100, 29),
        NOTE11("Место проживания", "note11", true, 250, 15),                 EXPIRATION_DATE("Дата истечения", "expirationDate", false, 100, 30),
        LAST_READER_DATE("Проходы", "lastReaderDate", false, 100, 31);
                
        private final String header;
        private final String property;        
        private final boolean propIsObj;
        private final int width;
        private final int sortOrder;
        
        FIELDS(String header, String property, boolean propIsObj,int width, int sortOrder){
            this.header = header;
            this.property = property;
            this.propIsObj = propIsObj;
            this.width = width;
            this.sortOrder = sortOrder;
        }
        
        public String getHeader() { return header; }
        public String getProperty() { return property; }
        public boolean isPropIsObj() {return propIsObj;}
        public int getWidth() { return width; }
        public int getSortOrder() { return sortOrder; }
    }
    
    private List<SelectItem> cardtypes;
    private Short[] selected_cardtypes;
    
    private List<SelectItem> fields;
    private List<FIELDS> selected_fields;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    private List<Card> report08Data;
    
    public Report08(){}
    
    @PostConstruct
    @Override
    public void init() {
        setDate1(new Date());
        setDate2(new Date());
        
        cardtypes = new ArrayList<>();
        cardtypes.add(new SelectItem(1, "Постоянные"));
        cardtypes.add(new SelectItem(2, "Временные"));
        cardtypes.add(new SelectItem(3, "Разовые"));
        
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
        selected_fields.add(FIELDS.NOTE2);
        selected_fields.add(FIELDS.NOTE3);
        selected_fields.add(FIELDS.NOTE4);
        selected_fields.add(FIELDS.NOTE5);
        selected_fields.add(FIELDS.NOTE6);
        selected_fields.add(FIELDS.NOTE7);
    }
    
    public List<SelectItem> getFields() {
        return fields;
    }
    public void setFields(List<SelectItem> fields) {
        this.fields = fields;
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

    public List<FIELDS> getSelected_fields() {
        return selected_fields;
    }
    public void setSelected_fields(List<FIELDS> selected_fields) {
        this.selected_fields = selected_fields;
    }

    public List<Card> getReport08Data() {
        return report08Data;
    }
    public void setReport08Data(List<Card> report08Data) {
        this.report08Data = report08Data;
    }

    @Override
    public String getReportFileName(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy");
        return "Cards_" + sdf.format(getDate1()) + "_" + sdf.format(getDate2());
    }
    
    @Override
    public String showReport() {        
        String retVal = "/views/report08/report08?faces-redirect=true";
        if (getDate1() == null) setDate1(new Date(0L));
        if (getDate2() == null) setDate2(atEndOfDay(new Date()));
        
        String qlString = "SELECT card FROM Card card "
                    + "LEFT JOIN FETCH card.cardHolder "
                    + "WHERE card.deleted != 1 "
                    + "AND card.timeStamp BETWEEN :date1 AND :date2 ";
        if (selected_cardtypes.length > 0)
            qlString += "AND card.cardStatus IN :selected_cardtypes ";
        qlString += "ORDER BY card.timeStamp, card.cardNumber";
        
        Query query = entityManager.createQuery(qlString, Card.class);
        query.setParameter("date1", atStartOfDay(minDate(getDate1(), getDate2())));
        query.setParameter("date2", atEndOfDay(maxDate(getDate1(), getDate2())));
        if (selected_cardtypes.length > 0)
            query.setParameter("selected_cardtypes", Arrays.asList(selected_cardtypes));
        
        report08Data = query.getResultList();
        
        if(selected_fields.isEmpty())
            selected_fields.addAll(Arrays.asList(FIELDS.values()));
        selected_fields.sort((FIELDS o1, FIELDS o2) -> {
            return o2.sortOrder > o1.sortOrder ? -1 : (o2.sortOrder < o1.sortOrder) ? 1 : 0;
        });
        
        return retVal;
    }
    
    
}
