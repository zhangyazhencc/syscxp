package org.zstack.tunnel.header.monitor;

import org.zstack.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-18.
 * @Description: .
 */
@Inventory(mappingVOClass = SpeedRecordsVO.class)
public class SpeedRecordsInventory {
    private String uuid;
    private String tunnelUuid;
    private String srcHostUuid;
    private String srcMonitorIp;
    private String dstHostUuid;
    private String dstMonitorIp;
    private ProtocolType protocolType;
    private Integer duration;
    private Integer avgSpeed;
    private Integer maxSpeed;
    private Integer minSpeed;
    private Integer completed;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static SpeedRecordsInventory valueOf(SpeedRecordsVO vo){
        SpeedRecordsInventory inventory = new SpeedRecordsInventory();
        inventory.setUuid(vo.getUuid());
        inventory.setTunnelUuid(vo.getTunnelUuid());
        inventory.setSrcHostUuid(vo.getSrcHostUuid());
        inventory.setSrcMonitorIp(vo.getSrcMonitorIp());
        inventory.setDstHostUuid(vo.getDstHostUuid());
        inventory.setDstMonitorIp(vo.getDstMonitorIp());
        inventory.setProtocolType(vo.getProtocolType());
        inventory.setDuration(vo.getDuration());
        inventory.setAvgSpeed(vo.getAvgSpeed());
        inventory.setMaxSpeed(vo.getMaxSpeed());
        inventory.setMinSpeed(vo.getMinSpeed());
        inventory.setCompleted(vo.getCompleted());
        inventory.setLastOpDate(vo.getLastOpDate());
        inventory.setCreateDate(vo.getCreateDate());

        return  inventory;
    }

    public static List<SpeedRecordsInventory> valueOf(Collection<SpeedRecordsVO> vos) {
        List<SpeedRecordsInventory> lst = new ArrayList<SpeedRecordsInventory>(vos.size());
        for (SpeedRecordsVO vo : vos) {
            lst.add(SpeedRecordsInventory.valueOf(vo));
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

    public String getSrcHostUuid() {
        return srcHostUuid;
    }

    public void setSrcHostUuid(String srcHostUuid) {
        this.srcHostUuid = srcHostUuid;
    }

    public String getSrcMonitorIp() {
        return srcMonitorIp;
    }

    public void setSrcMonitorIp(String srcMonitorIp) {
        this.srcMonitorIp = srcMonitorIp;
    }

    public String getDstHostUuid() {
        return dstHostUuid;
    }

    public void setDstHostUuid(String dstHostUuid) {
        this.dstHostUuid = dstHostUuid;
    }

    public String getDstMonitorIp() {
        return dstMonitorIp;
    }

    public void setDstMonitorIp(String dstMonitorIp) {
        this.dstMonitorIp = dstMonitorIp;
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

    public Integer getCompleted() {
        return completed;
    }

    public void setCompleted(Integer completed) {
        this.completed = completed;
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
}
