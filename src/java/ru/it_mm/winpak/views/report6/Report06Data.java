package ru.it_mm.winpak.views.report6;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import ru.it_mm.winpak.utils.entity.CardHolder;

public class Report06Data implements Serializable{
    private static final long serialVersionUID = 66566264L;
    private CardHolder cardHolder;
    private String cardNumber;
    private final HashMap<Date, String> countEvent;
    private int totalMissDay;
    
    
    public Report06Data(){
        countEvent = new  HashMap<>();
    }

    public CardHolder getCardHolder() {
        return cardHolder;
    }
    public void setCardHolder(CardHolder cardHolder) {
        this.cardHolder = cardHolder;
    }
    
    public String getCountEventOnDate(Date date){
        return countEvent.get(date);
    }
    public void setCountEventOnDate(Date date, String countEvent){
        this.countEvent.put(date, countEvent);
    }

    public String getCardNumber() {
        return cardNumber;
    }
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public int getTotalMissDay() {
        return totalMissDay;
    }
    public void setTotalMissDay(int totalMissDay) {
        this.totalMissDay = totalMissDay;
    }
        
    public String getReportValueOnDate(Date period){
        return getCountEventOnDate(period);
    }
    
    public String getCellStyle(Date period){
        return getCountEventOnDate(period).isEmpty()? 
                "background: #ffdddd;" : "";
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += Objects.hash(cardHolder, countEvent, cardNumber, totalMissDay);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Report06Data)) {
            return false;
        }
        Report06Data other = (Report06Data) object;
        return this.cardHolder.equals(other.cardHolder) &&
                cardNumber.equals(other.cardNumber) &&
                totalMissDay == other.totalMissDay;
    }

    @Override
    public String toString() {
        return getClass().getCanonicalName() + "[ cardHolder=" + cardHolder + "]";
    }
}
