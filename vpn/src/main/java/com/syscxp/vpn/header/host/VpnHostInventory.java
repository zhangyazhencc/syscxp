package com.syscxp.vpn.header.host;

import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = VpnHostVO.class)
public class VpnHostInventory {
    private String uuid;
    private String name;
    private String description;
    private String publicIface;
    private String publicIp;
    private String manageIp;
    private String sshPort;
    private String state;
    private String status;
    private String username;
    private String password;
    private ZoneInventory zoneInventory;
    private List<HostInterfaceInventory> hostInterfaceInventories;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static VpnHostInventory valueOf(VpnHostVO vo){
        VpnHostInventory inv = new VpnHostInventory();
        inv.setUuid(vo.getUuid());
        inv.setName(vo.getName());
        inv.setDescription(vo.getDescription());
        inv.setPublicIface(vo.getPublicInterface());
        inv.setPublicIp(vo.getPublicIp());
        inv.setManageIp(vo.getManageIp());
        inv.setSshPort(vo.getSshPort());
        inv.setUsername(vo.getUsername());
        inv.setPassword(vo.getPassword());
        inv.setState(vo.getState().toString());
        inv.setStatus(vo.getStatus().toString());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());
        inv.setZoneInventory(ZoneInventory.valueOf(vo.getZone()));
        inv.setHostInterfaceInventories(HostInterfaceInventory.valueOf(vo.getHostInterfaces()));
        return inv;
    }

    public static List<VpnHostInventory> valueOf(Collection<VpnHostVO> vos){
        List<VpnHostInventory> invs = new ArrayList<VpnHostInventory>(vos.size());
        for (VpnHostVO vo:vos){
            invs.add(VpnHostInventory.valueOf(vo));
        }
        return invs;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public String getPublicIface() {
        return publicIface;
    }

    public void setPublicIface(String publicIface) {
        this.publicIface = publicIface;
    }

    public String getManageIp() {
        return manageIp;
    }

    public void setManageIp(String manageIp) {
        this.manageIp = manageIp;
    }

    public String getSshPort() {
        return sshPort;
    }

    public void setSshPort(String sshPort) {
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
