package com.syscxp.header.vpn.l3vpn;

import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = L3VpnVO.class)
public class L3VpnInventory {
    private String uuid;
    private String accountUuid;
    private String name;
    private String description;
    private Integer port;
    private Integer vlan;
    private String bandwidth;
    private String endpointUuid;
    private String l3NetworkUuid;
    private String workMode;
    private String startIp;
    private String stopIp;
    private String netmask;
    private String gateway;
    private String remoteIp;
    private String monitorIp;
    private String status;
    private String vpnCertUuid;
    private String vpnCertName;
    private String state;
    private Integer duration;
    private Timestamp expireDate;
    private Integer maxModifies;
    private String secretKey;
    private String clientConf;
    private String payment;
    private String vpnHostName;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static L3VpnInventory valueOf(L3VpnVO vo) {
        L3VpnInventory inv = new L3VpnInventory();
        inv.setUuid(vo.getUuid());
        inv.setAccountUuid(vo.getAccountUuid());
        inv.setName(vo.getName());
        inv.setDescription(vo.getDescription());
        inv.setBandwidth(vo.getBandwidthOfferingUuid());
        inv.setEndpointUuid(vo.getEndpointUuid());
        inv.setWorkMode(vo.getWorkMode());
        inv.setStartIp(vo.getStartIp());
        inv.setStopIp(vo.getStopIp());
        inv.setNetmask(vo.getNetmask());
        inv.setGateway(vo.getGateway());
        inv.setRemoteIp(vo.getRemoteIp());
        inv.setMonitorIp(vo.getMonitorIp());
        inv.setStatus(vo.getStatus().toString());
        inv.setState(vo.getState().toString());
        inv.setPort(vo.getPort());
        inv.setVlan(vo.getVlan());
        inv.setL3NetworkUuid(vo.getL3NetworkUuid());
        inv.setDuration(vo.getDuration());
        inv.setExpireDate(vo.getExpireDate());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());
        inv.setSecretKey(vo.getSecretKey());
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

    public static List<L3VpnInventory> valueOf(Collection<L3VpnVO> vos) {
        List<L3VpnInventory> invs = new ArrayList<>();
        for (L3VpnVO vo : vos) {
            invs.add(L3VpnInventory.valueOf(vo));
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

    public String getL3NetworkUuid() {
        return l3NetworkUuid;
    }

    public void setL3NetworkUuid(String l3NetworkUuid) {
        this.l3NetworkUuid = l3NetworkUuid;
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

    public String getWorkMode() { return workMode; }

    public void setWorkMode(String workMode) { this.workMode = workMode; }

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

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
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

    public String getStartIp() { return startIp; }

    public void setStartIp(String startIp) { this.startIp = startIp; }

    public String getStopIp() { return stopIp; }

    public void setStopIp(String stopIp) { this.stopIp = stopIp; }

    public String getNetmask() { return netmask; }

    public void setNetmask(String netmask) { this.netmask = netmask; }

    public String getGateway() { return gateway; }

    public void setGateway(String gateway) { this.gateway = gateway; }

    public String getRemoteIp() { return remoteIp; }

    public void setRemoteIp(String remoteIp) { this.remoteIp = remoteIp; }

    public String getMonitorIp() { return monitorIp; }

    public void setMonitorIp(String monitorIp) { this.monitorIp = monitorIp; }
}
