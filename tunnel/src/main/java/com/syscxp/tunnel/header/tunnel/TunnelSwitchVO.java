package com.syscxp.tunnel.header.tunnel;

import com.syscxp.header.vo.ForeignKey;
import com.syscxp.tunnel.header.endpoint.EndpointEO;
import com.syscxp.tunnel.header.endpoint.EndpointVO;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by DCY on 2017-11-01
 */
@Entity
@Table
@Inheritance(strategy = InheritanceType.JOINED)
public class TunnelSwitchVO {
    @Id
    @Column
    private String uuid;

    @Column
    @ForeignKey(parentEntityClass = TunnelEO.class, onDeleteAction = ForeignKey.ReferenceOption.SET_NULL)
    private String tunnelUuid;

    @Column
    @ForeignKey(parentEntityClass = EndpointEO.class, onDeleteAction = ForeignKey.ReferenceOption.SET_NULL)
    private String endpointUuid;

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="endpointUuid", insertable=false, updatable=false)
    private EndpointVO endpointVO;

    @Column
    private String switchPortUuid;

    @Column
    @Enumerated(EnumType.STRING)
    private NetworkType type;

    @Column
    private Integer vlan;

    @Column
    private String sortTag;

    @Column
    private Timestamp createDate;

    @Column
    private Timestamp lastOpDate;

    @PreUpdate
    private void preUpdate() {
        lastOpDate = null;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTunnelUuid() {
        return tunnelUuid;
    }

    public void setTunnelUuid(String tunnelUuid) {
        this.tunnelUuid = tunnelUuid;
    }

    public String getEndpointUuid() {
        return endpointUuid;
    }

    public void setEndpointUuid(String endpointUuid) {
        this.endpointUuid = endpointUuid;
    }

    public EndpointVO getEndpointVO() {
        return endpointVO;
    }

    public void setEndpointVO(EndpointVO endpointVO) {
        this.endpointVO = endpointVO;
    }

    public String getSwitchPortUuid() {
        return switchPortUuid;
    }

    public void setSwitchPortUuid(String switchPortUuid) {
        this.switchPortUuid = switchPortUuid;
    }

    public NetworkType getType() {
        return type;
    }

    public void setType(NetworkType type) {
        this.type = type;
    }

    public Integer getVlan() {
        return vlan;
    }

    public void setVlan(Integer vlan) {
        this.vlan = vlan;
    }

    public String getSortTag() {
        return sortTag;
    }

    public void setSortTag(String sortTag) {
        this.sortTag = sortTag;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public Timestamp getLastOpDate() {
        return lastOpDate;
    }

    public void setLastOpDate(Timestamp lastOpDate) {
        this.lastOpDate = lastOpDate;
    }
}
