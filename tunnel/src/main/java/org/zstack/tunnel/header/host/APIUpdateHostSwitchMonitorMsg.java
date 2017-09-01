package org.zstack.tunnel.header.host;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

/**
 * Created by DCY on 2017-08-30
 */
public class APIUpdateHostSwitchMonitorMsg extends APIMessage {

    @APIParam(emptyString = false,resourceType = HostSwitchMonitorVO.class)
    private String targetUuid;

    @APIParam(emptyString = false,maxLength = 32)
    private String hostUuid;

    @APIParam(required = false,maxLength = 32)
    private String switchPortUuid;

    @APIParam(required = false,maxLength = 128)
    private String interfaceName;

    public String getTargetUuid() {
        return targetUuid;
    }

    public void setTargetUuid(String targetUuid) {
        this.targetUuid = targetUuid;
    }

    public String getHostUuid() {
        return hostUuid;
    }

    public void setHostUuid(String hostUuid) {
        this.hostUuid = hostUuid;
    }

    public String getSwitchPortUuid() {
        return switchPortUuid;
    }

    public void setSwitchPortUuid(String switchPortUuid) {
        this.switchPortUuid = switchPortUuid;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }
}
