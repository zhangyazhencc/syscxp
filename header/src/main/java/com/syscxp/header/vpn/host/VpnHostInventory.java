package com.syscxp.header.vpn.host;

import com.syscxp.header.host.HostInventory;
import com.syscxp.header.search.Inventory;
import com.syscxp.header.search.Parent;
import com.syscxp.header.vpn.VpnConstant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = VpnHostVO.class, collectionValueOfMethod = "valueOf1",
        parent = {@Parent(inventoryClass = HostInventory.class, type = VpnConstant.HOST_TYPE)})
public class VpnHostInventory extends HostInventory{
    private String publicIp;
    private Integer sshPort;
    private String username;
    private Integer startPort;
    private Integer endPort;
    private String interfaceName;
    private ZoneInventory zoneInventory;
    private List<HostInterfaceInventory> hostInterfaceInventories;

    public VpnHostInventory(VpnHostVO vo) {
        super(vo);
        this.setPublicIp(vo.getPublicIp());
        this.setUsername(vo.getUsername());
        this.setSshPort(vo.getSshPort());
        this.setStartPort(vo.getStartPort());
        this.setEndPort(vo.getEndPort());
        this.setInterfaceName(vo.getInterfaceName());
        this.setZoneInventory(ZoneInventory.valueOf(vo.getZone()));
        this.setHostInterfaceInventories(HostInterfaceInventory.valueOf(vo.getHostInterfaces()));
    }

    public static VpnHostInventory valueOf(VpnHostVO vo){
        return new VpnHostInventory(vo);
    }

    public static List<VpnHostInventory> valueOf1(Collection<VpnHostVO> vos){
        List<VpnHostInventory> invs = new ArrayList<>(vos.size());
        for (VpnHostVO vo:vos){
            invs.add(VpnHostInventory.valueOf(vo));
        }
        return invs;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public Integer getStartPort() {
        return startPort;
    }

    public void setStartPort(Integer startPort) {
        this.startPort = startPort;
    }

    public Integer getEndPort() {
        return endPort;
    }

    public void setEndPort(Integer endPort) {
        this.endPort = endPort;
    }

    public List<HostInterfaceInventory> getHostInterfaceInventories() {
        return hostInterfaceInventories;
    }

    public void setHostInterfaceInventories(List<HostInterfaceInventory> hostInterfaceInventories) {
        this.hostInterfaceInventories = hostInterfaceInventories;
    }

    public ZoneInventory getZoneInventory() {
        return zoneInventory;
    }

    public void setZoneInventory(ZoneInventory zoneInventory) {
        this.zoneInventory = zoneInventory;
    }

    public String getPublicIp() {
        return publicIp;
    }

    public void setPublicIp(String publicIp) {
        this.publicIp = publicIp;
    }

    public Integer getSshPort() {
        return sshPort;
    }

    public void setSshPort(Integer sshPort) {
        this.sshPort = sshPort;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
