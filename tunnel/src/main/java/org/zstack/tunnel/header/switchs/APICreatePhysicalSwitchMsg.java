package org.zstack.tunnel.header.switchs;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

/**
 * Created by DCY on 2017-09-06
 */
public class APICreatePhysicalSwitchMsg extends APIMessage {

    @APIParam(emptyString = false,maxLength = 32)
    private String nodeUuid;
    @APIParam(emptyString = false,maxLength = 32)
    private String switchModelUuid;
    @APIParam(emptyString = false,maxLength = 128)
    private String code;
    @APIParam(emptyString = false,maxLength = 128)
    private String name;
    @APIParam(emptyString = false,maxLength = 128)
    private String brand;
    @APIParam(emptyString = false,maxLength = 128)
    private String owner;
    @APIParam(emptyString = false,validValues = {"JOIN", "TRANSPORT"})
    private PhysicalSwitchType type;
    @APIParam(emptyString = false,maxLength = 32)
    private String rack;
    @APIParam(emptyString = false,maxLength = 128)
    private String mIP;
    @APIParam(emptyString = false,maxLength = 128)
    private String localIP;
    @APIParam(emptyString = false,maxLength = 128)
    private String username;
    @APIParam(emptyString = false,maxLength = 128)
    private String password;
    @APIParam(required = false,maxLength = 255)
    private String description;

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

    public String getSwitchModelUuid() {
        return switchModelUuid;
    }

    public void setSwitchModelUuid(String switchModelUuid) {
        this.switchModelUuid = switchModelUuid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNodeUuid() {
        return nodeUuid;
    }

    public void setNodeUuid(String nodeUuid) {
        this.nodeUuid = nodeUuid;
    }

    public PhysicalSwitchType getType() {
        return type;
    }

    public void setType(PhysicalSwitchType type) {
        this.type = type;
    }

    public String getLocalIP() {
        return localIP;
    }

    public void setLocalIP(String localIP) {
        this.localIP = localIP;
    }
}
