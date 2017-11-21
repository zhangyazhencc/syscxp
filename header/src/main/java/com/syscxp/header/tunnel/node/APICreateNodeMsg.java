package com.syscxp.header.tunnel.node;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.tunnel.NodeConstant;
import com.syscxp.header.tunnel.TunnelConstant;

/**
 * Created by DCY on 8/21/17.
 */

@Action(services = {TunnelConstant.ACTION_SERVICE}, category = NodeConstant.ACTION_CATEGORY, names = {"create"}, adminOnly = true)

public class APICreateNodeMsg extends APIMessage {

    @APIParam(emptyString = false,maxLength = 255)
    private String name;
    @APIParam(emptyString = false,maxLength = 128)
    private String code;
    @APIParam(required = false,maxLength = 255)
    private String description;
    private Double longtitude;
    private Double latitude;
    @APIParam(emptyString = false,maxLength = 128)
    private String property;
    @APIParam(emptyString = false,maxLength = 128)
    private String country;
    @APIParam(emptyString = false,maxLength = 128)
    private String province;
    @APIParam(emptyString = false,maxLength = 128)
    private String city;
    @APIParam(emptyString = false,maxLength = 256)
    private String address;
    @APIParam(emptyString = false,maxLength = 128)
    private String contact;
    @APIParam(emptyString = false,maxLength = 32)
    private String telephone;
    @APIParam(emptyString = false,validValues = {"Close", "Open"})
    private NodeStatus status;
    @APIParam(required = false,maxLength = 32)
    private String extensionInfoUuid;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
