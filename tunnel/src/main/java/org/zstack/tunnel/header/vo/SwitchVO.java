package org.zstack.tunnel.header.vo;

import org.zstack.header.search.SqlTrigger;
import org.zstack.header.search.TriggerIndex;
import org.zstack.header.vo.ForeignKey;
import org.zstack.tunnel.header.vo.EndpointVO;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table
@Inheritance(strategy= InheritanceType.JOINED)
@TriggerIndex
@SqlTrigger
public class SwitchVO {

    @Id
    @Column
    private String uuid;

    @Column
    @ForeignKey(parentEntityClass = EndpointVO.class, parentKey = "uuid", onDeleteAction = ForeignKey.ReferenceOption.CASCADE)
    private String endpointUuid;

    @Column
    private String name;

    @Column
    private String code;

    @Column
    private String brand;

    @Column
    private String model;

    @Column
    private String subModel;

    @Column
    private String upperType;

    @Column
    private String owner;

    @Column
    private String description;

    @Column
    private Integer vlanBegin;

    @Column
    private Integer vlanEnd;

    @Column
    private String mIP;

    @Column
    private String username;

    @Column
    private String password;

    @Column
    private Integer vxlanSupport;

    @Column
    private String rack;

    @Column
    private Integer enabled;

    @Column
    private String status;

    @Column
    private Integer isPrivate;

    @Column
    private Integer deleted;

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

    public String getEndpointUuid() {
        return endpointUuid;
    }

    public void setEndpointUuid(String endpointUuid) {
        this.endpointUuid = endpointUuid;
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

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSubModel() {
        return subModel;
    }

    public void setSubModel(String subModel) {
        this.subModel = subModel;
    }

    public String getUpperType() {
        return upperType;
    }

    public void setUpperType(String upperType) {
        this.upperType = upperType;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getVlanBegin() {
        return vlanBegin;
    }

    public void setVlanBegin(Integer vlanBegin) {
        this.vlanBegin = vlanBegin;
    }

    public Integer getVlanEnd() {
        return vlanEnd;
    }

    public void setVlanEnd(Integer vlanEnd) {
        this.vlanEnd = vlanEnd;
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

    public Integer getVxlanSupport() {
        return vxlanSupport;
    }

    public void setVxlanSupport(Integer vxlanSupport) {
        this.vxlanSupport = vxlanSupport;
    }

    public String getRack() {
        return rack;
    }

    public void setRack(String rack) {
        this.rack = rack;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(Integer isPrivate) {
        this.isPrivate = isPrivate;
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
