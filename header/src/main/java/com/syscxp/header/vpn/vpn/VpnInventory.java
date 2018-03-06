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
    private String endpointUuid;
    private String tunnelUuid;
    private String status;
    private String vpnCertUuid;
    private String vpnCertName;
    private String state;
    private Integer duration;
    private Timestamp expireDate;
    private Integer maxModifies;
    private String certKey;
    private String clientConf;
    private String payment;
    private String vpnHostName;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static VpnInventory valueOf(VpnVO vo) {
        VpnInventory inv = new VpnInventory();
        inv.setUuid(vo.getUuid());
        inv.setAccountUuid(vo.getAccountUuid());
        inv.setName(vo.getName());
        inv.setDescription(vo.getDescription());
        inv.setBandwidth(vo.getBandwidthOfferingUuid());
        inv.setEndpointUuid(vo.getEndpointUuid());
        inv.setStatus(vo.getStatus().toString());
        inv.setState(vo.getState().toString());
        inv.setPort(vo.getPort());
        inv.setVlan(vo.getVlan());
        inv.setTunnelUuid(vo.getTunnelUuid());
        inv.setDuration(vo.getDuration());
        inv.setExpireDate(vo.getExpireDate());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());
        inv.setCertKey(vo.getCertKey());
        inv.setMaxModifies(vo.getMaxModifies());
        inv.setVpnCertUuid(vo.getVpnCertUuid());
        inv.setClientConf(vo.getClientConf());
        inv.setPayment(vo.getPayment().toString());
        inv.setVpnHostName(vo.getVpnHost().getName());
        inv.setVpnCertName("");
        if (vo.getVpnCert() != null)
            inv.setVpnCertName(vo.getVpnCert().getName());
        return inv;
    }

    public static List<VpnInventory> valueOf(Collection<VpnVO> vos) {
        List<VpnInventory> invs = new ArrayList<>();
        for (VpnVO vo : vos) {
            invs.add(VpnInventory.valueOf(vo));
        }

        return invs;
    }

    public String getVpnHostName() {
        return vpnHostName;
    }

    public void setVpnHostName(String vpnHostName) {
        this.vpnHostName = vpnHostName;
    }

    public String getVpnCertName() {
        return vpnCertName;
    }

    public void setVpnCertName(String vpnCertName) {
        this.vpnCertName = vpnCertName;
    }

    public String getTunnelUuid() {
        return tunnelUuid;
    }

    public void setTunnelUuid(String tunnelUuid) {
        this.tunnelUuid = tunnelUuid;
    }

    public String getVpnCertUuid() {
        return vpnCertUuid;
    }

    public void setVpnCertUuid(String vpnCertUuid) {
        this.vpnCertUuid = vpnCertUuid;
    }

    public String getClientConf() {
        return clientConf;
    }

    public void setClientConf(String clientConf) {
        this.clientConf = clientConf;
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
