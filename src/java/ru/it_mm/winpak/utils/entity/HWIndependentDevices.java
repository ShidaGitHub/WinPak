package ru.it_mm.winpak.utils.entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "HWIndependentDevices")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "HWIndependentDevices.findAll", query = "SELECT h FROM HWIndependentDevices h")
    , @NamedQuery(name = "HWIndependentDevices.findByDeviceID", query = "SELECT h FROM HWIndependentDevices h WHERE h.deviceID = :deviceID")    
    , @NamedQuery(name = "HWIndependentDevices.findByDeleted", query = "SELECT h FROM HWIndependentDevices h WHERE h.deleted = :deleted")    
    , @NamedQuery(name = "HWIndependentDevices.findByName", query = "SELECT h FROM HWIndependentDevices h WHERE h.name = :name")})
public class HWIndependentDevices implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "DeviceID")
    private Integer deviceID;    
    @Column(name = "Deleted")
    private Short deleted;    
    @Size(max = 40)
    @Column(name = "Name")
    private String name;

    public HWIndependentDevices() {}

    public HWIndependentDevices(Integer deviceID) {
        this.deviceID = deviceID;
    }

    public Integer getDeviceID() {
        return deviceID;
    }
    public void setDeviceID(Integer deviceID) {
        this.deviceID = deviceID;
    }
    
    public Short getDeleted() {
        return deleted;
    }
    public void setDeleted(Short deleted) {
        this.deleted = deleted;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (deviceID != null ? deviceID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof HWIndependentDevices)) {
            return false;
        }
        HWIndependentDevices other = (HWIndependentDevices) object;
        return this.deviceID.equals(other.deviceID);
    }

    @Override
    public String toString() {
        return "ru.it_mm.winpak.utils.entity.HWIndependentDevices[ deviceID=" + deviceID + "  name=" +  name + " ]";
    }
    
}
