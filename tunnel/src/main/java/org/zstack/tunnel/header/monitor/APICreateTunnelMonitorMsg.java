package org.zstack.tunnel.header.monitor;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.tunnel.header.host.HostEO;
import org.zstack.tunnel.header.host.HostVO;
import org.zstack.tunnel.header.tunnel.TunnelEO;
import org.zstack.tunnel.header.tunnel.TunnelVO;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-11.
 * @Description: 创建监控通道.
 */
public class APICreateTunnelMonitorMsg extends APIMessage {

    // @APIParam(emptyString = false,resourceType = TunnelVO.class)
    @APIParam(required = false,maxLength = 1024)
    private String tunnelUuid;

    @APIParam(required = false,maxLength = 1024)
    private String msg;

    public String getTunnelUuid() {
        return tunnelUuid;
    }

    public void setTunnelUuid(String tunnelUuid) {
        this.tunnelUuid = tunnelUuid;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
