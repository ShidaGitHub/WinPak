package ru.it_mm.winpak.views.report2;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import ru.it_mm.winpak.utils.entity.CardHolder;

public class Report02Data implements Serializable{
    private static final long serialVersionUID = 83557264L;
    private CardHolder cardHolder;
    private String param3; //cardNumber
    private Date inTime;
    
    public Report02Data(){}

    public CardHolder getCardHolder() {
        return cardHolder;
    }
    public void setCardHolder(CardHolder cardHolder) {
        this.cardHolder = cardHolder;
    }

    public String getParam3() {
        return param3;
    }
    public void setParam3(String param3) {
        this.param3 = param3;
    }

    public Date getInTime() {
        return inTime;
    }
    public void setInTime(Date inTime) {
        this.inTime = inTime;
    }
    
    public boolean rendered(Report02.FIELDS column){
        if(column == Report02.FIELDS.DATE_IN)
            return !inTime.equals(new Date(0));        
        return true;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += Objects.hash(cardHolder, param3, inTime);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Report02Data)) {
            return false;
        }
        Report02Data other = (Report02Data) object;
        return this.param3.equals(other.param3) &&
                this.cardHolder.equals(other.cardHolder) &&
                this.inTime.equals(other.inTime);
    }

    @Override
    public String toString() {
        return getClass().getCanonicalName() + " [ cardNumber=" + param3 + "   " + " cardHolder=" + cardHolder + " ]";
    }
    

    
}
