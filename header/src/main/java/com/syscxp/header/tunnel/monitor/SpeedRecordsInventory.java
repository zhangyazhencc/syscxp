package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.query.ExpandedQueries;
import com.syscxp.header.query.ExpandedQuery;
import com.syscxp.header.search.Inventory;
import com.syscxp.header.tunnel.tunnel.TunnelEO;
import com.syscxp.header.tunnel.tunnel.TunnelInventory;

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
@ExpandedQueries({
        @ExpandedQuery(expandedField = "tunnel", inventoryClass = TunnelInventory.class,
                foreignKey = "tunnelUuid", expandedInventoryKey = "uuid"),

})
public class SpeedRecordsInventory {
    private String uuid;
    private String accountUuid;
    private String tunnelUuid;
    private String tunnelName;
    private String srcTunnelMonitorUuid;
    private String dstTunnelMonitorUuid;
    private String srcNodeUuid;
    private String srcNodeName;
    private String dstNodeUuid;
    private String dstNodeName;
    private String protocolType;
    private Integer duration;
    private Integer avgSpeed;
    private Integer maxSpeed;
    private Integer minSpeed;
    private String status;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static SpeedRecordsInventory valueOf(SpeedRecordsVO vo){
        SpeedRecordsInventory inventory = new SpeedRecordsInventory();
        inventory.setUuid(vo.getUuid());
        inventory.setAccountUuid(vo.getAccountUuid());
        inventory.setTunnelUuid(vo.getTunnelUuid());
        inventory.setSrcTunnelMonitorUuid(vo.getSrcTunnelMonitorUuid());
        inventory.setDstTunnelMonitorUuid(vo.getDstTunnelMonitorUuid());
        inventory.setProtocolType(vo.getProtocolType().toString());
        inventory.setDuration(vo.getDuration());
        inventory.setAvgSpeed(vo.getAvgSpeed());
        inventory.setMaxSpeed(vo.getMaxSpeed());
        inventory.setMinSpeed(vo.getMinSpeed());
        inventory.setStatus(vo.getStatus().toString());
        inventory.setLastOpDate(vo.getLastOpDate());
        inventory.setCreateDate(vo.getCreateDate());
        inventory.setTunnelName(vo.getTunnelEO().getName());
        inventory.setSrcNodeUuid(vo.getSrcNodeUuid());
        inventory.setSrcNodeName(vo.getSrcNodeEO().getName());
        inventory.setDstNodeUuid(vo.getDstNodeUuid());
        inventory.setDstNodeName(vo.getDstNodeEO().getName());

        return  inventory;
    }

    public static List<SpeedRecordsInventory> valueOf(Collection<SpeedRecordsVO> vos ) {
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

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
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

    public String getProtocolType() {
        return protocolType;
    }

    public void setProtocolType(String protocolType) {
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

    public String getTunnelName() {
        return tunnelName;
    }

    public void setTunnelName(String tunnelName) {
        this.tunnelName = tunnelName;
    }

    public String getSrcNodeUuid() {
        return srcNodeUuid;
    }

    public void setSrcNodeUuid(String srcNodeUuid) {
        this.srcNodeUuid = srcNodeUuid;
    }

    public String getSrcNodeName() {
        return srcNodeName;
    }

    public void setSrcNodeName(String srcNodeName) {
        this.srcNodeName = srcNodeName;
    }

    public String getDstNodeUuid() {
        return dstNodeUuid;
    }

    public void setDstNodeUuid(String dstNodeUuid) {
        this.dstNodeUuid = dstNodeUuid;
    }

    public String getDstNodeName() {
        return dstNodeName;
    }

    public void setDstNodeName(String dstNodeName) {
        this.dstNodeName = dstNodeName;
    }
}
