package org.zstack.tunnel.header.monitor;

import org.zstack.header.identity.Action;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.tunnel.header.host.HostVO;
import org.zstack.tunnel.header.switchs.PhysicalSwitchVO;
import org.zstack.tunnel.manage.TunnelConstant;

import javax.persistence.Column;
import java.sql.Timestamp;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-11.
 * @Description: 创建监控主机与物理交换机关联.
 */
public class APICreateHostSwitchMonitorMsg extends APIMessage {

    @APIParam(emptyString = false,resourceType = HostVO.class)
    private String hostUuid;

    @APIParam(emptyString = false,resourceType = PhysicalSwitchVO.class)
    private String physicalSwitchUuid;

    @APIParam(emptyString = false,maxLength = 128)
    private String physicalSwitchPortName;

    @APIParam(emptyString = false,maxLength = 128)
    private String interfaceName;

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
