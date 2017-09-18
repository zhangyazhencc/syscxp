package org.zstack.vpn.header.gateway;

import org.zstack.header.search.Inventory;
import org.zstack.vpn.manage.HostState;
import org.zstack.vpn.manage.HostStatus;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = VpnVO.class)
public class VpnInventory {
    private String uuid;
    private String accountUuid;
    private String hostUuid;
    private String name;
    private String description;
    private String vpnCidr;
    private Integer bandwidth;
    private String endpointUuid;
    private HostStatus status;
    private HostState state;
    private Integer months;
    private Timestamp expiredDate;
    private List<VpnInterfaceInventory> tunnelIfaces;
    private List<VpnRouteInventory> vpnRoutes;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static VpnInventory valueOf(VpnVO vo) {
        VpnInventory inv = new VpnInventory();
        inv.setUuid(vo.getUuid());
        inv.setAccountUuid(vo.getAccountUuid());
        inv.setHostUuid(vo.getHostUuid());
        inv.setName(vo.getName());
        inv.setDescription(vo.getDescription());
        inv.setVpnCidr(vo.getVpnCidr());
        inv.setBandwidth(vo.getBandwidth());
        inv.setEndpointUuid(vo.getEndpointUuid());
        inv.setStatus(vo.getStatus());
        inv.setState(vo.getState());
        inv.setMonths(vo.getMonths());
        inv.setExpiredDate(vo.getExpiredDate());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());
        return inv;
    }

    public static List<VpnInventory> valueOf(Collection<VpnVO> vos) {
        List<VpnInventory> invs = new ArrayList<VpnInventory>();
        for (VpnVO vo : vos) {
            invs.add(VpnInventory.valueOf(vo));
        }

        return invs;
    }

    public HostState getState() {
        return state;
    }

    public void setState(HostState state) {
        this.state = state;
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

    public HostStatus getStatus() {
        return status;
    }

    public void setStatus(HostStatus status) {
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

    public List<VpnInterfaceInventory> getTunnelIfaces() {
        return tunnelIfaces;
    }

    public void setTunnelIfaces(List<VpnInterfaceInventory> tunnelIfaces) {
        this.tunnelIfaces = tunnelIfaces;
    }

    public List<VpnRouteInventory> getVpnRoutes() {
        return vpnRoutes;
    }

    public void setVpnRoutes(List<VpnRouteInventory> vpnRoutes) {
        this.vpnRoutes = vpnRoutes;
    }
}
