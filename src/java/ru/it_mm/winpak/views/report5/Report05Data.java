package ru.it_mm.winpak.views.report5;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import ru.it_mm.winpak.utils.entity.CardHolder;

public class Report05Data implements Serializable{
    public enum DATA_INFO {DAY, NIGHT}
    private static final long serialVersionUID = 83557264L;
    private CardHolder cardHolder;
    private String cardNumber;
    private DATA_INFO dataInfo;
    private final HashMap<Date, Date> inTime;
    private final HashMap<Date, Date> outTime;
    private final HashMap<Date, Date> totalTime;    
    private final HashMap<Date, String> countEvent;
    
    public Report05Data(){
        inTime = new HashMap<>();
        outTime = new  HashMap<>();
        totalTime = new  HashMap<>();
        countEvent = new  HashMap<>();
    }

    public CardHolder getCardHolder() {
        return cardHolder;
    }
    public void setCardHolder(CardHolder cardHolder) {
        this.cardHolder = cardHolder;
    }
    
    public Date getInTimeOnDate(Date date){
        return inTime.get(date);
    }
    public void setInTimeOnDate(Date date, Date inTime){
        this.inTime.put(date, inTime);
    }
    
    public Date getOutTimeOnDate(Date date){
        return outTime.get(date);
    }
    public void setOutTimeOnDate(Date date, Date outTime){
        this.outTime.put(date, outTime);
    }

    public Date getTotalTimeOnDate(Date date){
        return totalTime.get(date);
    }
    public void setTotalTimeOnDate(Date date, Date totalTime){
        this.totalTime.put(date, totalTime);
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
    
    public DATA_INFO getDataInfo() {
        return dataInfo;
    }
    public void setDataInfo(DATA_INFO dataInfo) {
        this.dataInfo = dataInfo;
    }
    public String getDataInfoStr() {
        switch (dataInfo) {
            case DAY:  return "День";
            case NIGHT:  return "Ночь";
            default: return "";
        }
    }
    
    public String getReportValue(String column){
        if (column.equals("cardNumber"))
            return cardNumber;
        
        try {
            java.lang.reflect.Field fieldCalss = cardHolder.getClass().getDeclaredField(column);
            fieldCalss.setAccessible(true);
            return String.valueOf(fieldCalss.get(cardHolder));
        } catch (SecurityException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException ex) {
            return "";
        }
    }
        
    public String getReportValueOnDate(Date period, int colIndex){
        int perComp =  (int) Math.floor(colIndex / 4);
        
        Date in = getInTimeOnDate(period);
        Date out = getOutTimeOnDate(period);
        Date tot = getTotalTimeOnDate(period);

        //countEvent
        if(colIndex - perComp * 4 == 3)
            return getCountEventOnDate(period);
        
        //total
        if(colIndex - perComp * 4 == 2){
            if(dataInfo == DATA_INFO.NIGHT || tot == null  || tot.equals(new Date(0))) return "";
            
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            return sdf.format(tot);
        }
        //out time
        if(colIndex - perComp * 4 == 1){
            if(out == null || out.equals(new Date(0))) return "";
            
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            return sdf.format(out);
        }
        //in time
        if(colIndex - perComp * 4 == 0){
            if(in == null || in.equals(new Date(0))) return "";
            
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            return sdf.format(in);
        }
        return "";
    }
        
    public String getCellStyle(int colIndex){
        int perComp =  (int) Math.floor(colIndex / 4);
        return "border-width: 1px 1px " +
                String.valueOf((dataInfo == DATA_INFO.DAY? 2 : 1)) + "px " +
                String.valueOf((colIndex - perComp * 4 == 0? 2 : 1)) + "px; " + 
                (colIndex - perComp * 4 > 1 ? "font-weight: bold;" : "");
    }
    
    public String getTotalTime(){
        if (dataInfo == DATA_INFO.NIGHT) return "";
        
        LocalDateTime ldt0 = LocalDateTime.MIN;
        Date date0 = new Date(0);
        for (Date period : totalTime.keySet()){
            if (totalTime.get(period).equals(date0))
                continue;
            
            LocalDateTime ldt = LocalDateTime.ofInstant(totalTime.get(period).toInstant(), ZoneId.systemDefault());
            ldt0 = ldt0.plusHours(ldt.getHour()).plusMinutes(ldt.getMinute()).plusSeconds(ldt.getSecond());
        }
        long hrs = TimeUnit.DAYS.toHours(ldt0.getDayOfYear()) - 24 + ldt0.getHour();
        long min = ldt0.getMinute();
        return String.format("%02d:%02d", hrs, min);
    }
    
    public String getTotalInOut(){
        if (dataInfo == DATA_INFO.NIGHT) return "";
        
        int inC = 0;
        int outC = 0;
        for (Date period : countEvent.keySet()){
            if(countEvent.get(period) == null || countEvent.get(period).isEmpty()) continue;
            
            String[] cm = countEvent.get(period).split("/");
            try{
                inC += Integer.valueOf(cm[0].trim());
                outC += Integer.valueOf(cm[1].trim());
            }catch(NumberFormatException ex){}
        }
        
        return String.format("%3d/%3d", inC, outC);
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += Objects.hash(cardHolder, inTime, outTime, countEvent);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Report05Data)) {
            return false;
        }
        Report05Data other = (Report05Data) object;
        return this.cardHolder.equals(other.cardHolder) &&
               this.inTime.equals(other.inTime) &&
               this.outTime.equals(other.outTime);
    }

    @Override
    public String toString() {
        return getClass().getCanonicalName() + "[ cardHolder=" + cardHolder + "   " + " inTime=" + inTime + "  OutTime=" + outTime + "]";
    }
    

    
}
