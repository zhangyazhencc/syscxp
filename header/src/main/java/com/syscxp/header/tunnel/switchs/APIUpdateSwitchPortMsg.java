package com.syscxp.header.tunnel.switchs;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.tunnel.SwitchConstant;
import com.syscxp.header.tunnel.TunnelConstant;
import com.syscxp.header.tunnel.tunnel.PortOfferingVO;

/**
 * Created by DCY on 2017-09-13
 */
@Action(services = {TunnelConstant.ACTION_SERVICE}, category = SwitchConstant.ACTION_CATEGORY, names = {"update"}, adminOnly = true)
public class APIUpdateSwitchPortMsg extends APIMessage {

    @APIParam(emptyString = false,resourceType = SwitchPortVO.class)
    private String uuid;
    @APIParam(emptyString = false,required = false,validValues = {"Enabled", "Disabled"})
    private SwitchPortState state;
    @APIParam(required = false)
    private String portAttribute;
    @APIParam(required = false,numberRange = {0, 1})
    private Integer autoAllot;
    @APIParam(required = false,resourceType = PortOfferingVO.class)
    private String portOfferingUuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public SwitchPortState getState() {
        return state;
    }

    public void setState(SwitchPortState state) {
        this.state = state;
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

    public String getPortOfferingUuid() {
        return portOfferingUuid;
    }

    public void setPortOfferingUuid(String portOfferingUuid) {
        this.portOfferingUuid = portOfferingUuid;
    }
}
