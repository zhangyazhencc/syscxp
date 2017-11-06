package com.syscxp.vpn.header.vpn;

import com.syscxp.header.search.Inventory;
import com.syscxp.vpn.header.host.VpnHostInventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = VpnVO.class)
public class VpnInventory {
    private String uuid;
    private String accountUuid;
    private String name;
    private String description;
    private String vpnCidr;
    private Integer port;
    private Integer vlan;
    private Long bandwidth;
    private String endpointUuid;
    private String status;
    private String state;
    private Integer duration;
    private Timestamp expiredDate;
    private Integer maxModifies;
    private String sid;
    private String certKey;
    private Payment payment;
    private VpnHostInventory hostInventory;
    private List<VpnInterfaceInventory> interfaceInventories;
    private List<VpnRouteInventory> routeInventories;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static VpnInventory valueOf(VpnVO vo) {
        VpnInventory inv = new VpnInventory();
        inv.setUuid(vo.getUuid());
        inv.setAccountUuid(vo.getAccountUuid());
        inv.setName(vo.getName());
        inv.setDescription(vo.getDescription());
        inv.setVpnCidr(vo.getVpnCidr());
        inv.setBandwidth(vo.getBandwidth());
        inv.setEndpointUuid(vo.getEndpointUuid());
        inv.setStatus(vo.getStatus().toString());
        inv.setState(vo.getState().toString());
        inv.setPort(vo.getPort());
        inv.setDuration(vo.getDuration());
        inv.setExpiredDate(vo.getExpireDate());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());
        inv.setSid(vo.getSid());
        inv.setCertKey(vo.getCertKey());
        inv.setMaxModifies(vo.getMaxModifies());
        inv.setPayment(vo.getPayment());
        inv.setHostInventory(VpnHostInventory.valueOf(vo.getVpnHost()));
        inv.setInterfaceInventories(VpnInterfaceInventory.valueOf(vo.getVpnInterfaces()));
        inv.setRouteInventories(VpnRouteInventory.valueOf(vo.getVpnRoutes()));
        return inv;
    }

    public static List<VpnInventory> valueOf(Collection<VpnVO> vos) {
        List<VpnInventory> invs = new ArrayList<>();
        for (VpnVO vo : vos) {
            invs.add(VpnInventory.valueOf(vo));
        }

        return invs;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getVlan() {
        return vlan;
    }

    public void setVlan(Integer vlan) {
        this.vlan = vlan;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Timestamp getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(Timestamp expiredDate) {
        this.expiredDate = expiredDate;
    }

    public Integer getMaxModifies() {
        return maxModifies;
    }

    public void setMaxModifies(Integer maxModifies) {
        this.maxModifies = maxModifies;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getCertKey() {
        return certKey;
    }

    public void setCertKey(String certKey) {
        this.certKey = certKey;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public VpnHostInventory getHostInventory() {
        return hostInventory;
    }

    public void setHostInventory(VpnHostInventory hostInventory) {
        this.hostInventory = hostInventory;
    }

    public List<VpnInterfaceInventory> getInterfaceInventories() {
        return interfaceInventories;
    }

    public void setInterfaceInventories(List<VpnInterfaceInventory> interfaceInventories) {
        this.interfaceInventories = interfaceInventories;
    }

    public List<VpnRouteInventory> getRouteInventories() {
        return routeInventories;
    }

    public void setRouteInventories(List<VpnRouteInventory> routeInventories) {
        this.routeInventories = routeInventories;
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
