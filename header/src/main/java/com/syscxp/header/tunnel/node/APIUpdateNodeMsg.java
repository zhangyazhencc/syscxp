package com.syscxp.header.tunnel.node;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.tunnel.NodeConstant;

/**
 * Created by DCY on 2017-08-21
 */
public class APIUpdateNodeMsg extends APIMessage {

    @APIParam(emptyString = false,resourceType = NodeVO.class)
    private String uuid;
    @APIParam(required = false,emptyString = false,maxLength = 255)
    private String name;
    @APIParam(required = false,emptyString = false,maxLength = 128)
    private String code;
    @APIParam(required = false,maxLength = 255)
    private String description;
    @APIParam(required = false)
    private Double longtitude;
    @APIParam(required = false)
    private Double latitude;
    @APIParam(required = false,emptyString = false,maxLength = 128)
    private String property;
    @APIParam(required = false,emptyString = false,maxLength = 128)
    private String country;
    @APIParam(required = false,emptyString = false,maxLength = 128)
    private String province;
    @APIParam(required = false,emptyString = false,maxLength = 128)
    private String city;
    @APIParam(required = false,emptyString = false,maxLength = 256)
    private String address;
    @APIParam(required = false,emptyString = false,maxLength = 128)
    private String contact;
    @APIParam(required = false,emptyString = false,maxLength = 32)
    private String telephone;
    @APIParam(required = false,emptyString = false,validValues = {"Close", "Open"})
    private NodeStatus status;
    @APIParam(required = false,maxLength = 32)
    private String extensionInfoUuid;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(Double longtitude) {
        this.longtitude = longtitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
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

    public NodeStatus getStatus() {
        return status;
    }

    public void setStatus(NodeStatus status) {
        this.status = status;
    }

    public String getExtensionInfoUuid() {
        return extensionInfoUuid;
    }

    public void setExtensionInfoUuid(String extensionInfoUuid) {
        this.extensionInfoUuid = extensionInfoUuid;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
