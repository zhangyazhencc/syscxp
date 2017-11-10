package com.syscxp.header.tunnel.switchs;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.tunnel.SwitchConstant;

/**
 * Created by DCY on 2017-08-30
 */
@Action(category = SwitchConstant.ACTION_CATEGORY, names = {"create"}, adminOnly = true)

public class APICreateSwitchPortMsg extends APIMessage {

    @APIParam(emptyString = false,maxLength = 32,resourceType = SwitchVO.class)
    private String switchUuid;

    @APIParam(emptyString = false,maxLength = 128)
    private String portName;

    @APIParam(emptyString = false,validValues = {"RJ45_1G", "SFP_1G","SFP_10G","SHARE"})
    private SwitchPortType portType;

    @APIParam(emptyString = false)
    private String portAttribute;

    @APIParam(numberRange = {0, 1})
    private Integer autoAllot;


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

    public String getPortAttribute() {
        return portAttribute;
    }

    public void setPortAttribute(String portAttribute) {
        this.portAttribute = portAttribute;
    }

    public Integer getAutoAllot() {
        return autoAllot;
    }

    public void setAutoAllot(Integer autoAllot) {
        this.autoAllot = autoAllot;
    }
}
