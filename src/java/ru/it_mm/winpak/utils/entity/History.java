package ru.it_mm.winpak.utils.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "History")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "History.findAll", query = "SELECT h FROM History h")
    , @NamedQuery(name = "History.findByRecordID", query = "SELECT h FROM History h WHERE h.recordID = :recordID")   
    , @NamedQuery(name = "History.findByTimeStamp", query = "SELECT h FROM History h WHERE h.timeStamp = :timeStamp")    
    , @NamedQuery(name = "History.findByDeleted", query = "SELECT h FROM History h WHERE h.deleted = :deleted")
    , @NamedQuery(name = "History.findByParam1", query = "SELECT h FROM History h WHERE h.param1 = :param1")
    , @NamedQuery(name = "History.findByGenTime", query = "SELECT h FROM History h WHERE h.genTime = :genTime")})

public class History implements Serializable {
    private static final long serialVersionUID = 743696654L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "RecordID")
    private Integer recordID;    
    @Column(name = "TimeStamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timeStamp;    
    @Column(name = "Deleted")
    private Short deleted;    
    @Column(name = "GenTime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date genTime;
    @Column(name = "Param1")
    private Integer param1;
    @Column(name = "Param3")
    private String param3;    
    
    @ManyToOne
    @JoinColumn(name="Link1")
    private HWIndependentDevices hWIndependentDevices;
    
    @ManyToOne
    @JoinColumn(name="Link2")
    private Card card;
    
    @ManyToOne
    @JoinColumn(name="Link3")
    private CardHolder cardHolder;

    public History() {}

    public History(Integer recordID) {
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

    public Date getGenTime() {
        return genTime;
    }
    public void setGenTime(Date genTime) {
        this.genTime = genTime;
    }

    public CardHolder getCardHolder() {
        return cardHolder;
    }
    public void setCardHolder(CardHolder cardHolder) {
        this.cardHolder = cardHolder;
    }
    
    public HWIndependentDevices getHWIndependentDevices() {
        return hWIndependentDevices;
    }
    public void setHWIndependentDevices(HWIndependentDevices hWIndependentDevices) {
        this.hWIndependentDevices = hWIndependentDevices;
    }

    public Card getCard() {
        return card;
    }
    public void setCard(Card card) {
        this.card = card;
    }
      
    public Integer getParam1() {
        return param1;
    }
    public void setParam1(Integer param1) {
        this.param1 = param1;
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
        hash += (recordID != null ? recordID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof History)) {
            return false;
        }
        History other = (History) object;
        return this.recordID.equals(other.recordID);
    }

    @Override
    public String toString() {
        return "ru.it_mm.winpak.utils.entity.History[ recordID=" + recordID + " ]";
    }
    
}
