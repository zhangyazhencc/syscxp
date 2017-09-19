package org.zstack.vpn.header.vpn;

import org.zstack.header.search.TriggerIndex;
import org.zstack.vpn.header.host.VpnState;
import org.zstack.vpn.header.host.VpnStatus;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;

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
    private String hostUuid;
    @Column
    private String name;
    @Column
    private String description;
    @Column
    private String vpnCidr;
    @Column
    private Integer bandwidth;
    @Column
    private String endpointUuid;
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

    private Set<VpnInterfaceVO> vpnInterfaces;

    private Set<VpnRouteVO> vpnRoutes;

    public Set<VpnInterfaceVO> getVpnInterfaces() {
        return vpnInterfaces;
    }

    public void setVpnInterfaces(Set<VpnInterfaceVO> vpnInterfaces) {
        this.vpnInterfaces = vpnInterfaces;
    }

    public Set<VpnRouteVO> getVpnRoutes() {
        return vpnRoutes;
    }

    public void setVpnRoutes(Set<VpnRouteVO> vpnRoutes) {
        this.vpnRoutes = vpnRoutes;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public VpnState getState() {
        return state;
    }

    public void setState(VpnState state) {
        this.state = state;
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

    public Integer getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(Integer bandwidth) {
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

    public void setStatus(VpnStatus status) {
        this.status = status;
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
