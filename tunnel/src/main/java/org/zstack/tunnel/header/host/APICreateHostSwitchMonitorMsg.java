package org.zstack.tunnel.header.host;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

/**
 * Created by DCY on 2017-08-30
 */
public class APICreateHostSwitchMonitorMsg extends APIMessage {

    @APIParam(emptyString = false,maxLength = 32)
    private String hostUuid;

    @APIParam(emptyString = false,maxLength = 32)
    private String switchPortUuid;

    @APIParam(emptyString = false,maxLength = 128)
    private String interfaceName;

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
