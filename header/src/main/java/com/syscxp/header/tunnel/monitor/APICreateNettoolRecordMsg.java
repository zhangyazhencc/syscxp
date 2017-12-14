package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.host.Host;
import com.syscxp.header.host.HostConstant;
import com.syscxp.header.host.HostVO;
import com.syscxp.header.identity.Action;
import com.syscxp.header.identity.SuppressCredentialCheck;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.tunnel.MonitorConstant;
import com.syscxp.header.tunnel.TunnelConstant;
import com.syscxp.header.tunnel.host.MonitorHostVO;
import com.syscxp.header.tunnel.node.NodeVO;
import com.syscxp.header.tunnel.tunnel.TunnelVO;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-11-13.
 * @Description: 网络工具测速.
 */

@SuppressCredentialCheck
public class APICreateNettoolRecordMsg extends APIMessage {

    @APIParam(validValues = {"ping", "trace"}, emptyString = false)
    private String command;

    @APIParam(emptyString = false,resourceType = MonitorHostVO.class)
    private String monitorHostUuid;

    @APIParam(emptyString = false)
    private String remoteIp;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getMonitorHostUuid() {
        return monitorHostUuid;
    }

    public void setMonitorHostUuid(String monitorHostUuid) {
        this.monitorHostUuid = monitorHostUuid;
    }

    public String getRemoteIp() {
        return remoteIp;
    }

    public void setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
    }
}
