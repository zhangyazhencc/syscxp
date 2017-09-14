package org.zstack.tunnel.header.monitor;

import org.zstack.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-14.
 * @Description: .
 */
@Inventory(mappingVOClass = TunnelMonitorVO.class)
public class TunnelMonitorInventory {

    private String uuid;

    private String tunnelUuid;

    private String hostAUuid;

    private String monitorAIp;

    private String hostZUuid;

    private String monitorZIp;

    private TunnelMonitorStatus status;

    private String msg;

    private Timestamp lastOpDate;

    private Timestamp createDate;

    public static TunnelMonitorInventory valueOf(TunnelMonitorVO vo){
        TunnelMonitorInventory inventory = new TunnelMonitorInventory();
        inventory.setUuid(vo.getUuid());
        inventory.setTunnelUuid(vo.getTunnelUuid());
        inventory.setHostAUuid(vo.getHostAUuid());
        inventory.setMonitorAIp(vo.getMonitorAIp());
        inventory.setHostZUuid(vo.getHostZUuid());
        inventory.setMonitorZIp(vo.getMonitorZIp());
        inventory.setStatus(vo.getStatus());
        inventory.setMsg(vo.getMsg());
        inventory.setLastOpDate(vo.getLastOpDate());
        inventory.setCreateDate(vo.getCreateDate());

        return inventory;
    }

    public static List<TunnelMonitorInventory> valueOf(Collection<TunnelMonitorVO> vos){
        List<TunnelMonitorInventory> lst = new ArrayList<TunnelMonitorInventory>(vos.size());
        for(TunnelMonitorVO vo:vos){
            lst.add(TunnelMonitorInventory.valueOf(vo));
        }

        return lst;
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

    public String getHostAUuid() {
        return hostAUuid;
    }

    public void setHostAUuid(String hostAUuid) {
        this.hostAUuid = hostAUuid;
    }

    public String getMonitorAIp() {
        return monitorAIp;
    }

    public void setMonitorAIp(String monitorAIp) {
        this.monitorAIp = monitorAIp;
    }

    public String getHostZUuid() {
        return hostZUuid;
    }

    public void setHostZUuid(String hostZUuid) {
        this.hostZUuid = hostZUuid;
    }

    public String getMonitorZIp() {
        return monitorZIp;
    }

    public void setMonitorZIp(String monitorZIp) {
        this.monitorZIp = monitorZIp;
    }

    public TunnelMonitorStatus getStatus() {
        return status;
    }

    public void setStatus(TunnelMonitorStatus status) {
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
