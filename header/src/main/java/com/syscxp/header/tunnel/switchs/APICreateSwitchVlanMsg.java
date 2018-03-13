package com.syscxp.header.tunnel.switchs;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.tunnel.SwitchConstant;
import com.syscxp.header.tunnel.TunnelConstant;

/**
 * Created by DCY on 2017-08-30
 */
@Action(services = {TunnelConstant.ACTION_SERVICE}, category = SwitchConstant.ACTION_CATEGORY, names = {"create"}, adminOnly = true)
public class APICreateSwitchVlanMsg extends APIMessage {

    @APIParam(emptyString = false,maxLength = 32,resourceType = SwitchVO.class)
    private String switchUuid;

    @APIParam(numberRange = {1, 4094})
    private Integer startVlan;

    @APIParam(numberRange = {1, 4094})
    private Integer endVlan;

    public String getSwitchUuid() {
        return switchUuid;
    }

    public void setSwitchUuid(String switchUuid) {
        this.switchUuid = switchUuid;
    }

    public Integer getStartVlan() {
        return startVlan;
    }

    public void setStartVlan(Integer startVlan) {
        this.startVlan = startVlan;
    }

    public Integer getEndVlan() {
        return endVlan;
    }

    public void setEndVlan(Integer endVlan) {
        this.endVlan = endVlan;
    }

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                String uuid = null;
                if (evt.isSuccess()) {
                    uuid = ((APICreateSwitchVlanEvent) evt).getInventory().getUuid();
                }
                ntfy("Create SwitchVlanVO")
                        .resource(uuid, SwitchVlanVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
