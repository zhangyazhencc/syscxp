package com.syscxp.tunnel.header.monitor;

import com.syscxp.header.host.HostVO;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.tunnel.header.tunnel.TunnelSwitchPortVO;
import com.syscxp.tunnel.header.tunnel.TunnelVO;

import javax.persistence.Column;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-11.
 * @Description: 创建监控通道.
 */
public class APIStartTunnelMonitorMsg extends APIMessage {

    @APIParam(emptyString = false,resourceType = TunnelVO.class,maxLength = 32)
    private String tunnelUuid;

    @APIParam(required = true,maxLength = 64)
    private String monitorCidr;

    @APIParam(required = true,maxLength = 64)
    private String startIp;

    @APIParam(required = true,maxLength = 64)
    private String endIp;

    @APIParam(required = false,maxLength = 1024)
    private String msg;

    public String getTunnelUuid() {
        return tunnelUuid;
    }

    public void setTunnelUuid(String tunnelUuid) {
        this.tunnelUuid = tunnelUuid;
    }

    public String getMonitorCidr() {
        return monitorCidr;
    }

    public void setMonitorCidr(String monitorCidr) {
        this.monitorCidr = monitorCidr;
    }

    public String getStartIp() {
        return startIp;
    }

    public void setStartIp(String startIp) {
        this.startIp = startIp;
    }

    public String getEndIp() {
        return endIp;
    }

    public void setEndIp(String endIp) {
        this.endIp = endIp;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
