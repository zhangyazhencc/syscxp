package org.zstack.tunnel.header.inventory;

import org.zstack.header.search.Inventory;
import org.zstack.tunnel.header.vo.HostVO;

import java.sql.Timestamp;

@Inventory(mappingVOClass = HostVO.class)
public class HostInventory {

    private String uuid;
    private String name;
    private String code;
    private String ip;
    private String username;
    private String password;
    private String monitorState;
    private String monitorStatus;
    private Integer deleted;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static HostInventory valueOf(HostVO vo) {
        HostInventory inv = new HostInventory();
        inv.setUuid(vo.getUuid());
        inv.setName(vo.getName());
        inv.setCode(vo.getCode());
        inv.setIp(vo.getIp());
        inv.setUsername(vo.getUsername());
        inv.setPassword(vo.getPassword());
        inv.setMonitorState(vo.getMonitorState());
        inv.setMonitorState(vo.getMonitorStatus());
        inv.setDeleted(vo.getDeleted());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());
        return inv;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
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

    public String getMonitorState() {
        return monitorState;
    }

    public void setMonitorState(String monitorState) {
        this.monitorState = monitorState;
    }

    public String getMonitorStatus() {
        return monitorStatus;
    }

    public void setMonitorStatus(String monitorStatus) {
        this.monitorStatus = monitorStatus;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
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
