package org.zstack.tunnel.header.switchs;

import org.zstack.header.identity.Action;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.tunnel.manage.SwitchConstant;

/**
 * Created by DCY on 2017-09-06
 */

@Action(category = SwitchConstant.ACTION_CATEGORY, names = {"update"}, adminOnly = true)

public class APIUpdatePhysicalSwitchMsg extends APIMessage {

    @APIParam(emptyString = false,resourceType = PhysicalSwitchVO.class)
    private String uuid;
    @APIParam(required = false,maxLength = 32)
    private String nodeUuid;
    @APIParam(required = false,maxLength = 32)
    private String switchModelUuid;
    @APIParam(required = false,maxLength = 128)
    private String code;
    @APIParam(required = false,maxLength = 128)
    private String name;
    @APIParam(required = false,maxLength = 128)
    private String brand;
    @APIParam(required = false,maxLength = 128)
    private String owner;
    @APIParam(required = false,maxLength = 32)
    private String rack;
    @APIParam(required = false,maxLength = 128)
    private String mIP;
    @APIParam(required = false,maxLength = 128)
    private String username;
    @APIParam(required = false,maxLength = 128)
    private String password;
    @APIParam(required = false,maxLength = 255)
    private String description;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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
}
