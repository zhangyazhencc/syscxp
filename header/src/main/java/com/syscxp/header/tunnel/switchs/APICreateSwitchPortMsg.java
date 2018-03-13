package com.syscxp.header.tunnel.switchs;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.tunnel.SwitchConstant;
import com.syscxp.header.tunnel.TunnelConstant;
import com.syscxp.header.tunnel.tunnel.PortOfferingVO;

/**
 * Created by DCY on 2017-08-30
 */
@Action(services = {TunnelConstant.ACTION_SERVICE}, category = SwitchConstant.ACTION_CATEGORY, names = {"create"}, adminOnly = true)
public class APICreateSwitchPortMsg extends APIMessage {

    @APIParam(emptyString = false,maxLength = 32,resourceType = SwitchVO.class)
    private String switchUuid;

    @APIParam(emptyString = false,maxLength = 128)
    private String portName;

    @APIParam(emptyString = false,maxLength = 32,resourceType = PortOfferingVO.class)
    private String portOfferingUuid;

    @APIParam(emptyString = false, required = false)
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

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                String uuid = null;
                if (evt.isSuccess()) {
                    uuid = ((APICreateSwitchPortEvent) evt).getInventory().getUuid();
                }
                ntfy("Create SwitchPortVO")
                        .resource(uuid, SwitchPortVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
