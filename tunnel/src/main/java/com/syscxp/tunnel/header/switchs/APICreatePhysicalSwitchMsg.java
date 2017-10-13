package com.syscxp.tunnel.header.switchs;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.tunnel.header.node.NodeVO;
import com.syscxp.tunnel.manage.SwitchConstant;

/**
 * Created by DCY on 2017-09-06
 */
@Action(category = SwitchConstant.ACTION_CATEGORY, names = {"create"}, adminOnly = true)
public class APICreatePhysicalSwitchMsg extends APIMessage {

    @APIParam(emptyString = false,maxLength = 32,resourceType = NodeVO.class)
    private String nodeUuid;
    @APIParam(emptyString = false,maxLength = 32,resourceType = SwitchModelVO.class)
    private String switchModelUuid;
    @APIParam(emptyString = false,maxLength = 128)
    private String code;
    @APIParam(emptyString = false,maxLength = 128)
    private String name;
    @APIParam(emptyString = false,maxLength = 128)
    private String owner;
    @APIParam(emptyString = false,validValues = {"ACCESS", "TRANSPORT","BOTH"})
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
    @APIParam(emptyString = false,required = false,maxLength = 255)
    private String description;

    @APIParam(emptyString = false,required = false,validValues = {"SDN", "MPLS"})
    private PhysicalSwitchAccessType accessType;
    @APIParam(emptyString = false,required = false,maxLength = 128)
    private String portName;
    @APIParam(emptyString = false,required = false,maxLength = 32,resourceType = PhysicalSwitchVO.class)
    private String uplinkPhysicalSwitchUuid;
    @APIParam(emptyString = false,required = false,maxLength = 128)
    private String uplinkPhysicalSwitchPortName;

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

    public PhysicalSwitchAccessType getAccessType() {
        return accessType;
    }

    public void setAccessType(PhysicalSwitchAccessType accessType) {
        this.accessType = accessType;
    }

    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public String getUplinkPhysicalSwitchUuid() {
        return uplinkPhysicalSwitchUuid;
    }

    public void setUplinkPhysicalSwitchUuid(String uplinkPhysicalSwitchUuid) {
        this.uplinkPhysicalSwitchUuid = uplinkPhysicalSwitchUuid;
    }

    public String getUplinkPhysicalSwitchPortName() {
        return uplinkPhysicalSwitchPortName;
    }

    public void setUplinkPhysicalSwitchPortName(String uplinkPhysicalSwitchPortName) {
        this.uplinkPhysicalSwitchPortName = uplinkPhysicalSwitchPortName;
    }
}
