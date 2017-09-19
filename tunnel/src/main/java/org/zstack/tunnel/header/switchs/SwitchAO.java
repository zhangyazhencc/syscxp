package org.zstack.tunnel.header.switchs;

import org.zstack.header.vo.ForeignKey;
import org.zstack.tunnel.header.endpoint.EndpointEO;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by DCY on 2017-08-24
 */
@MappedSuperclass
public class SwitchAO {

    @Id
    @Column
    private String uuid;

    @Column
    @ForeignKey(parentEntityClass = EndpointEO.class, onDeleteAction = ForeignKey.ReferenceOption.SET_NULL)
    private String endpointUuid;

    @Column
    private String code;

    @Column
    private String name;

    @Column
    @ForeignKey(parentEntityClass = PhysicalSwitchEO.class, onDeleteAction = ForeignKey.ReferenceOption.SET_NULL)
    private String physicalSwitchUuid;

    @Column
    @Enumerated(EnumType.STRING)
    private SwitchUpperType upperType;

    @Column
    private String description;

    @Column
    @Enumerated(EnumType.STRING)
    private SwitchState state;

    @Column
    @Enumerated(EnumType.STRING)
    private SwitchStatus status;

    @Column
    private Timestamp lastOpDate;

    @Column
    private Timestamp createDate;

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

    public String getEndpointUuid() {
        return endpointUuid;
    }

    public void setEndpointUuid(String endpointUuid) {
        this.endpointUuid = endpointUuid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhysicalSwitchUuid() {
        return physicalSwitchUuid;
    }

    public void setPhysicalSwitchUuid(String physicalSwitchUuid) {
        this.physicalSwitchUuid = physicalSwitchUuid;
    }

    public SwitchUpperType getUpperType() {
        return upperType;
    }

    public void setUpperType(SwitchUpperType upperType) {
        this.upperType = upperType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SwitchStatus getStatus() {
        return status;
    }

    public void setStatus(SwitchStatus status) {
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

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public SwitchState getState() {
        return state;
    }

    public void setState(SwitchState state) {
        this.state = state;
    }
}
