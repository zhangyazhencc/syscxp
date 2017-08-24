package org.zstack.tunnel.header.node;

import org.zstack.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by DCY on 2017-08-21
 */
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
    private String address;
    private double longtitude;
    private double latitude;
    private List<NodeProperty> property;
    private NodeStatus status;
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
        inv.setAddress(vo.getAddress());
        inv.setLatitude(vo.getLatitude());
        inv.setLongtitude(vo.getLongtitude());
        inv.setProperty(vo.getProperty());
        inv.setStatus(vo.getStatus());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());
        return inv;
    }

    public static List<NodeInventory> valueOf(Collection<NodeVO> vos) {
        List<NodeInventory> lst = new ArrayList<NodeInventory>(vos.size());
        for (NodeVO vo : vos) {
            lst.add(NodeInventory.valueOf(vo));
        }
        return lst;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public List<NodeProperty> getProperty() {
        return property;
    }

    public void setProperty(List<NodeProperty> property) {
        this.property = property;
    }

    public NodeStatus getStatus() {
        return status;
    }

    public void setStatus(NodeStatus status) {
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
}