package org.zstack.tunnel.header.vo;

import org.zstack.header.search.SqlTrigger;
import org.zstack.header.search.TriggerIndex;
import org.zstack.header.vo.ForeignKey;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table
@Inheritance(strategy= InheritanceType.JOINED)
@TriggerIndex
@SqlTrigger
public class TunnelMonitorVO {

    @Id
    @Column
    private String uuid;

    @Column
    @org.zstack.header.vo.ForeignKey(parentEntityClass = TunnelVO.class, parentKey = "uuid", onDeleteAction = ForeignKey.ReferenceOption.CASCADE)
    private String tunnelUuid;

    @Column
    @org.zstack.header.vo.ForeignKey(parentEntityClass = TunnelPointVO.class, parentKey = "uuid", onDeleteAction = ForeignKey.ReferenceOption.CASCADE)
    private String tunnelPointA;

    @Column
    @org.zstack.header.vo.ForeignKey(parentEntityClass = TunnelPointVO.class, parentKey = "uuid", onDeleteAction = ForeignKey.ReferenceOption.CASCADE)
    private String tunnelPointB;

    @Column
    private String monitorAIp;

    @Column
    private String monitorBIp;

    @Column
    private String status;

    @Column
    private String msg;

    @Column
    private Timestamp lastOpDate;

    @Column
    private Timestamp createDate;

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

    public String getTunnelPointA() {
        return tunnelPointA;
    }

    public void setTunnelPointA(String tunnelPointA) {
        this.tunnelPointA = tunnelPointA;
    }

    public String getTunnelPointB() {
        return tunnelPointB;
    }

    public void setTunnelPointB(String tunnelPointB) {
        this.tunnelPointB = tunnelPointB;
    }

    public String getMonitorAIp() {
        return monitorAIp;
    }

    public void setMonitorAIp(String monitorAIp) {
        this.monitorAIp = monitorAIp;
    }

    public String getMonitorBIp() {
        return monitorBIp;
    }

    public void setMonitorBIp(String monitorBIp) {
        this.monitorBIp = monitorBIp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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
