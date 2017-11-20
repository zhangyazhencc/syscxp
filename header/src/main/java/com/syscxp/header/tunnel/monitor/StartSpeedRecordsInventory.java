package com.syscxp.header.tunnel.monitor;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-18.
 * @Description: .
 */
public class StartSpeedRecordsInventory {
    private String uuid;
    private String tunnelUuid;
    private String srcTunnelMonitorUuid;
    private String dstTunnelMonitorUuid;
    private ProtocolType protocolType;
    private Integer duration;
    private Integer avgSpeed;
    private Integer maxSpeed;
    private Integer minSpeed;
    private SpeedRecordStatus status;
    private Timestamp lastOpDate;
    private Timestamp createDate;
    private String hostIp;

    public static StartSpeedRecordsInventory valueOf(SpeedRecordsVO vo, String hostIp){
        StartSpeedRecordsInventory inventory = new StartSpeedRecordsInventory();
        inventory.setUuid(vo.getUuid());
        inventory.setTunnelUuid(vo.getTunnelUuid());
        inventory.setSrcTunnelMonitorUuid(vo.getSrcTunnelMonitorUuid());
        inventory.setDstTunnelMonitorUuid(vo.getDstTunnelMonitorUuid());
        inventory.setProtocolType(vo.getProtocolType());
        inventory.setDuration(vo.getDuration());
        inventory.setAvgSpeed(vo.getAvgSpeed());
        inventory.setMaxSpeed(vo.getMaxSpeed());
        inventory.setMinSpeed(vo.getMinSpeed());
        inventory.setStatus(vo.getStatus());
        inventory.setLastOpDate(vo.getLastOpDate());
        inventory.setCreateDate(vo.getCreateDate());
        inventory.setHostIp(hostIp);

        return  inventory;
    }

    public static List<StartSpeedRecordsInventory> valueOf(Collection<SpeedRecordsVO> vos, String hostIp ) {
        List<StartSpeedRecordsInventory> lst = new ArrayList<StartSpeedRecordsInventory>(vos.size());
        for (SpeedRecordsVO vo : vos) {
            lst.add(StartSpeedRecordsInventory.valueOf(vo,hostIp));
        }
        return lst;
    }


    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    } public String getUuid() {
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

    public String getSrcTunnelMonitorUuid() {
        return srcTunnelMonitorUuid;
    }

    public void setSrcTunnelMonitorUuid(String srcTunnelMonitorUuid) {
        this.srcTunnelMonitorUuid = srcTunnelMonitorUuid;
    }

    public String getDstTunnelMonitorUuid() {
        return dstTunnelMonitorUuid;
    }

    public void setDstTunnelMonitorUuid(String dstTunnelMonitorUuid) {
        this.dstTunnelMonitorUuid = dstTunnelMonitorUuid;
    }

    public ProtocolType getProtocolType() {
        return protocolType;
    }

    public void setProtocolType(ProtocolType protocolType) {
        this.protocolType = protocolType;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(Integer avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public Integer getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(Integer maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public Integer getMinSpeed() {
        return minSpeed;
    }

    public void setMinSpeed(Integer minSpeed) {
        this.minSpeed = minSpeed;
    }

    public SpeedRecordStatus getStatus() {
        return status;
    }

    public void setStatus(SpeedRecordStatus status) {
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

    public String getHostIp() {
        return hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }
}
