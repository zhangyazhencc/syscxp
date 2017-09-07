package org.zstack.tunnel.header.host;

import org.zstack.header.identity.Action;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.tunnel.manage.TunnelConstant;

/**
 * Created by DCY on 2017-08-30
 */
@Action(category = TunnelConstant.ACTION_CATEGORY, names = {"host"}, adminOnly = true)
public class APIUpdateHostMonitorMsg extends APIMessage {

    @APIParam(emptyString = false,resourceType = HostMonitorVO.class)
    private String uuid;

    @APIParam(emptyString = false,maxLength = 32)
    private String hostUuid;

    @APIParam(required = false,maxLength = 32)
    private String switchPortUuid;

    @APIParam(required = false,maxLength = 128)
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
