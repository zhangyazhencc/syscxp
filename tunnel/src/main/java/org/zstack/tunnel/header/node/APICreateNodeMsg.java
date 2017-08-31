package org.zstack.tunnel.header.node;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

import java.util.List;

/**
 * Created by DCY on 8/21/17.
 */


public class APICreateNodeMsg extends APIMessage {

    @APIParam(nonempty = true,maxLength = 255)
    private String name;
    @APIParam(nonempty = true,maxLength = 128)
    private String code;
    @APIParam(nonempty = true)
    private double longtitude;
    @APIParam(nonempty = true)
    private double latitude;
    @APIParam(nonempty = true,maxLength = 128)
    private String property;
    @APIParam(nonempty = true,maxLength = 128)
    private String province;
    @APIParam(nonempty = true,maxLength = 128)
    private String city;
    @APIParam(nonempty = true,maxLength = 256)
    private String address;
    @APIParam(nonempty = true,maxLength = 128)
    private String contact;
    @APIParam(nonempty = true,maxLength = 32)
    private String telephone;
    @APIParam(nonempty = true,validValues = {"CLOSE", "OPEN","AVAILABLE"})
    private NodeStatus status;

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
}
