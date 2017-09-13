package org.zstack.tunnel.header.node;

import org.zstack.header.identity.Action;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.tunnel.manage.NodeConstant;

/**
 * Created by DCY on 2017-08-21
 */
@Action(category = NodeConstant.ACTION_CATEGORY, names = {"update"}, adminOnly = true)

public class APIUpdateNodeMsg extends APIMessage {

    @APIParam(emptyString = false,resourceType = NodeVO.class)
    private String uuid;
    @APIParam(emptyString = false,maxLength = 255)
    private String name;
    @APIParam(emptyString = false,maxLength = 128)
    private String code;
    @APIParam(required = false,maxLength = 255)
    private String description;
    @APIParam(emptyString = false)
    private double longtitude;
    @APIParam(emptyString = false)
    private double latitude;
    @APIParam(emptyString = false,maxLength = 128)
    private String property;
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
    @APIParam(emptyString = false,validValues = {"CLOSE", "OPEN","AVAILABLE"})
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
}
