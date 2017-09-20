package org.zstack.vpn.header.vpn;

import org.zstack.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = VpnInterfaceVO.class)
public class VpnInterfaceInventory {
    private String uuid;
    private String vpnUuid;
    private String name;
    private String description;
    private String tunnel;
    private String serverIP;
    private String clientIP;
    private String mask;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static VpnInterfaceInventory valueOf(VpnInterfaceVO vo) {
        VpnInterfaceInventory inv = new VpnInterfaceInventory();
        inv.setUuid(vo.getUuid());
        inv.setVpnUuid(vo.getVpnUuid());
        inv.setName(vo.getName());
        inv.setTunnel(vo.getTunnelUuid());
        inv.setServerIP(vo.getLocalIp());
        inv.setClientIP(vo.getRemoteIp());
        inv.setMask(vo.getNetmask());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());
        return inv;
    }

    public static List<VpnInterfaceInventory> valueOf(Collection<VpnInterfaceVO> vos) {
        List<VpnInterfaceInventory> invs = new ArrayList<VpnInterfaceInventory>();
        for (VpnInterfaceVO vo : vos) {
            invs.add(VpnInterfaceInventory.valueOf(vo));
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

    public String getTunnel() {
        return tunnel;
    }

    public void setTunnel(String tunnel) {
        this.tunnel = tunnel;
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
