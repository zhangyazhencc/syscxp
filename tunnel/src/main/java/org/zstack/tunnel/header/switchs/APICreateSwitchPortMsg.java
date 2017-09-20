package org.zstack.tunnel.header.switchs;

import org.zstack.header.identity.Action;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.tunnel.manage.SwitchConstant;

/**
 * Created by DCY on 2017-08-30
 */
@Action(category = SwitchConstant.ACTION_CATEGORY, names = {"create"}, adminOnly = true)

public class APICreateSwitchPortMsg extends APIMessage {

    @APIParam(emptyString = false,maxLength = 32,resourceType = SwitchVO.class)
    private String switchUuid;

    @APIParam(emptyString = false,maxLength = 128)
    private String portName;

    @APIParam(emptyString = false,validValues = {"RJ45", "SFP_1G","SFG_10G"})
    private SwitchPortType portType;

    @APIParam(emptyString = false,validValues = {"Exclusive", "Shared"})
    private SwitchPortAttribute portAttribute;


    public String getSwitchUuid() {
        return switchUuid;
    }

    public void setSwitchUuid(String switchUuid) {
        this.switchUuid = switchUuid;
    }

    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public SwitchPortType getPortType() {
        return portType;
    }

    public void setPortType(SwitchPortType portType) {
        this.portType = portType;
    }

    public SwitchPortAttribute getPortAttribute() {
        return portAttribute;
    }

    public void setPortAttribute(SwitchPortAttribute portAttribute) {
        this.portAttribute = portAttribute;
    }
}
