package com.syscxp.header.vpn.host;

import com.syscxp.header.vo.ForeignKey;
import com.syscxp.header.vo.ForeignKey.ReferenceOption;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table
public class HostInterfaceVO {
    @Id
    @Column
    private String uuid;
    @Column
    private String name;
    @Column
    @ForeignKey(parentEntityClass = VpnHostVO.class, parentKey = "uuid", onDeleteAction = ReferenceOption.CASCADE)
    private String hostUuid;
    @Column
    private String endpointUuid;
    @Column
    private String interfaceName;
    @Column
    private Timestamp lastOpDate;
    @Column
    private Timestamp createDate;
    @PreUpdate
    private void preUpdate() {
        lastOpDate = null;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHostUuid() {
        return hostUuid;
    }

    public void setHostUuid(String hostUuid) {
        this.hostUuid = hostUuid;
    }

    public String getEndpointUuid() {
        return endpointUuid;
    }

    public void setEndpointUuid(String endpointUuid) {
        this.endpointUuid = endpointUuid;
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
}
