package com.syscxp.vpn.header.host;

import com.syscxp.header.host.HostInventory;
import com.syscxp.header.search.Inventory;
import com.syscxp.header.search.Parent;
import com.syscxp.vpn.vpn.VpnConstant;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = VpnHostVO.class, collectionValueOfMethod = "valueOf1",
        parent = {@Parent(inventoryClass = HostInventory.class, type = VpnConstant.HOST_TYPE)})
public class VpnHostInventory extends HostInventory{
    private String publicIface;
    private String publicIp;
    private Integer sshPort;
    private String username;
    private String password;
    private String vpnInterfaceName;
    private Integer startPort;
    private Integer endPort;
    private ZoneInventory zoneInventory;
    private List<HostInterfaceInventory> hostInterfaceInventories;

    public VpnHostInventory(VpnHostVO vo) {
        super(vo);
        this.setPublicIface(vo.getPublicInterface());
        this.setPublicIp(vo.getPublicIp());
        this.setUsername(vo.getUsername());
        this.setPassword(vo.getPassword());
        this.setSshPort(vo.getSshPort());
        this.setVpnInterfaceName(vo.getVpnInterfaceName());
        this.setStartPort(vo.getStartPort());
        this.setEndPort(vo.getEndPort());
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

    public String getVpnInterfaceName() {
        return vpnInterfaceName;
    }

    public void setVpnInterfaceName(String vpnInterfaceName) {
        this.vpnInterfaceName = vpnInterfaceName;
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

    public String getPublicIface() {
        return publicIface;
    }

    public void setPublicIface(String publicIface) {
        this.publicIface = publicIface;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
