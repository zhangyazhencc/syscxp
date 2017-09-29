package org.zstack.vpn.header.vpn;

import org.zstack.header.search.TriggerIndex;
import org.zstack.vpn.header.host.VpnHostVO;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

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

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="hostUuid", insertable=false, updatable=false)
    private VpnHostVO vpnHost;
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
    private Integer port;
    @Column
    @Enumerated(EnumType.STRING)
    private VpnState state;
    @Column
    @Enumerated(EnumType.STRING)
    private VpnStatus status;
    @Column
    private Integer duration;
    @Column
    private Timestamp expireDate;
    @Column
    private Timestamp lastOpDate;
    @Column
    private Timestamp createDate;
    @Column
    private String sid;
    @Column
    private String key;
    @Column
    private Payment payment;
    @Column
    private Integer maxModifies;

    public Integer getMaxModifies() {
        return maxModifies;
    }

    public void setMaxModifies(Integer maxModifies) {
        this.maxModifies = maxModifies;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getHostUuid() {
        return hostUuid;
    }

    public void setHostUuid(String hostUuid) {
        this.hostUuid = hostUuid;
    }

    @OneToMany(fetch= FetchType.EAGER)
    @JoinColumn(name = "vpnUuid", insertable = false, updatable = false)
    private List<VpnInterfaceVO> vpnInterfaces = new ArrayList<>();

    @OneToMany(fetch= FetchType.EAGER)
    @JoinColumn(name = "vpnUuid", insertable = false, updatable = false)
    private List<VpnRouteVO> vpnRoutes = new ArrayList<>();

    public VpnHostVO getVpnHost() {
        return vpnHost;
    }

    public void setVpnHost(VpnHostVO vpnHost) {
        this.vpnHost = vpnHost;
    }

    public List<VpnInterfaceVO> getVpnInterfaces() {
        return vpnInterfaces;
    }

    public void setVpnInterfaces(List<VpnInterfaceVO> vpnInterfaces) {
        this.vpnInterfaces = vpnInterfaces;
    }

    public List<VpnRouteVO> getVpnRoutes() {
        return vpnRoutes;
    }

    public void setVpnRoutes(List<VpnRouteVO> vpnRoutes) {
        this.vpnRoutes = vpnRoutes;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
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

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Timestamp getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Timestamp expireDate) {
        this.expireDate = expireDate;
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
