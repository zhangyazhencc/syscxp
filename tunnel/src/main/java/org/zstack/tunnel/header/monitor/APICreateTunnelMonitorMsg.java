package org.zstack.tunnel.header.monitor;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.tunnel.header.host.HostEO;
import org.zstack.tunnel.header.tunnel.TunnelEO;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-11.
 * @Description: .
 */
public class APICreateTunnelMonitorMsg extends APIMessage {

    @APIParam(emptyString = false,maxLength = 32,resourceType = TunnelEO.class)
    private String tunnelUuid;

    @APIParam(emptyString = false,maxLength = 32,resourceType = HostEO.class)
    private String hostAUuid;

    @APIParam(emptyString = false,maxLength = 64)
    private String monitorAIp;

    @APIParam(emptyString = false,maxLength = 32,resourceType = HostEO.class)
    private String hostZUuid;

    @APIParam(emptyString = false,maxLength = 32)
    private String monitorZIp;

    @APIParam(emptyString = false,maxLength = 32)
    private TunnelMonitorStatus status;

    @APIParam(required = false,maxLength = 32)
    private String msg;

    public String getTunnelUuid() {
        return tunnelUuid;
    }

    public void setTunnelUuid(String tunnelUuid) {
        this.tunnelUuid = tunnelUuid;
    }

    public String getHostAUuid() {
        return hostAUuid;
    }

    public void setHostAUuid(String hostAUuid) {
        this.hostAUuid = hostAUuid;
    }

    public String getMonitorAIp() {
        return monitorAIp;
    }

    public void setMonitorAIp(String monitorAIp) {
        this.monitorAIp = monitorAIp;
    }

    public String getHostZUuid() {
        return hostZUuid;
    }

    public void setHostZUuid(String hostZUuid) {
        this.hostZUuid = hostZUuid;
    }

    public String getMonitorZIp() {
        return monitorZIp;
    }

    public void setMonitorZIp(String monitorZIp) {
        this.monitorZIp = monitorZIp;
    }

    public TunnelMonitorStatus getStatus() {
        return status;
    }

    public void setStatus(TunnelMonitorStatus status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
