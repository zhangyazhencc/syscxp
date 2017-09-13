package org.zstack.tunnel.header.switchs;

import org.zstack.header.identity.Action;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.tunnel.manage.SwitchConstant;

/**
 * Created by DCY on 2017-08-29
 */
@Action(category = SwitchConstant.ACTION_CATEGORY, names = {"create"}, adminOnly = true)

public class APICreateSwitchMsg extends APIMessage {

    @APIParam(emptyString = false,maxLength = 32)
    private String endpointUuid;
    @APIParam(emptyString = false,maxLength = 128)
    private String code;
    @APIParam(emptyString = false,maxLength = 128)
    private String name;
    @APIParam(emptyString = false,maxLength = 32)
    private String physicalSwitchUuid;
    @APIParam(emptyString = false,validValues = {"FABRIC", "INTERNET"})
    private SwitchUpperType upperType;
    @APIParam(required = false,maxLength = 255)
    private String description;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
