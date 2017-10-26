package com.syscxp.tunnel.header.switchs;

import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;
import com.syscxp.header.query.AutoQuery;

@AutoQuery(replyClass = APIQuerySwitchPortAvailableReply.class, inventoryClass = SwitchPortAvailableInventory.class)
public class APIQuerySwitchPortAvailableMsg extends APISyncCallMessage {
    @APIParam
    private String uuid;
    @APIParam(required = false)
    private String portName;
    @APIParam(required = false)
    private SwitchPortAttribute portAttribute;
    @APIParam(required = false)
    private SwitchPortType portType;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public SwitchPortAttribute getPortAttribute() {
        return portAttribute;
    }

    public void setPortAttribute(SwitchPortAttribute portAttribute) {
        this.portAttribute = portAttribute;
    }

    public SwitchPortType getPortType() {
        return portType;
    }

    public void setPortType(SwitchPortType portType) {
        this.portType = portType;
    }
}
