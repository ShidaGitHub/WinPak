package ru.it_mm.winpak.views.report1;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import ru.it_mm.winpak.utils.entity.CardHolder;

public class Report01Data implements Serializable{
    private static final long serialVersionUID = 83557264L;
    private CardHolder cardHolder;
    private String param3; //cardNumber
    private Date inTime;
    private Date outTime;
    
    public Report01Data(){}

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

    public Date getOutTime() {
        return outTime;
    }
    public void setOutTime(Date outTime) {
        this.outTime = outTime;
    }
    
    public boolean rendered(Report01.FIELDS column){
        if(column == Report01.FIELDS.DATE_IN)
            return !inTime.equals(new Date(0));
        if(column == Report01.FIELDS.DATE_OUT)
            return !outTime.equals(new Date(0));
        return true;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += Objects.hash(cardHolder, param3, inTime, outTime);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Report01Data)) {
            return false;
        }
        Report01Data other = (Report01Data) object;
        return this.param3.equals(other.param3) &&
                this.cardHolder.equals(other.cardHolder) &&
                this.inTime.equals(other.inTime) && 
                this.outTime.equals(other.outTime);
    }

    @Override
    public String toString() {
        return getClass().getCanonicalName() + " [ cardNumber=" + param3 + "   " + " cardHolder=" + cardHolder + " ]";
    }
    

    
}
