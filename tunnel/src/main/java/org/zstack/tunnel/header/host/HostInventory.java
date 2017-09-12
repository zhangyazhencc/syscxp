package org.zstack.tunnel.header.host;

import org.zstack.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by DCY on 2017-08-30
 */
@Inventory(mappingVOClass = HostVO.class)
public class HostInventory {

    private String uuid;
    private String nodeUuid;
    private String name;
    private String code;
    private String hostIp;
    private String username;
    private String password;
    private HostState state;
    private String status;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static HostInventory valueOf(HostVO vo){
        HostInventory inv = new HostInventory();
        inv.setUuid(vo.getUuid());
        inv.setNodeUuid(vo.getNodeUuid());
        inv.setName(vo.getName());
        inv.setCode(vo.getCode());
        inv.setHostIp(vo.getHostIp());
        inv.setUsername(vo.getUsername());
        inv.setPassword(vo.getPassword());
        inv.setState(vo.getState());
        inv.setStatus(vo.getStatus());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());
        return inv;
    }

    public static List<HostInventory> valueOf(Collection<HostVO> vos) {
        List<HostInventory> lst = new ArrayList<HostInventory>(vos.size());
        for (HostVO vo : vos) {
            lst.add(HostInventory.valueOf(vo));
        }
        return lst;
    }

    public String getUuid() {
        return uuid;
    }

    public String getNodeUuid() {
        return nodeUuid;
    }

    public void setNodeUuid(String nodeUuid) {
        this.nodeUuid = nodeUuid;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHostIp() {
        return hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public HostState getState() {
        return state;
    }

    public void setState(HostState state) {
        this.state = state;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
