package org.zstack.vpn.header.vpn;

import org.zstack.header.search.TriggerIndex;
import org.zstack.header.vo.ForeignKey;
import org.zstack.header.vo.ForeignKey.ReferenceOption;
import org.zstack.vpn.header.host.VpnHostVO;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table
@TriggerIndex
public class VpnVO {

    @Id
    @Column
    private String uuid;
    @Column
    private String accountUuid;
    @Column
    @ForeignKey(parentEntityClass = VpnHostVO.class, parentKey = "uuid", onDeleteAction = ReferenceOption.CASCADE)
    private String hostUuid;
    @Column
    private String name;
    @Column
    private String description;
    @Column
    private String vpnCidr;
    @Column
    private Long bandwidth;
    @Column
    private String endpointUuid;
    @Column
    private String port;
    @Column
    private String vlan;
    @Column
    @Enumerated(EnumType.STRING)
    private VpnState state;
    @Column
    @Enumerated(EnumType.STRING)
    private VpnStatus status;
    @Column
    private Integer months;
    @Column
    private Timestamp expiredDate;
    @Column
    private Timestamp lastOpDate;
    @Column
    private Timestamp createDate;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getVlan() {
        return vlan;
    }

    public void setVlan(String vlan) {
        this.vlan = vlan;
    }

    public VpnState getState() {
        return state;
    }

    public void setState(VpnState state) {
        this.state = state;
    }

    public void setStatus(VpnStatus status) {
        this.status = status;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public String getHostUuid() {
        return hostUuid;
    }

    public void setHostUuid(String hostUuid) {
        this.hostUuid = hostUuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVpnCidr() {
        return vpnCidr;
    }

    public void setVpnCidr(String vpnCidr) {
        this.vpnCidr = vpnCidr;
    }

    public Long getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(Long bandwidth) {
        this.bandwidth = bandwidth;
    }

    public String getEndpointUuid() {
        return endpointUuid;
    }

    public void setEndpointUuid(String endpointUuid) {
        this.endpointUuid = endpointUuid;
    }

    public VpnStatus getStatus() {
        return status;
    }

    public Integer getMonths() {
        return months;
    }

    public void setMonths(Integer months) {
        this.months = months;
    }

    public Timestamp getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(Timestamp expiredDate) {
        this.expiredDate = expiredDate;
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
