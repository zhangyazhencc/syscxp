package org.zstack.tunnel.header.inventory;

import org.zstack.header.query.ExpandedQueries;
import org.zstack.header.query.ExpandedQuery;
import org.zstack.header.search.Inventory;
import org.zstack.tunnel.header.vo.SwitchVO;

import java.sql.Timestamp;

@Inventory(mappingVOClass = SwitchVO.class)
@ExpandedQueries({
        @ExpandedQuery(expandedField = "endpoint", inventoryClass = EndpointInventory.class,
                foreignKey = "uuid", expandedInventoryKey = "endpointUuid"),
})
public class SwitchInventory {

    private String uuid;
    private String endpointUuid;
    private String name;
    private String code;
    private String brand;
    private String model;
    private String subModel;
    private String upperType;
    private String owner;
    private String description;
    private Integer vlanBegin;
    private Integer vlanEnd;
    private String mIP;
    private String username;
    private String password;
    private Integer vxlanSupport;
    private String rack;
    private Integer enabled;
    private String status;
    private Integer isPrivate;
    private Integer deleted;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static SwitchInventory valueOf(SwitchVO vo){
        SwitchInventory inv = new SwitchInventory();
        inv.setUuid(vo.getUuid());
        inv.setEndpointUuid(vo.getEndpointUuid());
        inv.setName(vo.getName());
        inv.setCode(vo.getCode());
        inv.setBrand(vo.getBrand());
        inv.setModel(vo.getModel());
        inv.setSubModel(vo.getSubModel());
        inv.setUpperType(vo.getUpperType());
        inv.setOwner(vo.getOwner());
        inv.setDescription(vo.getDescription());
        inv.setVlanBegin(vo.getVlanBegin());
        inv.setVlanEnd(vo.getVlanEnd());
        inv.setmIP(vo.getmIP());
        inv.setUsername(vo.getUsername());
        inv.setVxlanSupport(vo.getVxlanSupport());
        inv.setRack(vo.getRack());
        inv.setEnabled(vo.getEnabled());
        inv.setIsPrivate(vo.getIsPrivate());
        inv.setStatus(vo.getStatus());
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
