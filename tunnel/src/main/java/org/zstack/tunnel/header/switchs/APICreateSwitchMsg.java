package org.zstack.tunnel.header.switchs;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

/**
 * Created by DCY on 2017-08-29
 */
public class APICreateSwitchMsg extends APIMessage {

    @APIParam(nonempty = true,maxLength = 32)
    private String endpointUuid;
    @APIParam(nonempty = true,maxLength = 128)
    private String code;
    @APIParam(nonempty = true,maxLength = 128)
    private String name;
    @APIParam(nonempty = true,maxLength = 128)
    private String brand;
    @APIParam(nonempty = true,maxLength = 32)
    private String switchModelUuid;
    @APIParam(nonempty = true,validValues = {"FABRIC", "INTERNET"})
    private SwitchUpperType upperType;
    @APIParam(nonempty = true,maxLength = 128)
    private String owner;
    @APIParam(nonempty = true,maxLength = 32)
    private String rack;
    @APIParam(nonempty = true,maxLength = 128)
    private String mIP;
    @APIParam(nonempty = true,maxLength = 128)
    private String username;
    @APIParam(nonempty = true,maxLength = 128)
    private String password;
    @APIParam(nonempty = true)
    private Integer isPrivate;

    public String getEndpointUuid() {
        return endpointUuid;
    }

    public void setEndpointUuid(String endpointUuid) {
        this.endpointUuid = endpointUuid;
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

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getSwitchModelUuid() {
        return switchModelUuid;
    }

    public void setSwitchModelUuid(String switchModelUuid) {
        this.switchModelUuid = switchModelUuid;
    }

    public SwitchUpperType getUpperType() {
        return upperType;
    }

    public void setUpperType(SwitchUpperType upperType) {
        this.upperType = upperType;
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

    public Integer getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(Integer isPrivate) {
        this.isPrivate = isPrivate;
    }
}