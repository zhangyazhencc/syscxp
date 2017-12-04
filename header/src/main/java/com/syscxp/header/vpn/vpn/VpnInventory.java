package com.syscxp.header.vpn.vpn;

import com.syscxp.header.search.Inventory;
import com.syscxp.header.vpn.host.VpnHostInventory;

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
    private Integer port;
    private Integer vlan;
    private String bandwidth;
    private String interfaceUuid;
    private String status;
    private String state;
    private Integer duration;
    private Timestamp expireDate;
    private Integer maxModifies;
    private String sid;
    private String certKey;
    private String payment;
    private VpnHostInventory hostInventory;
    private VpnCertInventory certInventory;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static VpnInventory valueOf(VpnVO vo) {
        VpnInventory inv = new VpnInventory();
        inv.setUuid(vo.getUuid());
        inv.setAccountUuid(vo.getAccountUuid());
        inv.setName(vo.getName());
        inv.setDescription(vo.getDescription());
        inv.setBandwidth(vo.getBandwidthOfferingUuid());
        inv.setInterfaceUuid(vo.getInterfaceUuid());
        inv.setStatus(vo.getStatus().toString());
        inv.setState(vo.getState().toString());
        inv.setPort(vo.getPort());
        inv.setVlan(vo.getVlan());
        inv.setDuration(vo.getDuration());
        inv.setExpireDate(vo.getExpireDate());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());
        inv.setSid(vo.getSid());
        inv.setCertKey(vo.getCertKey());
        inv.setMaxModifies(vo.getMaxModifies());
        inv.setPayment(vo.getPayment().toString());
        inv.setHostInventory(VpnHostInventory.valueOf(vo.getVpnHost()));
        if (vo.getVpnCert() != null)
            inv.setCertInventory(VpnCertInventory.valueOf(vo.getVpnCert()));
        return inv;
    }

    public static List<VpnInventory> valueOf(Collection<VpnVO> vos) {
        List<VpnInventory> invs = new ArrayList<>();
        for (VpnVO vo : vos) {
            invs.add(VpnInventory.valueOf(vo));
        }

        return invs;
    }

    public VpnCertInventory getCertInventory() {
        return certInventory;
    }

    public void setCertInventory(VpnCertInventory certInventory) {
        this.certInventory = certInventory;
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

    public String getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(String bandwidth) {
        this.bandwidth = bandwidth;
    }

    public String getInterfaceUuid() {
        return interfaceUuid;
    }

    public void setInterfaceUuid(String interfaceUuid) {
        this.interfaceUuid = interfaceUuid;
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

    public Timestamp getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Timestamp expireDate) {
        this.expireDate = expireDate;
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

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public VpnHostInventory getHostInventory() {
        return hostInventory;
    }

    public void setHostInventory(VpnHostInventory hostInventory) {
        this.hostInventory = hostInventory;
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
