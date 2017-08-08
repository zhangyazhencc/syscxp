package org.zstack.tunnel.header.inventory;

import org.zstack.header.query.ExpandedQueries;
import org.zstack.header.query.ExpandedQuery;
import org.zstack.header.search.Inventory;
import org.zstack.tunnel.header.vo.SpeedRecordVO;

import java.sql.Timestamp;

@Inventory(mappingVOClass = SpeedRecordVO.class)
@ExpandedQueries({
        @ExpandedQuery(expandedField = "tunnel", inventoryClass = TunnelInventory.class,
                foreignKey = "uuid", expandedInventoryKey = "tunnelUuid"),
})
public class SpeedRecordInventory {

    private String uuid;
    private String tunnelUuid;
    private String protocol;
    private String duration;
    private Integer srcDirection;
    private Integer dstDirection;
    private Integer avgSpeed;
    private Integer minSpeed;
    private Integer maxSpeed;
    private Integer completed;
    private Integer deleted;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static SpeedRecordInventory valueOf(SpeedRecordVO vo){
        SpeedRecordInventory inv = new SpeedRecordInventory();
        inv.setUuid(vo.getUuid());
        inv.setTunnelUuid(vo.getTunnelUuid());
        inv.setProtocol(vo.getProtocol());
        inv.setDuration(vo.getDuration());
        inv.setSrcDirection(vo.getSrcDirection());
        inv.setDstDirection(vo.getDstDirection());
        inv.setAvgSpeed(vo.getAvgSpeed());
        inv.setMinSpeed(vo.getMinSpeed());
        inv.setMaxSpeed(vo.getMaxSpeed());
        inv.setCompleted(vo.getCompleted());
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

    public String getTunnelUuid() {
        return tunnelUuid;
    }

    public void setTunnelUuid(String tunnelUuid) {
        this.tunnelUuid = tunnelUuid;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Integer getSrcDirection() {
        return srcDirection;
    }

    public void setSrcDirection(Integer srcDirection) {
        this.srcDirection = srcDirection;
    }

    public Integer getDstDirection() {
        return dstDirection;
    }

    public void setDstDirection(Integer dstDirection) {
        this.dstDirection = dstDirection;
    }

    public Integer getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(Integer avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public Integer getMinSpeed() {
        return minSpeed;
    }

    public void setMinSpeed(Integer minSpeed) {
        this.minSpeed = minSpeed;
    }

    public Integer getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(Integer maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public Integer getCompleted() {
        return completed;
    }

    public void setCompleted(Integer completed) {
        this.completed = completed;
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
