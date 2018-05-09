package com.syscxp.header.tunnel.tunnel;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Create by DCY on 2018/5/9
 */
@Entity
@Table
@Inheritance(strategy = InheritanceType.JOINED)
public class TEConfigVO {
    @Id
    @Column
    private String uuid;

    @Column
    private String tunnelUuid;

    @Column
    @Enumerated(EnumType.STRING)
    private TETraceType traceType;

    @Column
    private String source;

    @Column
    private String target;

    @Column
    private String inNodes;

    @Column
    private String exNodes;

    @Column
    private String blurryInNodes;

    @Column
    private String blurryExNodes;

    @Column
    private String connInNodes;

    @Column
    private String connExNodes;

    @Column
    private String command;

    @Column
    @Enumerated(EnumType.STRING)
    private TETraceStatus status;

    @Column
    private Integer isConnected;

    @Column
    private Timestamp createDate;

    @Column
    private Timestamp lastOpDate;

    @OneToMany(fetch= FetchType.EAGER)
    @JoinColumn(name = "teConfigUuid", insertable = false, updatable = false)
    private List<TETraceVO> teTraceVOS = new ArrayList<TETraceVO>();

    @PreUpdate
    private void preUpdate() {
        lastOpDate = null;
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

    public TETraceType getTraceType() {
        return traceType;
    }

    public void setTraceType(TETraceType traceType) {
        this.traceType = traceType;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getInNodes() {
        return inNodes;
    }

    public void setInNodes(String inNodes) {
        this.inNodes = inNodes;
    }

    public String getExNodes() {
        return exNodes;
    }

    public void setExNodes(String exNodes) {
        this.exNodes = exNodes;
    }

    public String getBlurryInNodes() {
        return blurryInNodes;
    }

    public void setBlurryInNodes(String blurryInNodes) {
        this.blurryInNodes = blurryInNodes;
    }

    public String getBlurryExNodes() {
        return blurryExNodes;
    }

    public void setBlurryExNodes(String blurryExNodes) {
        this.blurryExNodes = blurryExNodes;
    }

    public String getConnInNodes() {
        return connInNodes;
    }

    public void setConnInNodes(String connInNodes) {
        this.connInNodes = connInNodes;
    }

    public String getConnExNodes() {
        return connExNodes;
    }

    public void setConnExNodes(String connExNodes) {
        this.connExNodes = connExNodes;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public TETraceStatus getStatus() {
        return status;
    }

    public void setStatus(TETraceStatus status) {
        this.status = status;
    }

    public Integer getIsConnected() {
        return isConnected;
    }

    public void setIsConnected(Integer isConnected) {
        this.isConnected = isConnected;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public Timestamp getLastOpDate() {
        return lastOpDate;
    }

    public void setLastOpDate(Timestamp lastOpDate) {
        this.lastOpDate = lastOpDate;
    }

    public List<TETraceVO> getTeTraceVOS() {
        return teTraceVOS;
    }

    public void setTeTraceVOS(List<TETraceVO> teTraceVOS) {
        this.teTraceVOS = teTraceVOS;
    }
}
