/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "Card")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Card.findAll", query = "SELECT c FROM Card c")
    , @NamedQuery(name = "Card.findByRecordID", query = "SELECT c FROM Card c WHERE c.recordID = :recordID")    
    , @NamedQuery(name = "Card.findByTimeStamp", query = "SELECT c FROM Card c WHERE c.timeStamp = :timeStamp")        
    , @NamedQuery(name = "Card.findByDeleted", query = "SELECT c FROM Card c WHERE c.deleted = :deleted")    
    , @NamedQuery(name = "Card.findByCardNumber", query = "SELECT c FROM Card c WHERE c.cardNumber = :cardNumber")        
    , @NamedQuery(name = "Card.findByActivationDate", query = "SELECT c FROM Card c WHERE c.activationDate = :activationDate")
    , @NamedQuery(name = "Card.findByExpirationDate", query = "SELECT c FROM Card c WHERE c.expirationDate = :expirationDate")    
    , @NamedQuery(name = "Card.findByCardStatus", query = "SELECT c FROM Card c WHERE c.cardStatus = :cardStatus")    
    , @NamedQuery(name = "Card.findByLastReaderDate", query = "SELECT c FROM Card c WHERE c.lastReaderDate = :lastReaderDate")})
public class Card implements Serializable {
    private static final long serialVersionUID = 43617264L;
    
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "RecordID")
    private Integer recordID;    
    @Column(name = "TimeStamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timeStamp;
    @Column(name = "Deleted")
    private Short deleted;    //Удалена
    @Size(max = 30)
    @Column(name = "CardNumber")
    private String cardNumber; //Номер карты
    
    @ManyToOne
    @JoinColumn(name="CardHolderID")
    private CardHolder cardHolder;    
    
    @Column(name = "ActivationDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date activationDate; // Дата активации
    @Column(name = "ExpirationDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expirationDate; // Дата истечения срока карты
    @Column(name = "CardStatus")
    private Short cardStatus;   // Статус
    @Column(name = "LastReaderDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastReaderDate;

    public Card() {}

    public Card(Integer recordID) {
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

    public String getCardNumber() {
        return cardNumber;
    }
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public CardHolder getCardHolder() {
        return cardHolder;
    }
    public void setCardHolder(CardHolder cardHolder) {
        this.cardHolder = cardHolder;
    }

    public Date getActivationDate() {
        return activationDate;
    }
    public void setActivationDate(Date activationDate) {
        this.activationDate = activationDate;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }
    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Short getCardStatus() {
        return cardStatus;
    }
    public void setCardStatus(Short cardStatus) {
        this.cardStatus = cardStatus;
    }

    public Date getLastReaderDate() {
        return lastReaderDate;
    }
    public void setLastReaderDate(Date lastReaderDate) {
        this.lastReaderDate = lastReaderDate;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (recordID != null ? recordID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Card)) {
            return false;
        }
        Card other = (Card) object;
        return this.recordID.equals(other.recordID);
    }

    @Override
    public String toString() {
        return "ru.it_mm.winpak.utils.entity.Card[ recordID=" + recordID + "   " + " cardNumber=" + cardNumber + " ]";
    }
    
}
