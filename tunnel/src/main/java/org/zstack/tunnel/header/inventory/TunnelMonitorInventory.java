package org.zstack.tunnel.header.inventory;

import org.zstack.header.query.ExpandedQueries;
import org.zstack.header.query.ExpandedQuery;
import org.zstack.header.search.Inventory;
import org.zstack.tunnel.header.vo.TunnelMonitorVO;

import java.sql.Timestamp;

@Inventory(mappingVOClass = TunnelMonitorVO.class)
@ExpandedQueries({
        @ExpandedQuery(expandedField = "tunnel", inventoryClass = TunnelInventory.class,
                foreignKey = "uuid", expandedInventoryKey = "tunnelUuid"),
        @ExpandedQuery(expandedField = "tunnelPointA", inventoryClass = TunnelPointInventory.class,
                foreignKey = "uuid", expandedInventoryKey = "tunnelPointA", hidden = true),
        @ExpandedQuery(expandedField = "tunnelPointB", inventoryClass = TunnelPointInventory.class,
                foreignKey = "uuid", expandedInventoryKey = "tunnelPointB", hidden = true)
})
public class TunnelMonitorInventory {

    private String uuid;
    private String tunnelUuid;
    private String tunnelPointA;
    private String tunnelPointB;
    private String monitorAIp;
    private String monitorBIp;
    private String status;
    private String msg;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static TunnelMonitorInventory valueOf(TunnelMonitorVO vo) {
        TunnelMonitorInventory inv = new TunnelMonitorInventory();
        inv.setUuid(vo.getUuid());
        inv.setTunnelUuid(vo.getTunnelUuid());
        inv.setTunnelPointA(vo.getTunnelPointA());
        inv.setTunnelPointB(vo.getTunnelPointB());
        inv.setMonitorAIp(vo.getMonitorAIp());
        inv.setMonitorBIp(vo.getMonitorBIp());
        inv.setStatus(vo.getStatus());
        inv.setMsg(vo.getMsg());
        inv.setCreateDate(vo.getCreateDate());
        inv.setLastOpDate(vo.getLastOpDate());
        return inv;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTunnelUuid() {
        return tunnelUuid;
    }

    public void setTunnelUuid(String tunnelUuid) {
        this.tunnelUuid = tunnelUuid;
    }

    public String getTunnelPointA() {
        return tunnelPointA;
    }

    public void setTunnelPointA(String tunnelPointA) {
        this.tunnelPointA = tunnelPointA;
    }

    public String getTunnelPointB() {
        return tunnelPointB;
    }

    public void setTunnelPointB(String tunnelPointB) {
        this.tunnelPointB = tunnelPointB;
    }

    public String getMonitorAIp() {
        return monitorAIp;
    }

    public void setMonitorAIp(String monitorAIp) {
        this.monitorAIp = monitorAIp;
    }

    public String getMonitorBIp() {
        return monitorBIp;
    }

    public void setMonitorBIp(String monitorBIp) {
        this.monitorBIp = monitorBIp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
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
