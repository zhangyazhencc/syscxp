package com.syscxp.tunnel.header.monitor;

import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.tunnel.header.host.HostVO;
import com.syscxp.tunnel.header.switchs.PhysicalSwitchVO;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-11.
 * @Description: 更新监控主机与物理交换机关联.
 */
public class APIUpdateHostSwitchMonitorMsg extends APIMessage {

    @APIParam(emptyString = false,resourceType = HostSwitchMonitorVO.class)
    private String uuid;

    @APIParam(emptyString = false,resourceType = HostVO.class)
    private String hostUuid;

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

    public String getHostUuid() {
        return hostUuid;
    }

    public void setHostUuid(String hostUuid) {
        this.hostUuid = hostUuid;
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
}
