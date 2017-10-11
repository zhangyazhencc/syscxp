package com.syscxp.tunnel.header.endpoint;


import com.syscxp.header.vo.ForeignKey;
import com.syscxp.tunnel.header.node.NodeEO;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by DCY on 2017-08-23
 */
@MappedSuperclass
public class EndpointAO {

    @Id
    @Column
    private String uuid;

    @Column
    @ForeignKey(parentEntityClass = NodeEO.class, onDeleteAction = ForeignKey.ReferenceOption.SET_NULL)
    private String nodeUuid;

    @Column
    private String name;

    @Column
    private String code;

    @Column
    @Enumerated(EnumType.STRING)
    private EndpointType endpointType;

    @Column
    private Integer enabled;

    @Column
    private Integer openToCustomers;

    @Column
    private String description;

    @Column
    private Timestamp lastOpDate;

    @PreUpdate
    private void preUpdate() {
        lastOpDate = null;
    }

    @Column
    private Timestamp createDate;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getNodeUuid() {
        return nodeUuid;
    }

    public void setNodeUuid(String nodeUuid) {
        this.nodeUuid = nodeUuid;
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

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public Integer getOpenToCustomers() {
        return openToCustomers;
    }

    public void setOpenToCustomers(Integer openToCustomers) {
        this.openToCustomers = openToCustomers;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public EndpointType getEndpointType() {
        return endpointType;
    }

    public void setEndpointType(EndpointType endpointType) {
        this.endpointType = endpointType;
    }
}