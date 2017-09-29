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
    @APIParam(emptyString = false,required = false,maxLength = 32,resourceType = SwitchModelVO.class)
    private String switchModelUuid;
    @APIParam(emptyString = false,required = false,maxLength = 128)
    private String code;
    @APIParam(emptyString = false,required = false,maxLength = 128)
    private String name;
    @APIParam(emptyString = false,required = false,maxLength = 128)
    private String owner;
    @APIParam(emptyString = false,required = false,validValues = {"ACCESS", "TRANSPORT","BOTH"})
    private PhysicalSwitchType type;
    @APIParam(emptyString = false,required = false,maxLength = 32)
    private String rack;
    @APIParam(emptyString = false,required = false,maxLength = 128)
    private String mIP;
    @APIParam(emptyString = false,required = false,maxLength = 128)
    private String localIP;
    @APIParam(emptyString = false,required = false,maxLength = 128)
    private String username;
    @APIParam(emptyString = false,required = false,maxLength = 128)
    private String password;
    @APIParam(emptyString = false,required = false,maxLength = 255)
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

    public String getLocalIP() {
        return localIP;
    }

    public void setLocalIP(String localIP) {
        this.localIP = localIP;
    }

    public PhysicalSwitchType getType() {
        return type;
    }

    public void setType(PhysicalSwitchType type) {
        this.type = type;
    }
}
