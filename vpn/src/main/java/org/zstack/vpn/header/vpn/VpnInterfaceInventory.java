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
    private String tunnelUuid;
    private Integer vlan;
    private String localIp;
    private String netmask;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static VpnInterfaceInventory valueOf(VpnInterfaceVO vo) {
        VpnInterfaceInventory inv = new VpnInterfaceInventory();
        inv.setUuid(vo.getUuid());
        inv.setVpnUuid(vo.getVpnUuid());
        inv.setName(vo.getName());
        inv.setTunnelUuid(vo.getNetworkUuid());
        inv.setVlan(vo.getVlan());
        inv.setNetmask(vo.getNetmask());
        inv.setLocalIp(vo.getLocalIp());
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

    public String getTunnelUuid() {
        return tunnelUuid;
    }

    public void setTunnelUuid(String tunnelUuid) {
        this.tunnelUuid = tunnelUuid;
    }

    public Integer getVlan() {
        return vlan;
    }

    public void setVlan(Integer vlan) {
        this.vlan = vlan;
    }

    public String getLocalIp() {
        return localIp;
    }

    public void setLocalIp(String localIp) {
        this.localIp = localIp;
    }

    public String getNetmask() {
        return netmask;
    }

    public void setNetmask(String netmask) {
        this.netmask = netmask;
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
