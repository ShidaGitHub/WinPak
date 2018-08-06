package ru.it_mm.winpak.utils.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;


@Entity
@Table(name = "CardHolder")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CardHolder.findAll", query = "SELECT c FROM CardHolder c")
    , @NamedQuery(name = "CardHolder.findByRecordID", query = "SELECT c FROM CardHolder c WHERE c.recordID = :recordID")    
    , @NamedQuery(name = "CardHolder.findByTimeStamp", query = "SELECT c FROM CardHolder c WHERE c.timeStamp = :timeStamp")        
    , @NamedQuery(name = "CardHolder.findByDeleted", query = "SELECT c FROM CardHolder c WHERE c.deleted = :deleted")    
    , @NamedQuery(name = "CardHolder.findByFirstName", query = "SELECT c FROM CardHolder c WHERE c.firstName = :firstName")
    , @NamedQuery(name = "CardHolder.findByLastName", query = "SELECT c FROM CardHolder c WHERE c.lastName = :lastName")
    , @NamedQuery(name = "CardHolder.findByNote1", query = "SELECT c FROM CardHolder c WHERE c.note1 = :note1")
    , @NamedQuery(name = "CardHolder.findByNote2", query = "SELECT c FROM CardHolder c WHERE c.note2 = :note2")
    , @NamedQuery(name = "CardHolder.findByNote3", query = "SELECT c FROM CardHolder c WHERE c.note3 = :note3")
    , @NamedQuery(name = "CardHolder.findByNote4", query = "SELECT c FROM CardHolder c WHERE c.note4 = :note4")
    , @NamedQuery(name = "CardHolder.findByNote5", query = "SELECT c FROM CardHolder c WHERE c.note5 = :note5")
    , @NamedQuery(name = "CardHolder.findByNote6", query = "SELECT c FROM CardHolder c WHERE c.note6 = :note6")
    , @NamedQuery(name = "CardHolder.findByNote7", query = "SELECT c FROM CardHolder c WHERE c.note7 = :note7")
    , @NamedQuery(name = "CardHolder.findByNote8", query = "SELECT c FROM CardHolder c WHERE c.note8 = :note8")
    , @NamedQuery(name = "CardHolder.findByNote9", query = "SELECT c FROM CardHolder c WHERE c.note9 = :note9")
    , @NamedQuery(name = "CardHolder.findByNote10", query = "SELECT c FROM CardHolder c WHERE c.note10 = :note10")
    , @NamedQuery(name = "CardHolder.findByNote11", query = "SELECT c FROM CardHolder c WHERE c.note11 = :note11")
    , @NamedQuery(name = "CardHolder.findByNote12", query = "SELECT c FROM CardHolder c WHERE c.note12 = :note12")
    , @NamedQuery(name = "CardHolder.findByNote13", query = "SELECT c FROM CardHolder c WHERE c.note13 = :note13")
    , @NamedQuery(name = "CardHolder.findByNote14", query = "SELECT c FROM CardHolder c WHERE c.note14 = :note14")
    , @NamedQuery(name = "CardHolder.findByNote15", query = "SELECT c FROM CardHolder c WHERE c.note15 = :note15")
    , @NamedQuery(name = "CardHolder.findByNote16", query = "SELECT c FROM CardHolder c WHERE c.note16 = :note16")
    , @NamedQuery(name = "CardHolder.findByNote17", query = "SELECT c FROM CardHolder c WHERE c.note17 = :note17")
    , @NamedQuery(name = "CardHolder.findByNote18", query = "SELECT c FROM CardHolder c WHERE c.note18 = :note18")
    , @NamedQuery(name = "CardHolder.findByNote19", query = "SELECT c FROM CardHolder c WHERE c.note19 = :note19")
    , @NamedQuery(name = "CardHolder.findByNote20", query = "SELECT c FROM CardHolder c WHERE c.note20 = :note20")
    , @NamedQuery(name = "CardHolder.findByNote21", query = "SELECT c FROM CardHolder c WHERE c.note21 = :note21")
    , @NamedQuery(name = "CardHolder.findByNote22", query = "SELECT c FROM CardHolder c WHERE c.note22 = :note22")
    , @NamedQuery(name = "CardHolder.findByNote23", query = "SELECT c FROM CardHolder c WHERE c.note23 = :note23")
    , @NamedQuery(name = "CardHolder.findByNote24", query = "SELECT c FROM CardHolder c WHERE c.note24 = :note24")
    , @NamedQuery(name = "CardHolder.findByNote25", query = "SELECT c FROM CardHolder c WHERE c.note25 = :note25")
    , @NamedQuery(name = "CardHolder.findByNote26", query = "SELECT c FROM CardHolder c WHERE c.note26 = :note26")
    , @NamedQuery(name = "CardHolder.findByNote27", query = "SELECT c FROM CardHolder c WHERE c.note27 = :note27")
    , @NamedQuery(name = "CardHolder.findByNote28", query = "SELECT c FROM CardHolder c WHERE c.note28 = :note28")
    , @NamedQuery(name = "CardHolder.findByNote29", query = "SELECT c FROM CardHolder c WHERE c.note29 = :note29")})
