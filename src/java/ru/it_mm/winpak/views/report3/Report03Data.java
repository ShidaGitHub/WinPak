package ru.it_mm.winpak.views.report3;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import ru.it_mm.winpak.utils.entity.Card;
import ru.it_mm.winpak.utils.entity.CardHolder;
import ru.it_mm.winpak.views.report3.Report03.FIELDS;

public class Report03Data implements Serializable{
    private static final long serialVersionUID = 83557264L;
    private Date genTime;
    private CardHolder cardHolder;
    private Card card;
    private String devName;
    
    public Report03Data(){}

    public CardHolder getCardHolder() {
        return cardHolder;
    }
    public void setCardHolder(CardHolder cardHolder) {
        this.cardHolder = cardHolder;
    }

    public Date getGenTime() {
        return genTime;
    }
    public void setGenTime(Date genTime) {
        this.genTime = genTime;
    }

    public Card getCard() {
        return card;
    }
    public void setCard(Card card) {
        this.card = card;
    }

    public String getDevName() {
        return devName;
    }
    public void setDevName(String devName) {
        this.devName = devName;
    }
    
    public String getReportValue(FIELDS column){
        if (column == FIELDS.LAST_NAME)
            return cardHolder.getLastName();
        
        if (column == FIELDS.FIRST_NAME)
            return cardHolder.getFirstName();
        
        if (column == FIELDS.NOTE1)
            return cardHolder.getNote1();
        
        if (column == FIELDS.CARD_NUMBER)
            return card.getCardNumber().trim();
        
        if (column == FIELDS.GEN_TIME){
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy HH:mm:ss");
            return sdf.format(genTime);
        }
        
        if (column == FIELDS.DEV_NAME)
            return devName;
        
        if (column == FIELDS.CARD_STATUS){
            switch(card.getCardStatus()){
                case 1: return "Постоянная карта";
                case 2: return "Временная карта";
                case 3: return "Разовая карта";
                default: return "";
            }
        }
        
        if (column.getTableName().contains("cardHolder") && column.getProperty().contains("note")){
            try {
                java.lang.reflect.Field fieldCalss = cardHolder.getClass().getDeclaredField(column.getProperty());
                fieldCalss.setAccessible(true);
                return String.valueOf(fieldCalss.get(cardHolder));
            } catch (SecurityException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException ex) {
                return "";
            }
        }
        return "";
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += Objects.hash(genTime, cardHolder, card, devName);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Report03Data)) {
            return false;
        }
        Report03Data other = (Report03Data) object;
        return this.genTime.equals(other.genTime) &&
                this.cardHolder.equals(other.cardHolder) &&
                this.card.equals(other.card) &&
                this.devName.equals(other.devName);
    }

    @Override
    public String toString() {
        return "ru.it_mm.winpak.views.report1.Report03Data[ genTime=" + genTime + "   " + " devName=" + devName + " ]";
    }
}
