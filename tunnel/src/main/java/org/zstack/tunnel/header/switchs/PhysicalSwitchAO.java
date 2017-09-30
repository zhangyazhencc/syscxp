package org.zstack.tunnel.header.switchs;

import org.zstack.header.vo.ForeignKey;
import org.zstack.tunnel.header.node.NodeEO;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by DCY on 2017-09-06
 */
@MappedSuperclass
public class PhysicalSwitchAO {
    @Id
    @Column
    private String uuid;

    @Column
    @ForeignKey(parentEntityClass = NodeEO.class, onDeleteAction = ForeignKey.ReferenceOption.SET_NULL)
    private String nodeUuid;

    @Column
    @ForeignKey(parentEntityClass = SwitchModelVO.class, onDeleteAction = ForeignKey.ReferenceOption.SET_NULL)
    private String switchModelUuid;

    @Column
    private String code;

    @Column
    private String name;

    @Column
    private String owner;

    @Column
    @Enumerated(EnumType.STRING)
    private PhysicalSwitchType type;

    @Column
    @Enumerated(EnumType.STRING)
    private PhysicalSwitchAccessType accessType;

    @Column
    private String rack;

    @Column
    private String description;

    @Column
    private String mIP;

    @Column
    private String localIP;

    @Column
    private String username;

    @Column
    private String password;

    @Column
    private Timestamp createDate;

    @Column
    private Timestamp lastOpDate;

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

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getRack() {
        return rack;
    }

    public void setRack(String rack) {
        this.rack = rack;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getmIP() {
        return mIP;
    }

    public void setmIP(String mIP) {
        this.mIP = mIP;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getSwitchModelUuid() {
        return switchModelUuid;
    }

    public void setSwitchModelUuid(String switchModelUuid) {
        this.switchModelUuid = switchModelUuid;
    }

    public String getNodeUuid() {
        return nodeUuid;
    }

    public void setNodeUuid(String nodeUuid) {
        this.nodeUuid = nodeUuid;
    }

    public PhysicalSwitchType getType() {
        return type;
    }

    public void setType(PhysicalSwitchType type) {
        this.type = type;
    }

    public String getLocalIP() {
        return localIP;
    }

    public void setLocalIP(String localIP) {
        this.localIP = localIP;
    }

    public PhysicalSwitchAccessType getAccessType() {
        return accessType;
    }

    public void setAccessType(PhysicalSwitchAccessType accessType) {
        this.accessType = accessType;
    }
}
