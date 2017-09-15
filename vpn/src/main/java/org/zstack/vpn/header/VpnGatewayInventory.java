package org.zstack.vpn.header;

import org.springframework.util.CollectionUtils;
import org.zstack.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = VpnGatewayVO.class)
public class VpnGatewayInventory {
    private String uuid;
    private String accountUuid;
    private String hostUuid;
    private String name;
    private String description;
    private String vpnCidr;
    private Integer bandwidth;
    private String endpointUuid;
    private VpnStatus status;
    private Integer months;
    private Timestamp expiredDate;
    private List<TunnelIfaceInventory> tunnelIfaces;
    private List<VpnRouteInventory> vpnRoutes;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static VpnGatewayInventory valueOf(VpnGatewayVO vo) {
        VpnGatewayInventory inv = new VpnGatewayInventory();
        inv.setUuid(vo.getUuid());
        inv.setAccountUuid(vo.getAccountUuid());
        inv.setHostUuid(vo.getHostUuid());
        inv.setName(vo.getName());
        inv.setDescription(vo.getDescription());
        inv.setVpnCidr(vo.getVpnCidr());
        inv.setBandwidth(vo.getBandwidth());
        inv.setEndpointUuid(vo.getEndpointUuid());
        inv.setStatus(vo.getStatus());
        inv.setMonths(vo.getMonths());
        inv.setExpiredDate(vo.getExpiredDate());
        if (!CollectionUtils.isEmpty(vo.getTunnelIfaces()))
            inv.setTunnelIfaces(TunnelIfaceInventory.valueOf(vo.getTunnelIfaces()));
        if (!CollectionUtils.isEmpty(vo.getTunnelIfaces()))
            inv.setTunnelIfaces(TunnelIfaceInventory.valueOf(vo.getTunnelIfaces()));
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());
        return inv;
    }

    public static List<VpnGatewayInventory> valueOf(Collection<VpnGatewayVO> vos) {
        List<VpnGatewayInventory> invs = new ArrayList<VpnGatewayInventory>();
        for (VpnGatewayVO vo : vos) {
            invs.add(VpnGatewayInventory.valueOf(vo));
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

    public List<TunnelIfaceInventory> getTunnelIfaces() {
        return tunnelIfaces;
    }

    public void setTunnelIfaces(List<TunnelIfaceInventory> tunnelIfaces) {
        this.tunnelIfaces = tunnelIfaces;
    }

    public List<VpnRouteInventory> getVpnRoutes() {
        return vpnRoutes;
    }

    public void setVpnRoutes(List<VpnRouteInventory> vpnRoutes) {
        this.vpnRoutes = vpnRoutes;
    }
}
