package org.zstack.vpn.header;

import org.zstack.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = VpnRouteVO.class)
public class VpnRouteInventory {
    private String uuid;
    private String gatewayUuid;
    private RouteType routeType;
    private String nextIfaceUuid;
    private String nextIfaceName;
    private String targetCidr;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static VpnRouteInventory valueOf(VpnRouteVO vo) {
        VpnRouteInventory inv = new VpnRouteInventory();
        inv.setUuid(vo.getUuid());
        inv.setGatewayUuid(vo.getGatewayUuid());
        inv.setRouteType(vo.getRouteType());
        inv.setNextIfaceName(vo.getNextIfaceName());
        inv.setNextIfaceUuid(vo.getNextIfaceUuid());
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

    public String getGatewayUuid() {
        return gatewayUuid;
    }

    public void setGatewayUuid(String gatewayUuid) {
        this.gatewayUuid = gatewayUuid;
    }

    public RouteType getRouteType() {
        return routeType;
    }

    public void setRouteType(RouteType routeType) {
        this.routeType = routeType;
    }

    public String getNextIfaceUuid() {
        return nextIfaceUuid;
    }

    public void setNextIfaceUuid(String nextIfaceUuid) {
        this.nextIfaceUuid = nextIfaceUuid;
    }

    public String getNextIfaceName() {
        return nextIfaceName;
    }

    public void setNextIfaceName(String nextIfaceName) {
        this.nextIfaceName = nextIfaceName;
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
