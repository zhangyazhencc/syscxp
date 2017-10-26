package com.syscxp.header.host;

import com.syscxp.header.vo.ResourceVO;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 */
@MappedSuperclass
public class HostAO extends ResourceVO {

    @Column
    private String nodeUuid;

    @Column
    private String name;

    @Column
    private String code;

    @Column
    private String hostIp;

    @Column
    private String hostType;

    @Column
    private String position;

    @Column
    @Enumerated(EnumType.STRING)
    private HostState state;

    @Column
    @Enumerated(EnumType.STRING)
    private HostStatus status;

    @Column
    private Timestamp createDate;

    @Column
    private Timestamp lastOpDate;

    @PreUpdate
    private void preUpdate() {
        lastOpDate = null;
    }

    public HostAO() {
    }

    public HostAO(HostAO ao) {
        this.setUuid(ao.getUuid());
        this.setNodeUuid(ao.getNodeUuid());
        this.setPosition(ao.getPosition());
        this.setName(ao.getName());
        this.setCode(ao.getCode());
        this.setHostIp(ao.getHostIp());
        this.setHostType(ao.getHostType());
        this.setState(ao.getState());
        this.setStatus(ao.getStatus());
        this.setCreateDate(ao.getCreateDate());
        this.setLastOpDate(ao.getLastOpDate());
    }


    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getNodeUuid() {
        return nodeUuid;
    }

    public void setNodeUuid(String nodeUuid) {
        this.nodeUuid = nodeUuid;
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

    public String getHostIp() {
        return hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

    public String getHostType() {
        return hostType;
    }

    public void setHostType(String hostType) {
        this.hostType = hostType;
    }

    public HostState getState() {
        return state;
    }

    public void setState(HostState state) {
        this.state = state;
    }

    public HostStatus getStatus() {
        return status;
    }

    public void setStatus(HostStatus connectionState) {
        this.status = connectionState;
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
}