public class CardHolder implements Serializable {
    private static final long serialVersionUID = 20660562L;
    
    
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "RecordID")
    private Integer recordID;    
    @Column(name = "TimeStamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timeStamp;    
    @Column(name = "Deleted")
    private Short deleted; //Удален    
    @Size(max = 30)
    @Column(name = "FirstName")
    private String firstName; //Имя
    @Size(max = 30)
    @Column(name = "LastName")
    private String lastName; //Фамилия
    @Size(max = 64)
    @Column(name = "Note1")
    private String note1; //Отчество
    @Size(max = 64)
    @Column(name = "Note2")
    private String note2; //Табельный номер
    @Size(max = 64)
    @Column(name = "Note3")
    private String note3; //Цех
    @Size(max = 64)
    @Column(name = "Note4")
    private String note4; //Служба
    @Size(max = 64)
    @Column(name = "Note5")
    private String note5; //Цех(полн. наименован.)
    @Size(max = 64)
    @Column(name = "Note6")
    private String note6; //Участок
    @Size(max = 64)
    @Column(name = "Note7")
    private String note7; //Должность
    @Size(max = 64)
    @Column(name = "Note8")
    private String note8; //Дата рождения
    @Size(max = 64)
    @Column(name = "Note9")
    private String note9; //Место рождения
    @Size(max = 64)
    @Column(name = "Note10")
    private String note10; //Место прописки
    @Size(max = 64)
    @Column(name = "Note11")
    private String note11; //Место проживания
    @Size(max = 64)
    @Column(name = "Note12")
    private String note12; //Семейное положение
    @Size(max = 64)
    @Column(name = "Note13")
    private String note13; //Образование
    @Size(max = 64)
    @Column(name = "Note14")
    private String note14; //Серия номер паспорта
    @Size(max = 64)
    @Column(name = "Note15")
    private String note15; //Кем выдан
    @Size(max = 64)
    @Column(name = "Note16")
    private String note16; //Дата выдачи
    @Size(max = 64)
    @Column(name = "Note17")
    private String note17; //Дата приема на работу
    @Size(max = 64)
    @Column(name = "Note18")
    private String note18; //Характер работы
    @Size(max = 64)
    @Column(name = "Note19")
    private String note19; //Номер приказа об увольнении
    @Size(max = 64)
    @Column(name = "Note20")
    private String note20; //Дата приказа об увольнении
    @Size(max = 64)
    @Column(name = "Note21")
    private String note21; //Дата увольнения
    @Size(max = 64)
    @Column(name = "Note22")
    private String note22; //Должность сокр
    @Size(max = 64)
    @Column(name = "Note23")
    private String note23; //График
    @Size(max = 64)
    @Column(name = "Note24")
    private String note24; //Заказчик
    @Size(max = 64)
    @Column(name = "Note25")
    private String note25; //Причина
    @Size(max = 64)
    @Column(name = "Note26")
    private String note26;
    @Size(max = 64)
    @Column(name = "Note27")
    private String note27;
    @Size(max = 64)
    @Column(name = "Note28")
    private String note28;
    @Size(max = 64)
    @Column(name = "Note29")
    private String note29;
    @Size(max = 64)
    
    public CardHolder() {
    }

    public CardHolder(Integer recordID) {
        this.recordID = recordID;
    }

    public Integer getRecordID() {
        return recordID;
    }
    public void setRecordID(Integer recordID) {
        this.recordID = recordID;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }
    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Short getDeleted() {
        return deleted;
    }
    public void setDeleted(Short deleted) {
        this.deleted = deleted;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNote1() {
        return note1;
    }
    public void setNote1(String note1) {
        this.note1 = note1;
    }

    public String getNote2() {
        return note2;
    }
    public void setNote2(String note2) {
        this.note2 = note2;
    }

    public String getNote3() {
        return note3;
    }
    public void setNote3(String note3) {
        this.note3 = note3;
    }

    public String getNote4() {
        return note4;
    }
    public void setNote4(String note4) {
        this.note4 = note4;
    }

    public String getNote5() {
        return note5;
    }
    public void setNote5(String note5) {
        this.note5 = note5;
    }

    public String getNote6() {
        return note6;
    }
    public void setNote6(String note6) {
        this.note6 = note6;
    }

    public String getNote7() {
        return note7;
    }
    public void setNote7(String note7) {
        this.note7 = note7;
    }

    public String getNote8() {
        return note8;
    }
    public void setNote8(String note8) {
        this.note8 = note8;
    }

    public String getNote9() {
        return note9;
    }
    public void setNote9(String note9) {
        this.note9 = note9;
    }

    public String getNote10() {
        return note10;
    }
    public void setNote10(String note10) {
        this.note10 = note10;
    }

    public String getNote11() {
        return note11;
    }
    public void setNote11(String note11) {
        this.note11 = note11;
    }

    public String getNote12() {
        return note12;
    }
    public void setNote12(String note12) {
        this.note12 = note12;
    }

    public String getNote13() {
        return note13;
    }
    public void setNote13(String note13) {
        this.note13 = note13;
    }

    public String getNote14() {
        return note14;
    }
    public void setNote14(String note14) {
        this.note14 = note14;
    }

    public String getNote15() {
        return note15;
    }
    public void setNote15(String note15) {
        this.note15 = note15;
    }

    public String getNote16() {
        return note16;
    }
    public void setNote16(String note16) {
        this.note16 = note16;
    }

    public String getNote17() {
        return note17;
    }
    public void setNote17(String note17) {
        this.note17 = note17;
    }

    public String getNote18() {
        return note18;
    }
    public void setNote18(String note18) {
        this.note18 = note18;
    }

    public String getNote19() {
        return note19;
    }
    public void setNote19(String note19) {
        this.note19 = note19;
    }

    public String getNote20() {
        return note20;
    }
    public void setNote20(String note20) {
        this.note20 = note20;
    }

    public String getNote21() {
        return note21;
    }
    public void setNote21(String note21) {
        this.note21 = note21;
    }

    public String getNote22() {
        return note22;
    }
    public void setNote22(String note22) {
        this.note22 = note22;
    }

    public String getNote23() {
        return note23;
    }
    public void setNote23(String note23) {
        this.note23 = note23;
    }

    public String getNote24() {
        return note24;
    }
    public void setNote24(String note24) {
        this.note24 = note24;
    }

    public String getNote25() {
        return note25;
    }
    public void setNote25(String note25) {
        this.note25 = note25;
    }

    public String getNote26() {
        return note26;
    }
    public void setNote26(String note26) {
        this.note26 = note26;
    }

    public String getNote27() {
        return note27;
    }
    public void setNote27(String note27) {
        this.note27 = note27;
    }

    public String getNote28() {
        return note28;
    }
    public void setNote28(String note28) {
        this.note28 = note28;
    }

    public String getNote29() {
        return note29;
    }
    public void setNote29(String note29) {
        this.note29 = note29;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (recordID != null ? recordID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {        
        if (!(object instanceof CardHolder)) {
            return false;
        }
        CardHolder other = (CardHolder) object;
        return this.recordID.equals(other.recordID);
    }

    @Override
    public String toString() {
        return "ru.it_mm.winpak.utils.entity.CardHolder[ recordID=" + recordID + " ]";
    }
    
}
