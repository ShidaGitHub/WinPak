package ru.it_mm.winpak.views.report7;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import ru.it_mm.winpak.utils.Utils;
import ru.it_mm.winpak.utils.entity.CardHolder;

public class Report07Data implements Serializable{
    private static final long serialVersionUID = 73777264L;
    private CardHolder cardHolder;
    private String param3; //cardNumber
    private Date timeIn;
    private Date timeOut;
    
    public Report07Data(){}

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

    public Date getTimeIn() {
        return timeIn;
    }
    public void setTimeIn(Date timeIn) {
        this.timeIn = timeIn;
    }

    public Date getTimeOut() {
        return timeOut;
    }
    public void setTimeOut(Date timeOut) {
        this.timeOut = timeOut;
    }
    
    public String getTimeDiff(Date peridod, Date date){
        if (peridod == null || date == null) return "";
        
        LocalTime ltP = LocalDateTime.ofInstant(peridod.toInstant(), ZoneId.systemDefault()).toLocalTime();
        LocalTime ltD = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).toLocalTime();
        
        Duration diff = Duration.between(ltD, ltP).abs();
        LocalDateTime ldt0 = LocalDateTime.MIN;
        ldt0 = ldt0.plusSeconds(diff.getSeconds());
        long hrs = TimeUnit.DAYS.toHours(ldt0.getDayOfYear()) - 24 + ldt0.getHour();
        long min = ldt0.getMinute();
        return String.format("%02d:%02d", hrs, min);
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += Objects.hash(cardHolder, param3, timeIn, timeOut);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Report07Data)) {
            return false;
        }
        Report07Data other = (Report07Data) object;
        return this.cardHolder.equals(other.cardHolder) &&
                this.param3.equals(other.param3) &&
                this.timeIn.equals(other.timeIn) &&
                this.timeOut.equals(other.timeOut);
    }

    @Override
    public String toString() {
        return getClass().getCanonicalName() + "[ cardHolder=" + cardHolder + "   " + " param1=" + param3 + " ]";
    }
}
