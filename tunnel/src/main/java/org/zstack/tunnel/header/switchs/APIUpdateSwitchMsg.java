package org.zstack.tunnel.header.switchs;

import org.zstack.header.identity.Action;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.tunnel.manage.SwitchConstant;

/**
 * Created by DCY on 2017-08-29
 */
@Action(category = SwitchConstant.ACTION_CATEGORY, names = {"update"}, adminOnly = true)

public class APIUpdateSwitchMsg extends APIMessage {

    @APIParam(emptyString = false,resourceType = SwitchVO.class)
    private String uuid;
    @APIParam(emptyString = false,required = false,maxLength = 128)
    private String code;
    @APIParam(emptyString = false,required = false,maxLength = 128)
    private String name;
    @APIParam(emptyString = false,required = false,maxLength = 32,resourceType = PhysicalSwitchVO.class)
    private String physicalSwitchUuid;
    @APIParam(emptyString = false,required = false,validValues = {"FABRIC", "INTERNET"})
    private SwitchUpperType upperType;
    @APIParam(emptyString = false,required = false,validValues = {"Enabled", "Disabled","PreMaintenance","Maintenance"})
    private SwitchState state;
    @APIParam(emptyString = false,required = false,validValues = {"Connecting", "Connected","Disconnected"})
    private SwitchStatus status;
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

    public String getPhysicalSwitchUuid() {
        return physicalSwitchUuid;
    }

    public void setPhysicalSwitchUuid(String physicalSwitchUuid) {
        this.physicalSwitchUuid = physicalSwitchUuid;
    }

    public SwitchUpperType getUpperType() {
        return upperType;
    }

    public void setUpperType(SwitchUpperType upperType) {
        this.upperType = upperType;
    }

    public SwitchStatus getStatus() {
        return status;
    }

    public void setStatus(SwitchStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SwitchState getState() {
        return state;
    }

    public void setState(SwitchState state) {
        this.state = state;
    }
}
