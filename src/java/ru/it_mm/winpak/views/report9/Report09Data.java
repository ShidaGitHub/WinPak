package ru.it_mm.winpak.views.report9;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Report09Data implements Serializable{
    public enum DATA_INFO {IN, OUT, TOTAL}
    private static final long serialVersionUID = 83557264L;
    private String name;
    private String tabNumber;
    private DATA_INFO dataInfo;
    private final HashMap<Date, Date> nightTime;
    private final HashMap<Date, Date> dayTime;
    
    public Report09Data(){
        nightTime = new HashMap<>();
        dayTime = new  HashMap<>();
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getTabNumber() {
        return tabNumber;
    }
    public void setTabNumber(String tabNamber) {
        this.tabNumber = tabNamber;
    }

    public DATA_INFO getDataInfo() {
        return dataInfo;
    }
    public void setDataInfo(DATA_INFO dataInfo) {
        this.dataInfo = dataInfo;
    }
    public String getDataInfoStr() {
        switch (dataInfo) {
            case IN:  return "Вход";
            case OUT:  return "Выход";
            case TOTAL:  return "Итого";
            default: return "";
        }
    }
    
    public Date getNightTimeOnDate(Date period) {
        return this.nightTime.get(period);
    }
    public void setNightTimeOnDate(Date period, Date nightTime) {
        this.nightTime.put(period, nightTime);
    }
        
    public Date getDayTimeOnDate(Date period) {
        return this.dayTime.get(period);
    }
    public void setDayTimeOnDate(Date period, Date dayTime) {
        this.dayTime.put(period, dayTime);
    }
    
    public boolean rendered(Date period, int index){
        Date time = index % 2 > 0 ? getDayTimeOnDate(period) : getNightTimeOnDate(period);
        return time != null && !time.equals(new Date(0));
    }
    
    public String getTimeCol(int index){
        if(index % 2 > 0)
            return "border-width: 0px 2px " + String.valueOf(dataInfo == DATA_INFO.TOTAL ? 2 : 0) + "px 0px; " + (dataInfo == DATA_INFO.TOTAL ? "font-weight: bold" : "");
        else
            return "border-width: 0px 1px " + String.valueOf(dataInfo == DATA_INFO.TOTAL ? 2 : 0) + "px 2px; " + (dataInfo == DATA_INFO.TOTAL ? "font-weight: bold" : "");
    }
    
    public String getTotal(){
        if (dataInfo != DATA_INFO.TOTAL) return "";
        
        LocalDateTime ldt0 = LocalDateTime.MIN;
        Date date0 = new Date(0);
        for (Date period : dayTime.keySet()){
            if (dayTime.get(period).equals(date0))
                continue;
            
            LocalDateTime ldt = LocalDateTime.ofInstant(dayTime.get(period).toInstant(), ZoneId.systemDefault());
            ldt0 = ldt0.plusHours(ldt.getHour()).plusMinutes(ldt.getMinute()).plusSeconds(ldt.getSecond());
        }
        for (Date period : nightTime.keySet()){
            if (nightTime.get(period).equals(date0))
                continue;
            
            LocalDateTime ldt = LocalDateTime.ofInstant(nightTime.get(period).toInstant(), ZoneId.systemDefault());
            ldt0 = ldt0.plusHours(ldt.getHour()).plusMinutes(ldt.getMinute()).plusSeconds(ldt.getSecond());
        }
        
        long hrs = TimeUnit.DAYS.toHours(ldt0.getDayOfYear()) - 24 + ldt0.getHour();
        long min = ldt0.getMinute();
        return String.format("%02d:%02d", hrs, min);
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += Objects.hash(name, tabNumber, dataInfo);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Report09Data)) {
            return false;
        }
        Report09Data other = (Report09Data) object;
        return this.name.equals(other.name) && this.tabNumber.equals(other.tabNumber) && this.dataInfo.equals(other.dataInfo);
    }

    @Override
    public String toString() {
        return "ru.it_mm.winpak.views.report9.Report09Data[ name=" + name + "   " + " tabNumber=" + tabNumber + " ]";
    }
    

    
}
