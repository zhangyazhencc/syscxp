package org.zstack.tunnel.header.monitor;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.tunnel.header.host.HostEO;
import org.zstack.tunnel.header.tunnel.TunnelEO;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-11.
 * @Description: 删除监控通道.
 */
public class APIDeleteTunnelMonitorMsg extends APIMessage {

    @APIParam(emptyString = false,resourceType = TunnelMonitorVO.class)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
