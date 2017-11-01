package com.syscxp.tunnel.header.monitor;

import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.tunnel.header.host.MonitorHostVO;
import com.syscxp.tunnel.header.tunnel.TunnelVO;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-11.
 * @Description: 更新监控通道.
 */
public class APIUpdateTunnelMonitorMsg extends APIMessage {

    @APIParam(emptyString = false,resourceType = TunnelMonitorVO.class,checkAccount = true)
    private String uuid;

    @APIParam(emptyString = false,maxLength = 32)
    private String accountUuid;

    @APIParam(emptyString = false,maxLength = 32)
    private String monitorCidr;

    @APIParam(required = false,maxLength = 1024)
    private String msg;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getMonitorCidr() {
        return monitorCidr;
    }

    public void setMonitorCidr(String monitorCidr) {
        this.monitorCidr = monitorCidr;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }
}
