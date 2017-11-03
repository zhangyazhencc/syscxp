package com.syscxp.tunnel.header.monitor;

import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.tunnel.header.tunnel.TunnelVO;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-11.
 * @Description: 创建监控通道.
 */
public class APIRestartTunnelMonitorMsg extends APIMessage {
    // TODO:重启监控
        // 更新tunnelVO的monitorState
        // 下发控制器监控命令
        // ICMP同步

    @APIParam(emptyString = false,resourceType = TunnelVO.class,maxLength = 32)
    private String tunnelUuid;

    @APIParam(required = false,maxLength = 32)
    private String accountUuid;

    @APIParam(required = true,maxLength = 32)
    private String monitorCidr;

    @APIParam(required = false,maxLength = 1024)
    private String msg;

    public String getTunnelUuid() {
        return tunnelUuid;
    }

    public void setTunnelUuid(String tunnelUuid) {
        this.tunnelUuid = tunnelUuid;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMonitorCidr() {
        return monitorCidr;
    }

    public void setMonitorCidr(String monitorCidr) {
        this.monitorCidr = monitorCidr;
    }
}
