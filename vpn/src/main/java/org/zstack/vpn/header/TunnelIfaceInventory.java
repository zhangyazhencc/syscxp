package org.zstack.vpn.header;

import org.zstack.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = TunnelIfaceVO.class)
public class TunnelIfaceInventory {
    private String uuid;
    private String gatewayUuid;
    private String name;
    private String description;
    private String tunnelUuid;
    private String tunnelName;
    private String serverIP;
    private String clientIP;
    private String mask;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static TunnelIfaceInventory valueOf(TunnelIfaceVO vo) {
        TunnelIfaceInventory inv = new TunnelIfaceInventory();
        inv.setUuid(vo.getUuid());
        inv.setGatewayUuid(vo.getGatewayUuid());
        inv.setName(vo.getName());
        inv.setDescription(vo.getDescription());
        inv.setTunnelUuid(vo.getTunnelUuid());
        inv.setTunnelName(vo.getTunnelName());
        inv.setServerIP(vo.getServerIP());
        inv.setClientIP(vo.getClientIP());
        inv.setMask(vo.getMask());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());
        return inv;
    }

    public static List<TunnelIfaceInventory> valueOf(Collection<TunnelIfaceVO> vos) {
        List<TunnelIfaceInventory> invs = new ArrayList<TunnelIfaceInventory>();
        for (TunnelIfaceVO vo : vos) {
            invs.add(TunnelIfaceInventory.valueOf(vo));
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

    public String getTunnelUuid() {
        return tunnelUuid;
    }

    public void setTunnelUuid(String tunnelUuid) {
        this.tunnelUuid = tunnelUuid;
    }

    public String getTunnelName() {
        return tunnelName;
    }

    public void setTunnelName(String tunnelName) {
        this.tunnelName = tunnelName;
    }

    public String getServerIP() {
        return serverIP;
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    public String getClientIP() {
        return clientIP;
    }

    public void setClientIP(String clientIP) {
        this.clientIP = clientIP;
    }

    public String getMask() {
        return mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
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
