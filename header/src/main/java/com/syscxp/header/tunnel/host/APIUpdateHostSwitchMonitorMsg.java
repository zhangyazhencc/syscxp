package com.syscxp.header.tunnel.host;

import com.syscxp.header.host.HostConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.tunnel.MonitorConstant;
import com.syscxp.header.tunnel.TunnelConstant;
import com.syscxp.header.tunnel.switchs.PhysicalSwitchVO;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-11.
 * @Description: 更新监控主机与物理交换机关联.
 */

@Action(services = {TunnelConstant.ACTION_SERVICE}, category = HostConstant.ACTION_CATEGORY, names = {"update"}, adminOnly = true)
public class APIUpdateHostSwitchMonitorMsg extends APIMessage {

    @APIParam(emptyString = false,resourceType = HostSwitchMonitorVO.class)
    private String uuid;

    @APIParam(emptyString = false,resourceType = PhysicalSwitchVO.class)
    private String physicalSwitchUuid;

    @APIParam(emptyString = false,maxLength = 128)
    private String physicalSwitchPortName;

    @APIParam(emptyString = false,maxLength = 128)
    private String interfaceName;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPhysicalSwitchUuid() {
        return physicalSwitchUuid;
    }

    public void setPhysicalSwitchUuid(String physicalSwitchUuid) {
        this.physicalSwitchUuid = physicalSwitchUuid;
    }

    public String getPhysicalSwitchPortName() {
        return physicalSwitchPortName;
    }

    public void setPhysicalSwitchPortName(String physicalSwitchPortName) {
        this.physicalSwitchPortName = physicalSwitchPortName;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("Update HostSwitchMonitorVO")
                        .resource(uuid, HostSwitchMonitorVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
