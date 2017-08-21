package org.zstack.tunnel.header.identity.node;

import org.zstack.header.search.Inventory;
import org.zstack.tunnel.header.identity.vo.NodeVO;

import java.sql.Timestamp;

@Inventory(mappingVOClass = NodeVO.class)
public class NodeInventory {

    private String uuid;
    private String name;
    private String code;
    private String extensionInfoUuid;
    private String description;
    private String contact;
    private String telephone;
    private String province;
    private String city;
    private double longtitude;
    private double latitude;
    private String property;
    private String status;
    private Integer deleted;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static NodeInventory valueOf(NodeVO vo){
        NodeInventory inv = new NodeInventory();
        inv.setUuid(vo.getUuid());
        inv.setName(vo.getName());
        inv.setCode(vo.getCode());
        inv.setExtensionInfoUuid(vo.getExtensionInfoUuid());
        inv.setDescription(vo.getDescription());
        inv.setContact(vo.getContact());
        inv.setTelephone(vo.getTelephone());
        inv.setProvince(vo.getProvince());
        inv.setCity(vo.getCity());
        inv.setLatitude(vo.getLatitude());
        inv.setLongtitude(vo.getLongtitude());
        inv.setProperty(vo.getProperty());
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

    public String getExtensionInfoUuid() {
        return extensionInfoUuid;
    }

    public void setExtensionInfoUuid(String extensionInfoUuid) {
        this.extensionInfoUuid = extensionInfoUuid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
