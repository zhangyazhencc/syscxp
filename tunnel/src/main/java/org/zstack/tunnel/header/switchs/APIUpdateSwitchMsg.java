package org.zstack.tunnel.header.switchs;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

/**
 * Created by DCY on 2017-08-29
 */
public class APIUpdateSwitchMsg extends APIMessage {

    @APIParam(emptyString = false,resourceType = SwitchVO.class)
    private String uuid;
    @APIParam(required = false,maxLength = 128)
    private String code;
    @APIParam(required = false,maxLength = 128)
    private String name;
    @APIParam(required = false,maxLength = 32)
    private String physicalSwitchUuid;
    @APIParam(required = false,validValues = {"FABRIC", "INTERNET"})
    private SwitchUpperType upperType;
    @APIParam(required = false)
    private Integer enabled;
    @APIParam(required = false,validValues = {"NORMAL", "UNUSUAL"})
    private SwitchStatus status;
    @APIParam(required = false)
    private Integer isPrivate;
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

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public SwitchStatus getStatus() {
        return status;
    }

    public void setStatus(SwitchStatus status) {
        this.status = status;
    }

    public Integer getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(Integer isPrivate) {
        this.isPrivate = isPrivate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
