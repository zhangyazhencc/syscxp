package org.zstack.vpn.header.vpn;

import org.zstack.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = VpnRouteVO.class)
public class VpnRouteInventory {
    private String uuid;
    private String vpnUuid;
    private RouteType routeType;
    private List<String> nextInterface;
    private String targetCidr;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static VpnRouteInventory valueOf(VpnRouteVO vo) {
        VpnRouteInventory inv = new VpnRouteInventory();
        inv.setUuid(vo.getUuid());
        inv.setVpnUuid(vo.getVpnUuid());
        inv.setRouteType(vo.getRouteType());
        inv.setTargetCidr(vo.getTargetCidr());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());
        return inv;
    }

    public static List<VpnRouteInventory> valueOf(Collection<VpnRouteVO> vos) {
        List<VpnRouteInventory> invs = new ArrayList<VpnRouteInventory>();
        for (VpnRouteVO vo : vos) {
            invs.add(VpnRouteInventory.valueOf(vo));
        }

        return invs;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getVpnUuid() {
        return vpnUuid;
    }

    public void setVpnUuid(String vpnUuid) {
        this.vpnUuid = vpnUuid;
    }

    public RouteType getRouteType() {
        return routeType;
    }

    public void setRouteType(RouteType routeType) {
        this.routeType = routeType;
    }

    public List<String> getNextInterface() {
        return nextInterface;
    }

    public void setNextInterface(List<String> nextInterface) {
        this.nextInterface = nextInterface;
    }

    public String getTargetCidr() {
        return targetCidr;
    }

    public void setTargetCidr(String targetCidr) {
        this.targetCidr = targetCidr;
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
