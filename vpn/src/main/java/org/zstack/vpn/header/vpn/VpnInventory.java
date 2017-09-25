package org.zstack.vpn.header.vpn;

import org.zstack.header.search.Inventory;

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
    private VpnStatus status;
    private VpnState state;
    private Integer duration;
    private Timestamp expiredDate;
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
        inv.setStatus(vo.getStatus());
        inv.setState(vo.getState());
        inv.setPort(vo.getPort());
        inv.setDuration(vo.getDuration());
        inv.setExpiredDate(vo.getExpiredDate());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());
        inv.setInterfaceInventories(VpnInterfaceInventory.valueOf(vo.getVpnInterfaces()));
        inv.setRouteInventories(VpnRouteInventory.valueOf(vo.getVpnRoutes()));
        return inv;
    }

    public static List<VpnInventory> valueOf(Collection<VpnVO> vos) {
        List<VpnInventory> invs = new ArrayList<VpnInventory>();
        for (VpnVO vo : vos) {
            invs.add(VpnInventory.valueOf(vo));
        }

        return invs;
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

    public VpnState getState() {
        return state;
    }

    public void setState(VpnState state) {
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

    public void setStatus(VpnStatus status) {
        this.status = status;
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
}
