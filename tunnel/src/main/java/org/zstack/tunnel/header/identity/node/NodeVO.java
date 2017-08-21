package org.zstack.tunnel.header.identity.node;

import org.zstack.header.search.SqlTrigger;
import org.zstack.header.search.TriggerIndex;

import javax.persistence.*;
import java.sql.Timestamp;
/**
 * Created by DCY on 2017-08-21
 */
@Entity
@Table
@Inheritance(strategy= InheritanceType.JOINED)
@TriggerIndex
@SqlTrigger
public class NodeVO {

    @Id
    @Column
    private String uuid;

    @Column
    private String name;

    @Column
    private String code;

    @Column
    private String extensionInfoUuid;

    @Column
    private String description;

    @Column
    private String contact;

    @Column
    private String telephone;

    @Column
    private String province;

    @Column
    private String city;

    @Column
    private String address;

    @Column
    private double longtitude;

    @Column
    private double latitude;

    @Column
    @Enumerated(EnumType.STRING)
    private NodeProperty property;

    @Column
    @Enumerated(EnumType.STRING)
    private NodeStatus status;

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

    public NodeProperty getProperty() {
        return property;
    }

    public void setProperty(NodeProperty property) {
        this.property = property;
    }

    public NodeStatus getStatus() {
        return status;
    }

    public void setStatus(NodeStatus status) {
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
