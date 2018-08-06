package ru.it_mm.winpak.views.report4;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import ru.it_mm.winpak.utils.entity.CardHolder;

public class Report04Data implements Serializable{
    private static final long serialVersionUID = 43744244L;
    private CardHolder cardHolder;
    private Date genTime;
    private String name; //устройство
    private String param3;
    
    public Report04Data(){
        cardHolder = new CardHolder();
        genTime = new Date(0);
        name = "";
        param3 = "";
    }

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

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }    

    public String getParam3() {
        return param3;
    }
    public void setParam3(String param3) {
        this.param3 = param3;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += Objects.hash(cardHolder, genTime, name, param3);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Report04Data)) {
            return false;
        }
        Report04Data other = (Report04Data) object;
        return this.cardHolder.equals(other.cardHolder) &&
                this.genTime.equals(other.genTime) &&
                this.name.equals(other.name) && 
                this.param3.equals(other.param3);
    }

    @Override
    public String toString() {
        return getClass().getCanonicalName() + "[ cardHolder=" + cardHolder + "   " + " genTime=" + genTime + " eventName=" + name + " ]";
    }
}
