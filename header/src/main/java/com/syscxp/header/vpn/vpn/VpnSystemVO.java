package com.syscxp.header.vpn.vpn;

import com.syscxp.header.core.converter.MapAttributeConverter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Map;

@Entity
@Table
public class VpnSystemVO {

    @Id
    @Column
    private String uuid;

    @Column
    @Convert(converter = MapAttributeConverter.class)
    private Map<String, Object> vpn;

    @Column
    @Convert(converter = MapAttributeConverter.class)
    private Map<String, Object> system;

    @Column
    @Convert(converter = MapAttributeConverter.class)
    private Map<String, Object> tap;

    @Column
    private Timestamp lastOpDate;

    @Column
    private Timestamp createDate;

    @PreUpdate
    private void preUpdate() {
        lastOpDate = null;
    }

    public Timestamp getLastOpDate() {
        return lastOpDate;
    }

    public void setLastOpDate(Timestamp lastOpDate) {
        this.lastOpDate = lastOpDate;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Map<String, Object> getVpn() {
        return vpn;
    }

    public void setVpn(Map<String, Object> vpn) {
        this.vpn = vpn;
    }

    public Map<String, Object> getSystem() {
        return system;
    }

    public void setSystem(Map<String, Object> system) {
        this.system = system;
    }

    public Map<String, Object> getTap() {
        return tap;
    }

    public void setTap(Map<String, Object> tap) {
        this.tap = tap;
    }
}
